package org.egov.pr.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.pr.service.EventService;
import org.egov.pr.util.ResponseInfoFactory;
import org.egov.pr.web.models.PrResponse;
import org.egov.pr.web.models.PrSearchCriteria;
import org.egov.pr.web.models.PublicRelationEvent;
import org.egov.pr.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
    @RequestMapping("/v1")
    public class EventController {

        private final ObjectMapper objectMapper;

        private final HttpServletRequest request;

        private final EventService eventService;

        private final ResponseInfoFactory responseInfoFactory;

    @Autowired
    public EventController(ObjectMapper objectMapper, HttpServletRequest request,
                                  EventService eventService, ResponseInfoFactory responseInfoFactory) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.eventService = eventService;
        this.responseInfoFactory = responseInfoFactory;
    }
    
    
    @RequestMapping(value="/_search", method = RequestMethod.POST)
    public ResponseEntity<PrResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                       @Valid @ModelAttribute PrSearchCriteria criteria){

        List<PublicRelationEvent> events = eventService.search(criteria,requestInfoWrapper.getRequestInfo());

       PrResponse response = PrResponse.builder().events(events).responseInfo(
                responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

/*

        @PostMapping("/_create")
        public ResponseEntity<TradeLicenseResponse> create(@Valid @RequestBody TradeLicenseRequest tradeLicenseRequest)
        {
             List<TradeLicense> licenses = prService.create(tradeLicenseRequest);
                TradeLicenseResponse response = TradeLicenseResponse.builder().licenses(licenses).responseInfo(
                    responseInfoFactory.createResponseInfoFromRequestInfo(tradeLicenseRequest.getRequestInfo(), true))
                    .build();
                return new ResponseEntity<>(response,HttpStatus.OK);
        }

    

        @RequestMapping(value="/_update", method = RequestMethod.POST)
        public ResponseEntity<TradeLicenseResponse> update(@Valid @RequestBody TradeLicenseRequest tradeLicenseRequest){
            List<TradeLicense> licenses = prService.update(tradeLicenseRequest);

            TradeLicenseResponse response = TradeLicenseResponse.builder().licenses(licenses).responseInfo(
                    responseInfoFactory.createResponseInfoFromRequestInfo(tradeLicenseRequest.getRequestInfo(), true))
                    .build();
            return new ResponseEntity<>(response, HttpStatus.OK);
            }
*/

    }
