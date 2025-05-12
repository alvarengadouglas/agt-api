package com.betmotion.agentsmanagement.security;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


import com.betmotion.agentsmanagement.rest.dto.user.UserRole;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@EqualsAndHashCode(callSuper = true)
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppUserDetails extends User {

  Integer userId;

  Integer id;

  public AppUserDetails(String username, String password,
      Collection<? extends GrantedAuthority> authorities, Integer userId, Integer id) {
    super(username, password, authorities);
    this.userId = userId;
    this.id = id;
  }

  public UserRole getRole() {
    for (GrantedAuthority authority : getAuthorities()) {
      if (authority != null) {
        return UserRole.contains(authority.getAuthority());
      }
    }
    return null;
  }

  public List<String> getAuthorityStrings() {
    return getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
  }

}
