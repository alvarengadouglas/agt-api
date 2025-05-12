package com.betmotion.agentsmanagement.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AgentPlayer.class)
public abstract class AgentPlayer_ {

	public static volatile SingularAttribute<AgentPlayer, Integer> agentId;
	public static volatile SingularAttribute<AgentPlayer, Agent> agent;
	public static volatile SingularAttribute<AgentPlayer, Integer> id;
	public static volatile SingularAttribute<AgentPlayer, Integer> playerId;

	public static final String AGENT_ID = "agentId";
	public static final String AGENT = "agent";
	public static final String ID = "id";
	public static final String PLAYER_ID = "playerId";

}

