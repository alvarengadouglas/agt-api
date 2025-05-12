package com.betmotion.agentsmanagement.rest;

import static com.betmotion.agentsmanagement.utils.Constants.PLATFORM_API_BASE_URI;
import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.service.SyncPlatformService;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
@RequestMapping(PLATFORM_API_BASE_URI)
public class SyncController {


  SyncPlatformService syncPlatformService;

  @PostMapping("/syncFromPlatform")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT')")
  public ResponseEntity<String> syncFromPlatform() {
    syncPlatformService.syncFromPlatform();
    return ResponseEntity.ok("Operation is done");
  }

}
