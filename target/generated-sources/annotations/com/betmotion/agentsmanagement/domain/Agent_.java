package com.betmotion.agentsmanagement.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Agent.class)
public abstract class Agent_ {

	public static volatile SingularAttribute<Agent, Integer> walletId;
	public static volatile SingularAttribute<Agent, String> parentTree;
	public static volatile SingularAttribute<Agent, Wallet> wallet;
	public static volatile SingularAttribute<Agent, AgentCode> code;
	public static volatile SingularAttribute<Agent, Wallet> creditsWallet;
	public static volatile SingularAttribute<Agent, BigDecimal> commissionSlots;
	public static volatile SingularAttribute<Agent, Agent> parentAgent;
	public static volatile SingularAttribute<Agent, Integer> userId;
	public static volatile SingularAttribute<Agent, String> commissionType;
	public static volatile SingularAttribute<Agent, Integer> parentId;
	public static volatile SingularAttribute<Agent, Integer> permissionUnblock;
	public static volatile SingularAttribute<Agent, Long> balance;
	public static volatile SingularAttribute<Agent, LocalDateTime> lastCommissionUpdate;
	public static volatile SingularAttribute<Agent, Integer> creditWalletId;
	public static volatile SingularAttribute<Agent, BigDecimal> commissionSports;
	public static volatile SingularAttribute<Agent, BigDecimal> commission;
	public static volatile SingularAttribute<Agent, Integer> id;
	public static volatile SingularAttribute<Agent, User> user;
	public static volatile SingularAttribute<Agent, BigDecimal> commissionCasino;
	public static volatile SingularAttribute<Agent, Boolean> canHaveSubAgents;

	public static final String WALLET_ID = "walletId";
	public static final String PARENT_TREE = "parentTree";
	public static final String WALLET = "wallet";
	public static final String CODE = "code";
	public static final String CREDITS_WALLET = "creditsWallet";
	public static final String COMMISSION_SLOTS = "commissionSlots";
	public static final String PARENT_AGENT = "parentAgent";
	public static final String USER_ID = "userId";
	public static final String COMMISSION_TYPE = "commissionType";
	public static final String PARENT_ID = "parentId";
	public static final String PERMISSION_UNBLOCK = "permissionUnblock";
	public static final String BALANCE = "balance";
	public static final String LAST_COMMISSION_UPDATE = "lastCommissionUpdate";
	public static final String CREDIT_WALLET_ID = "creditWalletId";
	public static final String COMMISSION_SPORTS = "commissionSports";
	public static final String COMMISSION = "commission";
	public static final String ID = "id";
	public static final String USER = "user";
	public static final String COMMISSION_CASINO = "commissionCasino";
	public static final String CAN_HAVE_SUB_AGENTS = "canHaveSubAgents";

}

