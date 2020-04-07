
package org.egov.pr.service;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pr.config.PrConfiguration;
import org.egov.pr.repository.EventRepository;
import org.egov.pr.service.notification.EditNotificationService;
import org.egov.pr.util.PrUtil;
import org.egov.pr.web.models.PrSearchCriteria;
import org.egov.pr.web.models.PublicRelationEvent;
import org.egov.pr.workflow.ActionValidator;
import org.egov.pr.workflow.PrWorkflowService;
import org.egov.pr.workflow.WorkflowIntegrator;
import org.egov.pr.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventService {
	
	private WorkflowIntegrator wfIntegrator;


    private UserService userService;

    private EventRepository repository;

    private ActionValidator actionValidator;

   

    private PrWorkflowService PrWorkflowService;


    private PrUtil util;

    private PrConfiguration config;

    private WorkflowService workflowService;

    private EditNotificationService  editNotificationService;

    @Autowired
    public EventService(WorkflowIntegrator wfIntegrator, 
                               UserService userService, EventRepository repository, ActionValidator actionValidator,
                                PrWorkflowService prWorkflowService,
                                PrUtil util, 
                               PrConfiguration config,EditNotificationService editNotificationService,WorkflowService workflowService) {
        this.wfIntegrator = wfIntegrator;

        this.userService = userService;
        this.repository = repository;
        this.actionValidator = actionValidator;
        
        this.PrWorkflowService = prWorkflowService;
 
        this.util = util;

        this.config = config;
        this.editNotificationService = editNotificationService;
        this.workflowService = workflowService;
    }

    public List<PublicRelationEvent> search(PrSearchCriteria criteria, RequestInfo requestInfo){
        List<PublicRelationEvent> pr;
       pr=repository.getEvent(requestInfo);
       
       return pr;
    }

	



}