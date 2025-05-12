package com.betmotion.agentsmanagement.services;

import static java.util.Collections.singletonList;
import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.dao.AgentRepository;
import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.User;
import com.betmotion.agentsmanagement.security.AppUserDetails;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class TestsUserDetailsService implements UserDetailsService {

  AgentRepository agentRepository;

  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Agent agent = agentRepository.findByUserName(username);
    if (agent != null) {
      User user = agent.getUser();
      return new AppUserDetails(user.getUserName(), "***",
          singletonList(new SimpleGrantedAuthority(user.getRole().name())), user.getId(),
          agent.getId());
    }
    return null;
  }
}
