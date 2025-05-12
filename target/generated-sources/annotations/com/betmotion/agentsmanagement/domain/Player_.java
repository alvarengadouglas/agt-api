package com.betmotion.agentsmanagement.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Player.class)
public abstract class Player_ {

	public static volatile SingularAttribute<Player, Integer> id;
	public static volatile SingularAttribute<Player, Integer> platformId;
	public static volatile SingularAttribute<Player, String> userName;
	public static volatile SingularAttribute<Player, Integer> userId;
	public static volatile SingularAttribute<Player, UserStatus> status;

	public static final String ID = "id";
	public static final String PLATFORM_ID = "platformId";
	public static final String USER_NAME = "userName";
	public static final String USER_ID = "userId";
	public static final String STATUS = "status";

}

