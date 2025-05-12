package com.betmotion.agentsmanagement.rest.dto.user;

public enum UserRole {
  OPERATOR,
  AGENT,
  READONLY_ADMIN,
  UNKNOWN;

  public static UserRole contains(String role) {
    for (UserRole userRole : values()) {
      if (role.contains(userRole.name())) {
        return userRole;
      }
    }
    return UNKNOWN;
  }
}
