package org.egov.pr.repository;

import static org.egov.pr.util.PrConstants.ACTION_ADHOC;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pr.config.PrConfiguration;
import org.egov.pr.producer.Producer;
import org.egov.pr.repository.builder.PrQueryBuilder;
import org.egov.pr.repository.rowmapper.EventRowMapper;

import org.egov.pr.web.models.Document;
import org.egov.pr.web.models.PublicRelationEvent;

import org.egov.pr.web.models.User;
import org.egov.pr.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository
public class EventRepository {

    private JdbcTemplate jdbcTemplate;

    private PrQueryBuilder queryBuilder;

    private EventRowMapper rowMapper;

    private Producer producer;

    private PrConfiguration config;

    private WorkflowService workflowService;


    @Autowired
    public EventRepository(JdbcTemplate jdbcTemplate, PrQueryBuilder queryBuilder, EventRowMapper rowMapper,
                        Producer producer, PrConfiguration config, WorkflowService workflowService) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.producer = producer;
        this.config = config;
        this.workflowService = workflowService;
    }


	public List<PublicRelationEvent> getEvent(RequestInfo requestInfo) {
		  List<Object> preparedStmtList = new ArrayList<>();
	        String query = queryBuilder.getEventSearchQuery(preparedStmtList);
	        log.info("Query: " + query);
	        List<PublicRelationEvent> licenses =  jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
	        return licenses;
	}


   


}
