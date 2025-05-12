package com.betmotion.agentsmanagement.rest;

import static com.betmotion.agentsmanagement.utils.Constants.AUTH_API_BASE_URI;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.ResponseEntity.of;

import com.betmotion.agentsmanagement.rest.dto.user.UserDto;
import com.betmotion.agentsmanagement.service.UserService;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
@RequestMapping(AUTH_API_BASE_URI)
public class AuthController {

  UserService userService;

  @GetMapping(value = "/me")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<UserDto> getCurrentUser() {
    return of(userService.getCurrentUser());
  }

}
