package com.betmotion.agentsmanagement.domain;

import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AgentTransaction.class)
public abstract class AgentTransaction_ {

	public static volatile SingularAttribute<AgentTransaction, LocalDateTime> operationDate;
	public static volatile SingularAttribute<AgentTransaction, String> note;
	public static volatile SingularAttribute<AgentTransaction, Long> amount;
	public static volatile SingularAttribute<AgentTransaction, Long> balance;
	public static volatile SingularAttribute<AgentTransaction, Long> bonus;
	public static volatile SingularAttribute<AgentTransaction, User> sourceUser;
	public static volatile SingularAttribute<AgentTransaction, Long> reportAmount;
	public static volatile SingularAttribute<AgentTransaction, AgentTransactionType> operationType;
	public static volatile SingularAttribute<AgentTransaction, Integer> id;
	public static volatile SingularAttribute<AgentTransaction, User> targetUser;

	public static final String OPERATION_DATE = "operationDate";
	public static final String NOTE = "note";
	public static final String AMOUNT = "amount";
	public static final String BALANCE = "balance";
	public static final String BONUS = "bonus";
	public static final String SOURCE_USER = "sourceUser";
	public static final String REPORT_AMOUNT = "reportAmount";
	public static final String OPERATION_TYPE = "operationType";
	public static final String ID = "id";
	public static final String TARGET_USER = "targetUser";

}

