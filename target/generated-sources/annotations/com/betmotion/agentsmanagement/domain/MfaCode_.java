package com.betmotion.agentsmanagement.domain;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(MfaCode.class)
public abstract class MfaCode_ {

	public static volatile SingularAttribute<MfaCode, Date> createdAt;
	public static volatile SingularAttribute<MfaCode, String> code;
	public static volatile SingularAttribute<MfaCode, Long> id;
	public static volatile SingularAttribute<MfaCode, User> user;
	public static volatile SingularAttribute<MfaCode, Date> expiresAt;

	public static final String CREATED_AT = "createdAt";
	public static final String CODE = "code";
	public static final String ID = "id";
	public static final String USER = "user";
	public static final String EXPIRES_AT = "expiresAt";

}

