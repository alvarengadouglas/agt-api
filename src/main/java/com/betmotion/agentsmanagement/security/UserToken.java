package com.betmotion.agentsmanagement.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Set;
import lombok.Data;

@Data
public class UserToken {

  @JsonProperty("authorities")
  Set<String> authorities;

  @JsonProperty("user_id")
  private Integer userId;

  @JsonProperty("id")
  private Integer id;

  @JsonProperty("user_name")
  private String username;
}
