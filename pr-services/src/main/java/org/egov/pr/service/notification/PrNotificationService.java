package org.egov.pr.service.notification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pr.config.PrConfiguration;
import org.egov.pr.repository.ServiceRequestRepository;
import org.egov.pr.util.NotificationUtil;
import org.egov.pr.util.PrConstants;
import org.egov.pr.web.models.Action;
import org.egov.pr.web.models.ActionItem;
import org.egov.pr.web.models.Event;
import org.egov.pr.web.models.EventRequest;
import org.egov.pr.web.models.PrRequest;
import org.egov.pr.web.models.SMSRequest;
import org.egov.pr.web.models.Source;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class PrNotificationService {


    private PrConfiguration config;

    private ServiceRequestRepository serviceRequestRepository;

    private NotificationUtil util;


    @Autowired
    public PrNotificationService(PrConfiguration config, ServiceRequestRepository serviceRequestRepository, NotificationUtil util) {
        this.config = config;
        this.serviceRequestRepository = serviceRequestRepository;
        this.util = util;
    }

    /**
     * Creates and send the sms based on the tradeLicenseRequest
     * @param request The  listenend on the kafka topic
     */
    public void process(PrRequest request){
        List<SMSRequest> smsRequests = new LinkedList<>();
        if(null != config.getIsSMSEnabled()) {
        	if(config.getIsSMSEnabled()) {
                enrichSMSRequest(request,smsRequests);
                if(!CollectionUtils.isEmpty(smsRequests))
                	util.sendSMS(smsRequests);
        	}
        }
        if(null != config.getIsUserEventsNotificationEnabled()) {
        	if(config.getIsUserEventsNotificationEnabled()) {
        		//EventRequest eventRequest = getEvents(request);
//        		if(null != eventRequest)
//        			util.sendEventNotification(eventRequest);
        	}
        }
    }


    /**
     * Enriches the smsRequest with the customized messages
     * @param request The tradeLicenseRequest from kafka topic
     * @param smsRequests List of SMSRequets
     */
    private void enrichSMSRequest(PrRequest request,List<SMSRequest> smsRequests){
        String tenantId = null;
        String localizationMessages = util.getLocalizationMessages(tenantId,request.getRequestInfo());
//        for(TradeLicense license : request.getLicenses()){
//            String message = util.getCustomizedMsg(request.getRequestInfo(),license,localizationMessages);
//            if(message==null) continue;
//
//            Map<String,String > mobileNumberToOwner = new HashMap<>();
//
//            license.getTradeLicenseDetail().getOwners().forEach(owner -> {
//                if(owner.getMobileNumber()!=null)
//                    mobileNumberToOwner.put(owner.getMobileNumber(),owner.getName());
//            });
//            smsRequests.addAll(util.createSMSRequest(message,mobileNumberToOwner));
//        }
    }
    
   
    
    
    
    /**
     * Fetches UUIDs of CITIZENs based on the phone number.
     * 
     * @param mobileNumbers
     * @param requestInfo
     * @param tenantId
     * @return
     */
    private Map<String, String> fetchUserUUIDs(Set<String> mobileNumbers, RequestInfo requestInfo, String tenantId) {
    	Map<String, String> mapOfPhnoAndUUIDs = new HashMap<>();
    	StringBuilder uri = new StringBuilder();
    	uri.append(config.getUserHost()).append(config.getUserSearchEndpoint());
    	Map<String, Object> userSearchRequest = new HashMap<>();
    	userSearchRequest.put("RequestInfo", requestInfo);
		userSearchRequest.put("tenantId", tenantId);
		userSearchRequest.put("userType", "CITIZEN");
    	for(String mobileNo: mobileNumbers) {
    		userSearchRequest.put("userName", mobileNo);
    		try {
    			Object user = serviceRequestRepository.fetchResult(uri, userSearchRequest);
    			if(null != user) {
    				String uuid = JsonPath.read(user, "$.user[0].uuid");
    				mapOfPhnoAndUUIDs.put(mobileNo, uuid);
    			}else {
        			log.error("Service returned null while fetching user for username - "+mobileNo);
    			}
    		}catch(Exception e) {
    			log.error("Exception while fetching user for username - "+mobileNo);
    			log.error("Exception trace: ",e);
    			continue;
    		}
    	}
    	return mapOfPhnoAndUUIDs;
    }







}