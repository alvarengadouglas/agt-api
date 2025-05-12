package com.betmotion.agentsmanagement.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PlayerWallet.class)
public abstract class PlayerWallet_ {

	public static volatile SingularAttribute<PlayerWallet, Long> balance;
	public static volatile SingularAttribute<PlayerWallet, Integer> id;
	public static volatile SingularAttribute<PlayerWallet, Long> platformBalance;
	public static volatile SingularAttribute<PlayerWallet, Integer> playerId;

	public static final String BALANCE = "balance";
	public static final String ID = "id";
	public static final String PLATFORM_BALANCE = "platformBalance";
	public static final String PLAYER_ID = "playerId";

}

