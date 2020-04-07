package org.egov.pr.service.notification;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.egov.pr.util.NotificationUtil;
import org.egov.pr.web.models.Difference;
import org.egov.pr.web.models.PrRequest;
import org.egov.pr.web.models.PublicRelationEvent;
import org.egov.pr.web.models.SMSRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EditNotificationService {


    private NotificationUtil util;


    @Autowired
    public EditNotificationService(NotificationUtil util) {
        this.util = util;
    }

    public void sendEditNotification(PrRequest request, Map<String, Difference> diffMap) {
        List<SMSRequest> smsRequests = enrichSMSRequest(request, diffMap);
        util.sendSMS(smsRequests);
    }

    /**
     * Creates smsRequest for edits done in update
     * @param request The update Request
     * @param diffMap The map of id to Difference for each license
     * @return The smsRequest
     */
    private List<SMSRequest> enrichSMSRequest(PrRequest request, Map<String, Difference> diffMap) {
        List<SMSRequest> smsRequests = new LinkedList<>();
       /* String tenantId = request.getLicenses().get(0).getTenantId();
        String localizationMessages = util.getLocalizationMessages(tenantId, request.getRequestInfo());
        for (PublicRelation license : request.getLicenses()) {
            Difference diff = diffMap.get(license.getId());
            String message = util.getCustomizedMsg(diff, license, localizationMessages);
            if (StringUtils.isEmpty(message)) continue;

            Map<String, String> mobileNumberToOwner = new HashMap<>();

            license.getTradeLicenseDetail().getOwners().forEach(owner -> {
                if (owner.getMobileNumber() != null)
                    mobileNumberToOwner.put(owner.getMobileNumber(), owner.getName());
            });
            smsRequests.addAll(util.createSMSRequest(message, mobileNumberToOwner));
        }*/
        return smsRequests;
    }

}
