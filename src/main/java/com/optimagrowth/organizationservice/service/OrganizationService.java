package com.optimagrowth.organizationservice.service;

import brave.ScopedSpan;
import brave.Tracer;
import com.optimagrowth.organizationservice.events.source.ActionEnum;
import com.optimagrowth.organizationservice.events.source.SimpleSourceBean;
import com.optimagrowth.organizationservice.model.Organization;
import com.optimagrowth.organizationservice.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    @Autowired
    private OrganizationRepository repository;

    @Autowired
    private SimpleSourceBean simpleSourceBean;

    @Autowired
    private Tracer tracer;

    public Organization findById(String organizationId) {
        Optional<Organization> opt = null;
        ScopedSpan newSpan = tracer.startScopedSpan("getOrgDBCall");
        try {
            opt = repository.findById(organizationId);
            //publish message
            simpleSourceBean.publishOrganizationChange(ActionEnum.GET, organizationId);
            if(!opt.isPresent()){
                String errMsg = String.format("Unable to find an organization with id %s", organizationId);
                logger.error(errMsg);
                throw new IllegalArgumentException(errMsg);
            }
            logger.debug("Retrieving Organization Info: " + opt.get().toString());
        } finally {
            newSpan.tag("peer.service", "postgres");
            newSpan.annotate("Client received");
            newSpan.finish();
        }

        return opt.get();
    }

    public Organization create(Organization organization){
        organization.setId(UUID.randomUUID().toString());
        organization = repository.save(organization);
        //publish message
        simpleSourceBean.publishOrganizationChange(ActionEnum.CREATED, organization.getId());
        return organization;

    }

    public void update(Organization organization){
        repository.save(organization);
        //publish message
        simpleSourceBean.publishOrganizationChange(ActionEnum.UPDATED, organization.getId());
    }

    public void delete(String organizationId){
        repository.deleteById(organizationId);
        //publish message
        simpleSourceBean.publishOrganizationChange(ActionEnum.DELETED, organizationId);
    }
}
