package org.egov.pr.workflow;

import java.util.Map;

import org.egov.pr.config.PrConfiguration;
import org.egov.pr.producer.Producer;
import org.egov.pr.web.models.PrRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class PrWorkflowService {

    private ActionValidator actionValidator;
    private Producer producer;
    private PrConfiguration config;
    private WorkflowConfig workflowConfig;

    @Autowired
    public PrWorkflowService(ActionValidator actionValidator, Producer producer, PrConfiguration config,WorkflowConfig workflowConfig) {
        this.actionValidator = actionValidator;
        this.producer = producer;
        this.config = config;
        this.workflowConfig = workflowConfig;
    }


    /**
     * Validates and updates the status
     * @param request The update pr Request
     */
    public void updateStatus(PrRequest request){
        actionValidator.validateUpdateRequest(request,null);
        changeStatus(request);
    }


    /**
     * Changes the status of the pr according to action status mapping
     * @param request The update PrRequest
     */
    private void changeStatus(PrRequest request){
       Map<String,String> actionToStatus =  workflowConfig.getActionStatusMap();
       /*request.getLicenses().forEach(license -> {
             license.setStatus(actionToStatus.get(license.getAction()));
             if(license.getAction().equalsIgnoreCase(ACTION_APPROVE)){
                 Long time = System.currentTimeMillis();
                 license.setIssuedDate(time);
                 license.setValidFrom(time);
             }
       });*/
    }

}
