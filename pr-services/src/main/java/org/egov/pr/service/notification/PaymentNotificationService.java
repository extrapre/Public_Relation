package org.egov.pr.service.notification;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pr.config.PrConfiguration;
import org.egov.pr.service.EventService;
import org.egov.pr.util.NotificationUtil;
import org.egov.pr.web.models.PublicRelationEvent;
import org.egov.pr.web.models.SMSRequest;
import org.egov.pr.web.models.PrSearchCriteria;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;


@Service
public class PaymentNotificationService {


    private PrConfiguration config;

    private EventService eventService;

    private NotificationUtil util;


    @Autowired
    public PaymentNotificationService(PrConfiguration config, EventService eventService,
                                      NotificationUtil util) {
        this.config = config;
        this.eventService = eventService;
        this.util = util;
    }





    final String tenantIdKey = "tenantId";

    final String businessServiceKey = "businessService";

    final String consumerCodeKey = "consumerCode";

    final String payerMobileNumberKey = "mobileNumber";

    final String paidByKey = "paidBy";

    final String amountPaidKey = "amountPaid";

    final String receiptNumberKey = "receiptNumber";


    /**
     * Generates sms from the input record and Sends smsRequest to SMSService
     * @param record The kafka message from receipt create topic
     */
    public void process(HashMap<String, Object> record){
        try{
            String jsonString = new JSONObject(record).toString();
            DocumentContext documentContext = JsonPath.parse(jsonString);
            Map<String,String> valMap = enrichValMap(documentContext);
            RequestInfo requestInfo = new RequestInfo();

           /* if(valMap.get(businessServiceKey).equalsIgnoreCase(config.getBusinessService())){
                TradeLicense license = getTradeLicenseFromConsumerCode(valMap.get(tenantIdKey),valMap.get(consumerCodeKey),
                                                                       requestInfo);
                String localizationMessages = util.getLocalizationMessages(license.getTenantId(),requestInfo);
                List<SMSRequest> smsRequests = getSMSRequests(license,valMap,localizationMessages);
                util.sendSMS(smsRequests);
            }*/
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Creates the SMSRequest
     * @param license The TradeLicense for which the receipt is generated
     * @param valMap The valMap containing the values from receipt
     * @param localizationMessages The localization message to be sent
     * @return
     */
    private List<SMSRequest> getSMSRequests(PublicRelationEvent license, Map<String,String> valMap,String localizationMessages){
            List<SMSRequest> ownersSMSRequest = getOwnerSMSRequest(license,valMap,localizationMessages);
            SMSRequest payerSMSRequest = getPayerSMSRequest(license,valMap,localizationMessages);

            List<SMSRequest> totalSMS = new LinkedList<>();
            totalSMS.addAll(ownersSMSRequest);
            totalSMS.add(payerSMSRequest);

            return totalSMS;
    }


    /**
     * Creates SMSRequest for the owners
     * @param license The tradeLicense for which the receipt is created
     * @param valMap The Map containing the values from receipt
     * @param localizationMessages The localization message to be sent
     * @return The list of the SMS Requests
     */
    private List<SMSRequest> getOwnerSMSRequest(PublicRelationEvent license, Map<String,String> valMap,String localizationMessages){
        String message = util.getOwnerPaymentMsg(license,valMap,localizationMessages);
        List<SMSRequest> smsRequests = new LinkedList<>();
/*
        HashMap<String,String> mobileNumberToOwnerName = new HashMap<>();
        license.getTradeLicenseDetail().getOwners().forEach(owner -> {
            if(owner.getMobileNumber()!=null)
                mobileNumberToOwnerName.put(owner.getMobileNumber(),owner.getName());
        });

       

        for(Map.Entry<String,String> entrySet : mobileNumberToOwnerName.entrySet()){
            String customizedMsg = message.replace("<1>",entrySet.getValue());
            smsRequests.add(new SMSRequest(entrySet.getKey(),customizedMsg));
        }*/
        return smsRequests;
    }


    /**
     * Creates SMSRequest to be send to the payer
     * @param valMap The Map containing the values from receipt
     * @param localizationMessages The localization message to be sent
     * @return
     */
    private SMSRequest getPayerSMSRequest(PublicRelationEvent license,Map<String,String> valMap,String localizationMessages){
        String message = util.getPayerPaymentMsg(license,valMap,localizationMessages);
        String customizedMsg = message.replace("<1>",valMap.get(paidByKey));
        SMSRequest smsRequest = new SMSRequest(valMap.get(payerMobileNumberKey),customizedMsg);
        return smsRequest;
    }


    /**
     * Enriches the map with values from receipt
     * @param context The documentContext of the receipt
     * @return The map containing required fields from receipt
     */
    private Map<String,String> enrichValMap(DocumentContext context){
        Map<String,String> valMap = new HashMap<>();
        try{
            valMap.put(businessServiceKey,context.read("$.Receipt[0].Bill[0].billDetails[0].businessService"));
            valMap.put(consumerCodeKey,context.read("$.Receipt[0].Bill[0].billDetails[0].consumerCode"));
            valMap.put(tenantIdKey,context.read("$.Receipt[0].tenantId"));
            valMap.put(payerMobileNumberKey,context.read("$.Receipt[0].Bill[0].mobileNumber"));
            valMap.put(paidByKey,context.read("$.Receipt[0].Bill[0].paidBy"));
            Integer amountPaid = context.read("$.Receipt[0].instrument.amount");
            valMap.put(amountPaidKey,amountPaid.toString());
            valMap.put(receiptNumberKey,context.read("$.Receipt[0].Bill[0].billDetails[0].receiptNumber"));

        }
        catch (Exception e){
            e.printStackTrace();
            throw new CustomException("RECEIPT ERROR","Unable to fetch values from receipt");
        }
        return valMap;
    }


    /**
     * Searches the tradeLicense based on the consumer code as applicationNumber
     * @param tenantId tenantId of the tradeLicense
     * @param consumerCode The consumerCode of the receipt
     * @param requestInfo The requestInfo of the request
     * @return TradeLicense for the particular consumerCode
     */
    private PublicRelationEvent getTradeLicenseFromConsumerCode(String tenantId,String consumerCode,RequestInfo requestInfo){

        PrSearchCriteria searchCriteria = new PrSearchCriteria();
        searchCriteria.setApplicationNumber(consumerCode);
        searchCriteria.setTenantId(tenantId);
        List<PublicRelationEvent> licenses = null;
        		//prService.getLicensesWithOwnerInfo(searchCriteria,requestInfo);

        if(CollectionUtils.isEmpty(licenses))
            throw new CustomException("INVALID RECEIPT","No license found for the consumerCode: "
                    +consumerCode+" and tenantId: "+tenantId);

        if(licenses.size()!=1)
            throw new CustomException("INVALID RECEIPT","Multiple license found for the consumerCode: "
                    +consumerCode+" and tenantId: "+tenantId);

        return licenses.get(0);

    }







}
