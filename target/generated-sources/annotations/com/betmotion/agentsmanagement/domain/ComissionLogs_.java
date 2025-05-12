package com.betmotion.agentsmanagement.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ComissionLogs.class)
public abstract class ComissionLogs_ {

	public static volatile SingularAttribute<ComissionLogs, LocalDateTime> date;
	public static volatile SingularAttribute<ComissionLogs, Integer> agentId;
	public static volatile SingularAttribute<ComissionLogs, BigDecimal> commissionSports;
	public static volatile SingularAttribute<ComissionLogs, Integer> parentAgentId;
	public static volatile SingularAttribute<ComissionLogs, BigDecimal> commissionSlots;
	public static volatile SingularAttribute<ComissionLogs, BigDecimal> commission;
	public static volatile SingularAttribute<ComissionLogs, Integer> id;
	public static volatile SingularAttribute<ComissionLogs, BigDecimal> commissionCasino;
	public static volatile SingularAttribute<ComissionLogs, String> commissionType;

	public static final String DATE = "date";
	public static final String AGENT_ID = "agentId";
	public static final String COMMISSION_SPORTS = "commissionSports";
	public static final String PARENT_AGENT_ID = "parentAgentId";
	public static final String COMMISSION_SLOTS = "commissionSlots";
	public static final String COMMISSION = "commission";
	public static final String ID = "id";
	public static final String COMMISSION_CASINO = "commissionCasino";
	public static final String COMMISSION_TYPE = "commissionType";

}

