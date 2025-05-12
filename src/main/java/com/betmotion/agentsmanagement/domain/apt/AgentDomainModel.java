package com.betmotion.agentsmanagement.domain.apt;

import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.AgentCode;
import com.betmotion.agentsmanagement.domain.User;
import com.betmotion.agentsmanagement.domain.Wallet;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Agent.class)
public abstract class AgentDomainModel {

  public static volatile SingularAttribute<Agent, Integer> walletId;
  public static volatile SingularAttribute<Agent, Wallet> wallet;
  public static volatile SingularAttribute<Agent, AgentCode> code;
  public static volatile SingularAttribute<Agent, Wallet> creditsWallet;
  public static volatile SingularAttribute<Agent, Agent> parentAgent;
  public static volatile SingularAttribute<Agent, Integer> userId;
  public static volatile SingularAttribute<Agent, Integer> parentId;
  public static volatile SingularAttribute<Agent, Integer> creditWalletId;
  public static volatile SingularAttribute<Agent, BigDecimal> commission;
  public static volatile SingularAttribute<Agent, Integer> id;
  public static volatile SingularAttribute<Agent, User> user;
  public static volatile SingularAttribute<Agent, Boolean> canHaveSubAgents;

  public static final String WALLET_ID = "walletId";
  public static final String WALLET = "wallet";
  public static final String CODE = "code";
  public static final String CREDITS_WALLET = "creditsWallet";
  public static final String PARENT_AGENT = "parentAgent";
  public static final String USER_ID = "userId";
  public static final String PARENT_ID = "parentId";
  public static final String CREDIT_WALLET_ID = "creditWalletId";
  public static final String COMMISSION = "commission";
  public static final String ID = "id";
  public static final String USER = "user";
  public static final String CAN_HAVE_SUB_AGENTS = "canHaveSubAgents";

}

