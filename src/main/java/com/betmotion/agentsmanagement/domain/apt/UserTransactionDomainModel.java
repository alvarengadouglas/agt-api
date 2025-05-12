package com.betmotion.agentsmanagement.domain.apt;

import com.betmotion.agentsmanagement.domain.Player;
import com.betmotion.agentsmanagement.domain.UserTransaction;
import com.betmotion.agentsmanagement.domain.UserTransactionType;
import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(UserTransaction.class)
public abstract class UserTransactionDomainModel {

  public static volatile SingularAttribute<UserTransaction, LocalDateTime> operationDate;
  public static volatile SingularAttribute<UserTransaction, Long> amount;
  public static volatile SingularAttribute<UserTransaction, Long> balance;
  public static volatile SingularAttribute<UserTransaction, UserTransactionType> operationType;
  public static volatile SingularAttribute<UserTransaction, Integer> id;
  public static volatile SingularAttribute<UserTransaction, Integer> userid;
  public static volatile SingularAttribute<UserTransaction, Integer> transactionTypeIndex;

  public static volatile SingularAttribute<UserTransaction, Integer> playerid;
  public static volatile SingularAttribute<UserTransaction, Player> player;
  public static volatile SingularAttribute<UserTransaction, Long> reportAmount;

  public static final String OPERATION_DATE = "operationDate";
  public static final String TRANSACTION_TYPE_INDEX = "transactionTypeIndex";

  public static final String AMOUNT = "amount";
  public static final String BALANCE = "balance";
  public static final String OPERATION_TYPE = "operationType";
  public static final String TRANSACTION_STATUS = "transactionStatus";
  public static final String ID = "id";
  public static final String USERID = "userid";
  public static final String PLAYERID = "playerid";
  public static final String PLAYER = "player";

  public static final String REPORT_AMOUNT = "reportAmount";
  public static final String BONUS_REPORT_AMOUNT = "bonusReportAmount";

}

