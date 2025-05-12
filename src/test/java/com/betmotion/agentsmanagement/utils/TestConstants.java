package com.betmotion.agentsmanagement.utils;

import java.math.BigDecimal;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestConstants {

  public static final String CLEAN_DB_SQL = "classpath:clean-db.sql";
  public static final String AGENT_DATA_SQL = "classpath:agent-data.sql";
  public static final String PREDEFINED_OPERATOR = "operator";
  public static final String PREDEFINED_AGENT_WITH_SUBAGENTS_3 = "agentWithSub_3";
  public static final String PREDEFINED_AGENT_WITH_SUBAGENTS_4 = "agentWithSub_4";
  public static final String PREDEFINED_SUBAGENT_3_1 = "subagent_3_1";
  public static final String PREDEFINED_SUBAGENT_3_1_1 = "subagent_3_1_1";
  public static final String PREDEFINED_SUBAGENT_3_1_2 = "subagent_3_1_2";
  public static final String PREDEFINED_SUBAGENT_4_1 = "subagent_4_1";
  public static final String PREDEFINED_PLAYER_3_1_1 = "player_3_1_1";
  public static final String PREDEFINED_PLAYER_3_1_1_1 = "player_3_1_1_1";
  public static final String PREDEFINED_PLAYER_3_1_2_1_1 = "player_3_1_2_1_1";
  public static final String PREDEFINED_PLAYER_4_1 = "player_4_1";
  public static final String AGENT = "AGENT";
  public static final String PLAYER = "PLAYER";
  public static final String OPERATOR_PASSWORD = "AAAAA";
  public static final String OPERATOR_EMAIL = "test@gmail.com";
  public static final String OPERATOR_PHONE_NUMBER = "0000";
  public static final String AGENT_USER_NAME = "AAA";
  public static final String AGENT_FULL_NAME = "AAA BBB";
  public static final String AGENT_PASSWORD = "AAAAA";
  public static final String AGENT_EMAIL = "test@gmail.com";
  public static final String AGENT_PHONE_NUMBER = "0000";
  public static final BigDecimal AGENT_COMMISSION = new BigDecimal("5.1");
  public static final boolean AGENT_CAN_HAVE_SUB_AGENTS = true;
  public static final String AGENT_FIRST_NAME = "AA";
  public static final String AGENT_LAST_NAME = "BBB";
  public static final String AGENT_BORN_DATE = "2000-07-18";
  public static final String AGENT_EMAIL_UPDATE = "test12@gmail.com";
  public static final String AGENT_PHONE_NUMBER_UPDATE = "0001";
  public static final BigDecimal AGENT_COMMISSION_UPDATE = new BigDecimal("6.2");
  public static final boolean AGENT_CAN_HAVE_SUB_AGENTS_UPDATE = false;
  public static final String AGENT_FULL_NAME_UPDATE = "CC DD";
  public static final String AGENT_FIRST_NAME_UPDATE = "CC";
  public static final String AGENT_LAST_NAME_UPDATE = "DD";
  public static final String AGENT_USER_NAME_UPDATE = "updateUserName";
  public static final String AGENT_BORN_DATE_UPDATE = "2000-07-22";
  public static final String OPERATOR_NEW_PASSWORD = "operatorNewPassword";
  public static final String AGENT_NEW_PASSWORD = "agentNewPassword";
  public static final String PLAYER_BORN_DATE = "1989-06-27";
  public static final String PLAYER_CURRENCY = "ARS";
  public static final String PLAYER_EMAIL = "Player@gmail.com";
  public static final String PLAYER_PHONE = "0673111111";
  public static final Integer PLAYER_PHONE_COUNTRY_ID = 11;

  public static final Boolean PLAYER_RECEIVE_EMAIL = false;
  public static final String PLAYER_ROLE = "PLAYER";
  public static final String PLAYER_STATUS = "ACTIVE";
  public static final Boolean PLAYER_TEST_USER = false;
  public static final String PLAYER_USER_NAME = "PlayerUserName";

  public static final String PLAYER_FIRST_NAME = "PlayerFirstName";

  public static final String PLAYER_LAST_NAME = "PlayerLastName";

  public static final String PLAYER_PASSWORD = "PlayerPassword";
  public static final String ID = "$.id";
  public static final String EMAIL = "$.email";
  public static final String USER_NAME = "$.userName";
  public static final String FULL_NAME = "$.fullName";
  public static final String FIRST_NAME = "$.firstName";
  public static final String LAST_NAME = "$.lastName";
  public static final String PHONE = "$.phone";
  public static final String PHONE_NUMBER = "$.phoneNumber";
  public static final String BORN_DATE = "$.bornDate";
  public static final String CURRENCY = "$.currency";
  public static final String COMMISSION = "$.commission";
  public static final String OPERATOR_ID = "$.operatorId";
  public static final String CREATED_ON = "$.createdOn";
  public static final String LAST_LOGIN = "$.lastLogin";
  public static final String RECEIVE_EMAIL = "$.receiveEmail";
  public static final String ROLE = "$.role";
  public static final String PARENT_USER_NAME = "$.parentUserName";
  public static final String NUMBER_OF_DIRECT_SUBAGENTS = "$.numberOfDirectSubAgents";
  public static final String NUMBER_OF_DIRECT_PLAYERS = "$.numberOfDirectPlayers";
  public static final String STATUS = "$.status";
  public static final String TEST_USER = "$.testUser";
  public static final String AGENT_ID = "$.agentId";
  public static final String CAN_HAVE_SUB_AGENTS = "$.canHaveSubAgents";
  public static final String CODE = "$.code";
  public static final String CREATED_DATE = "$.createdDate";

  public static final String HIERARCHY = "$.hierarchy";

  public static final String LENGTH = "$.length()";
  public static final String DATA_LENGTH = "$.data.length()";
  public static final String TOTAL_PAGES = "$.totalPages";
  public static final String TOTAL_ELEMENTS = "$.totalElements";

  public static final Long MAX_ATTEMPTS = 20L;


}
