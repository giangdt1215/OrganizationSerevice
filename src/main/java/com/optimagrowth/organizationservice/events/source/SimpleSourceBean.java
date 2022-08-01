package com.optimagrowth.organizationservice.events.source;

import com.optimagrowth.organizationservice.events.model.OrganizationChangeModel;
import com.optimagrowth.organizationservice.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
public class SimpleSourceBean {

    private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

    //private final Source source;

    //public SimpleSourceBean(Source source){
    //    this.source = source;
    //}

    //@Autowired
    //private StreamBridge streamBridge;

    private KafkaTemplate<String, OrganizationChangeModel> kafkaTemplate;

    @Autowired
    public SimpleSourceBean(KafkaTemplate<String, OrganizationChangeModel> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

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
        //source.output().send(MessageBuilder.withPayload(change).build());
        ListenableFuture<SendResult<String, OrganizationChangeModel>> future =
                kafkaTemplate.send("orgChangeTopic", change);

        future.addCallback(new ListenableFutureCallback<SendResult<String, OrganizationChangeModel>>() {
            @Override
            public void onFailure(Throwable ex) {
                logger.warn("Unable to deliver orgChange message {}", ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, OrganizationChangeModel> result) {
                logger.info("OrgChange message delivered with offset {}", result.getRecordMetadata().offset());
            }
        });
    }
}
