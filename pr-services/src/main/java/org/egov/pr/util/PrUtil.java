package org.egov.pr.util;

import static org.egov.pr.util.PrConstants.ACCESSORIES_CATEGORY;
import static org.egov.pr.util.PrConstants.COMMON_MASTERS_MODULE;
import static org.egov.pr.util.PrConstants.OWNERSHIP_CATEGORY;
import static org.egov.pr.util.PrConstants.STRUCTURE_TYPE;
import static org.egov.pr.util.PrConstants.TRADE_LICENSE_MODULE;
import static org.egov.pr.util.PrConstants.TRADE_TYPE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.pr.config.PrConfiguration;
import org.egov.pr.repository.ServiceRequestRepository;
import org.egov.pr.web.models.AuditDetails;
import org.egov.pr.web.models.PrRequest;

import org.egov.pr.web.models.workflow.BusinessService;
import org.egov.pr.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PrUtil {

    private PrConfiguration config;

    private ServiceRequestRepository serviceRequestRepository;

    private WorkflowService workflowService;

    @Autowired
    public PrUtil(PrConfiguration config, ServiceRequestRepository serviceRequestRepository,
                     WorkflowService workflowService) {
        this.config = config;
        this.serviceRequestRepository = serviceRequestRepository;
        this.workflowService = workflowService;
    }



    /**
     * Method to return auditDetails for create/update flows
     *
     * @param by
     * @param isCreate
     * @return AuditDetails
     */
    public AuditDetails getAuditDetails(String by, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if(isCreate)
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time).build();
        else
            return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
    }


  
 

    /**
     * Creates request to search UOM from MDMS
     * @param requestInfo The requestInfo of the request
     * @param tenantId The tenantId of the tradeLicense
     * @return request to search UOM from MDMS
     */
    public List<ModuleDetail> getTradeModuleRequest() {

        // master details for TL module
        List<MasterDetail> tlMasterDetails = new ArrayList<>();

        // filter to only get code field from master data
        final String filterCode = "$.[?(@.active==true)].code";

        tlMasterDetails.add(MasterDetail.builder().name(TRADE_TYPE).build());
        tlMasterDetails.add(MasterDetail.builder().name(ACCESSORIES_CATEGORY).build());

        ModuleDetail tlModuleDtls = ModuleDetail.builder().masterDetails(tlMasterDetails)
                .moduleName(TRADE_LICENSE_MODULE).build();

        // master details for common-masters module
        List<MasterDetail> commonMasterDetails = new ArrayList<>();
        commonMasterDetails.add(MasterDetail.builder().name(OWNERSHIP_CATEGORY).filter(filterCode).build());
        commonMasterDetails.add(MasterDetail.builder().name(STRUCTURE_TYPE).filter(filterCode).build());
        ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMasterDetails)
                .moduleName(COMMON_MASTERS_MODULE).build();


        /*MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Arrays.asList(tlModuleDtls,commonMasterMDtl)).tenantId(tenantId)
                .build();*/

        return Arrays.asList(tlModuleDtls,commonMasterMDtl);

    }


    /**
     * Creates request to search UOM from MDMS
     * @return request to search UOM from MDMS
     */
    public ModuleDetail getTradeUomRequest() {

        // master details for TL module
        List<MasterDetail> tlMasterDetails = new ArrayList<>();

        // filter to only get code field from master data


        tlMasterDetails.add(MasterDetail.builder().name(TRADE_TYPE).build());
        tlMasterDetails.add(MasterDetail.builder().name(ACCESSORIES_CATEGORY).build());

        ModuleDetail tlModuleDtls = ModuleDetail.builder().masterDetails(tlMasterDetails)
                .moduleName(TRADE_LICENSE_MODULE).build();


        /*MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Collections.singletonList(tlModuleDtls)).tenantId(tenantId)
                .build();*/

        return tlModuleDtls;
    }


    /**
     * Returns the url for mdms search endpoint
     *
     * @return url for mdms search endpoint
     */
    public StringBuilder getMdmsSearchUrl() {
        return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());
    }


    

    /**
     * Creates request to search financialYear in mdms
     * @return MDMS request for financialYear
     */
    private ModuleDetail getFinancialYearRequest() {

        // master details for TL module
        List<MasterDetail> tlMasterDetails = new ArrayList<>();

        // filter to only get code field from master data

        final String filterCodeForUom = "$.[?(@.active==true && @.module=='TL')]";

        tlMasterDetails.add(MasterDetail.builder().name(PrConstants.MDMS_FINANCIALYEAR).filter(filterCodeForUom).build());

        ModuleDetail tlModuleDtls = ModuleDetail.builder().masterDetails(tlMasterDetails)
                .moduleName(PrConstants.MDMS_EGF_MASTER).build();


  /*      MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(Collections.singletonList(tlModuleDtls)).tenantId(tenantId)
                .build();*/

        return tlModuleDtls;
    }


    private MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo,String tenantId){
        ModuleDetail financialYearRequest = getFinancialYearRequest();
        List<ModuleDetail> tradeModuleRequest = getTradeModuleRequest();

        List<ModuleDetail> moduleDetails = new LinkedList<>();
        moduleDetails.add(financialYearRequest);
        moduleDetails.addAll(tradeModuleRequest);

        MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
                .build();

        MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
                .requestInfo(requestInfo).build();
        return mdmsCriteriaReq;
    }



    public Object mDMSCall(PrRequest tradeLicenseRequest){
        RequestInfo requestInfo = tradeLicenseRequest.getRequestInfo();
        String tenantId =null;
        		//tradeLicenseRequest.getLicenses().get(0).getTenantId();
        MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo,tenantId);
        Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
        return result;
    }


    




}
