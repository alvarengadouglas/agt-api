package com.betmotion.agentsmanagement.domain;

import com.betmotion.agentsmanagement.rest.dto.user.UserTypeIntervalEnum;
import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(UserTransactionInterval.class)
public abstract class UserTransactionInterval_ {

	public static volatile SingularAttribute<UserTransactionInterval, LocalDateTime> operationDate;
	public static volatile SingularAttribute<UserTransactionInterval, Integer> userPlayerId;
	public static volatile SingularAttribute<UserTransactionInterval, UserTransactionType> operationType;
	public static volatile SingularAttribute<UserTransactionInterval, Integer> id;
	public static volatile SingularAttribute<UserTransactionInterval, UserTypeIntervalEnum> userType;

	public static final String OPERATION_DATE = "operationDate";
	public static final String USER_PLAYER_ID = "userPlayerId";
	public static final String OPERATION_TYPE = "operationType";
	public static final String ID = "id";
	public static final String USER_TYPE = "userType";

}

