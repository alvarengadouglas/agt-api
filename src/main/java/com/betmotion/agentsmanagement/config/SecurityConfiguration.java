package com.betmotion.agentsmanagement.config;

import static com.betmotion.agentsmanagement.utils.Constants.ANT_MATCH_ALL_PATTERN;
import static com.betmotion.agentsmanagement.utils.Constants.NEW_PLAYER_URI;
import static com.betmotion.agentsmanagement.utils.Constants.ACTUATOR_API_BASE_URI;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.betmotion.agentsmanagement.security.JwtTokenFilter;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import springfox.boot.starter.autoconfigure.SpringfoxConfigurationProperties;

@Configuration
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@EnableGlobalMethodSecurity(
    prePostEnabled = true,
    securedEnabled = true,
    jsr250Enabled = true)
@Slf4j
@EnableWebSecurity
public class SecurityConfiguration {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
      SpringfoxConfigurationProperties springfoxConfigurationProperties,
      JwtTokenFilter jwtTokenFilter) throws Exception {
    http.sessionManagement()
        .sessionCreationPolicy(STATELESS);
    http
        .authorizeRequests(authz -> authz
            .antMatchers(springfoxConfigurationProperties.getSwaggerUi().getBaseUrl()
                + ANT_MATCH_ALL_PATTERN)
            .permitAll()
            .antMatchers(String.format("%s%s", ACTUATOR_API_BASE_URI, ANT_MATCH_ALL_PATTERN), NEW_PLAYER_URI)
            .permitAll()
            .anyRequest().authenticated());
    http.csrf().disable();
    http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  @SuppressWarnings("deprecation")
  public PasswordEncoder passwordEncoder() {
    //TODO: We have to use old password encoder, because authentication-server use it //NOSONAR
    return new StandardPasswordEncoder(); //NOSONAR
  }

}
