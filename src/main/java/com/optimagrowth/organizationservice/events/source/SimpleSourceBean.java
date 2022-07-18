package com.optimagrowth.organizationservice.events.source;

import com.optimagrowth.organizationservice.events.model.OrganizationChangeModel;
import com.optimagrowth.organizationservice.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class SimpleSourceBean {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    private final Source source;

    public SimpleSourceBean(Source source){
        this.source = source;
    }

    //@Autowired
    //private StreamBridge streamBridge;

    public void publishOrganizationChange(ActionEnum action, String organizationId){
        logger.debug("Sending Kafka message {} for Organization Id: {}",
                        action, organizationId);

        OrganizationChangeModel change = new OrganizationChangeModel(
                OrganizationChangeModel.class.getTypeName(),
                action.toString(),
                organizationId,
                UserContext.getCorrelationId());

        //logger.debug("Initializing OrgChange: {}", change.toString());

        //String message = action.toString() + "|" + change.getOrganizationId();
        //streamBridge.send("orgChangeBinding-out-0", message);
        source.output().send(MessageBuilder.withPayload(change).build());
    }
}
