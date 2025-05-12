package com.betmotion.agentsmanagement.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Wallet.class)
public abstract class Wallet_ {

	public static volatile SingularAttribute<Wallet, Long> balance;
	public static volatile SingularAttribute<Wallet, Integer> id;
	public static volatile SingularAttribute<Wallet, Boolean> isCreditsWallet;

	public static final String BALANCE = "balance";
	public static final String ID = "id";
	public static final String IS_CREDITS_WALLET = "isCreditsWallet";

}

