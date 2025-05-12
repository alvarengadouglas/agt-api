package com.betmotion.agentsmanagement.service;

import static com.betmotion.agentsmanagement.utils.Constants.AGENT_AUTHORITY;
import static com.betmotion.agentsmanagement.utils.Constants.OPERATOR_AUTHORITY;
import static com.betmotion.agentsmanagement.utils.Constants.READONLY_ADMIN_AUTHORITY;
import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class UserProvider {

  public UserDetails getCurrentUserDetails() {
    return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public boolean isAgent(UserDetails details) {
    return containsAuthority(details, AGENT_AUTHORITY);
  }

  private static boolean containsAuthority(UserDetails details, String authority) {
    return details.getAuthorities()
        .stream()
        .anyMatch(item -> item.getAuthority().equals(authority));
  }

  public boolean isOperator(UserDetails details) {
    return containsAuthority(details, OPERATOR_AUTHORITY);
  }

  public boolean isReadOnlyAdmin(UserDetails details) {
    return containsAuthority(details, READONLY_ADMIN_AUTHORITY);
  }
}
