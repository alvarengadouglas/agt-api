package com.betmotion.agentsmanagement.domain;

import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(WalletTransaction.class)
public abstract class WalletTransaction_ {

	public static volatile SingularAttribute<WalletTransaction, Integer> walletId;
	public static volatile SingularAttribute<WalletTransaction, LocalDateTime> operationDate;
	public static volatile SingularAttribute<WalletTransaction, String> note;
	public static volatile SingularAttribute<WalletTransaction, Long> amount;
	public static volatile SingularAttribute<WalletTransaction, Long> balance;
	public static volatile SingularAttribute<WalletTransaction, WalletTransactionType> operationType;
	public static volatile SingularAttribute<WalletTransaction, Integer> id;

	public static final String WALLET_ID = "walletId";
	public static final String OPERATION_DATE = "operationDate";
	public static final String NOTE = "note";
	public static final String AMOUNT = "amount";
	public static final String BALANCE = "balance";
	public static final String OPERATION_TYPE = "operationType";
	public static final String ID = "id";

}

