package org.egov.pr.repository.builder;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.egov.pr.config.PrConfiguration;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PrQueryBuilder {

    private PrConfiguration config;

    @Autowired
    public PrQueryBuilder(PrConfiguration config) {
        this.config = config;
    }
    private static final String QUERY="select * from event";
    
	public String getEventSearchQuery(List<Object> preparedStmtList) {
		  String builder = QUERY;
		return builder;
	}


    



}
