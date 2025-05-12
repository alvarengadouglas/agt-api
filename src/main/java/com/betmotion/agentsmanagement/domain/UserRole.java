package com.betmotion.agentsmanagement.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum UserRole {
  PLAYER(1),
  OPERATOR(4),
  AGENT(6),
  READONLY_ADMIN(7);
  Integer id;
}
