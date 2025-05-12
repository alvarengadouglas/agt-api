package com.betmotion.agentsmanagement.domain.apt;

import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.Currency;
import com.betmotion.agentsmanagement.domain.User;
import com.betmotion.agentsmanagement.domain.UserRole;
import com.betmotion.agentsmanagement.domain.UserStatus;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(User.class)
public abstract class UserDomainModel {

  public static volatile SingularAttribute<User, Date> lastLogin;
  public static volatile SingularAttribute<User, String> lastName;
  public static volatile SingularAttribute<User, Agent> agent;
  public static volatile SingularAttribute<User, Integer> agentId;
  public static volatile SingularAttribute<User, UserRole> role;
  public static volatile SingularAttribute<User, Boolean> receiveEmail;
  public static volatile SingularAttribute<User, Date> bornDate;
  public static volatile SingularAttribute<User, String> userName;
  public static volatile SingularAttribute<User, Date> createdOn;
  public static volatile SingularAttribute<User, String> firstName;
  public static volatile SingularAttribute<User, String> password;
  public static volatile SingularAttribute<User, String> phone;
  public static volatile SingularAttribute<User, Currency> currency;
  public static volatile SingularAttribute<User, Integer> id;
  public static volatile SingularAttribute<User, Boolean> testUser;
  public static volatile SingularAttribute<User, String> email;
  public static volatile SingularAttribute<User, UserStatus> status;

  public static final String LAST_LOGIN = "lastLogin";
  public static final String LAST_NAME = "lastName";
  public static final String AGENT = "agent";
  public static final String AGENT_ID = "agentId";
  public static final String ROLE = "role";
  public static final String RECEIVE_EMAIL = "receiveEmail";
  public static final String BORN_DATE = "bornDate";
  public static final String USER_NAME = "userName";
  public static final String CREATED_ON = "createdOn";
  public static final String FIRST_NAME = "firstName";
  public static final String PASSWORD = "password";
  public static final String PHONE = "phone";
  public static final String CURRENCY = "currency";
  public static final String ID = "id";
  public static final String TEST_USER = "testUser";
  public static final String EMAIL = "email";
  public static final String STATUS = "status";

}

