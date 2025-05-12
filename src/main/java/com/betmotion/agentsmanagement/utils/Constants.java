package com.betmotion.agentsmanagement.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Constants {

  public static final String BASE_APPLICATION_PACKAGE = "com.betmotion.agentsmanagement";
  public static final String REPORTS_API_BASE_URI = "/api/reports";
  public static final String PLATFORM_API_BASE_URI = "/api/platform";
  public static final String POWERBI_API_BASE_URI = "/api/powerbi";
  public static final String AUTH_API_BASE_URI = "/api/auth";
  public static final String NEW_PLAYER_URI = "/api/agent/new-player";
  public static final String ACTUATOR_API_BASE_URI = "/actuator";
  public static final String ANT_MATCH_ALL_PATTERN = "/**";
  public static final String OPERATOR_AUTHORITY = "OPERATOR_DEFAULT";
  public static final String AGENT_AUTHORITY = "AGENT_DEFAULT";
  public static final String READONLY_ADMIN_AUTHORITY = "READONLY_ADMIN";
  public static final String PLATFORM_AGENT_USERNAME = "agentevirtual";

  public static final String ISO_DATE_FORMAT_VALUE = "yyyy-MM-dd";
  public static final String ISO_DATE_TIME_FORMAT_VALUE = "yyyy-MM-dd'T'HH:mm:ss";
  public static final String YYYYMMDDD_FORMAT = "yyyyMMdd";
  public static final int APP_DEFAULT_PAGE_SIZE = 15;
  public static final int APP_DEFAULT_PAGE_INDEX = 0;
  public static final int DEFAULT_BATCH_SIZE = 2000;
  public static final String EMPTY_STRING = "";
  public static final Date DEFAULT_BORN_DATE = new GregorianCalendar(
      1800, Calendar.JANUARY, 1).getTime();

  public static final Integer IN_LIMIT_SIZE = DEFAULT_BATCH_SIZE;
}
