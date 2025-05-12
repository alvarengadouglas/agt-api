package com.betmotion.agentsmanagement.domain;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(IpLoginAgent.class)
public abstract class IpLoginAgent_ {

	public static volatile SingularAttribute<IpLoginAgent, Integer> agentId;
	public static volatile SingularAttribute<IpLoginAgent, Long> ipAddress;
	public static volatile SingularAttribute<IpLoginAgent, Date> loginDate;
	public static volatile SingularAttribute<IpLoginAgent, Long> id;
	public static volatile SingularAttribute<IpLoginAgent, String> device;

	public static final String AGENT_ID = "agentId";
	public static final String IP_ADDRESS = "ipAddress";
	public static final String LOGIN_DATE = "loginDate";
	public static final String ID = "id";
	public static final String DEVICE = "device";

}

