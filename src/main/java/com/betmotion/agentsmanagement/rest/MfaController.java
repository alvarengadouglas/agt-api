package com.betmotion.agentsmanagement.rest;

import com.betmotion.agentsmanagement.domain.Agent;
import com.betmotion.agentsmanagement.domain.User;
import com.betmotion.agentsmanagement.rest.dto.operator.OperatorDto;
import com.betmotion.agentsmanagement.service.MfaService;
import com.betmotion.agentsmanagement.service.OperatorService;
import com.betmotion.agentsmanagement.service.UserProvider;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static lombok.AccessLevel.PRIVATE;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
@RequestMapping("/api/mfa")
public class MfaController {

    MfaService mfaService;

    @GetMapping("/generate-change-password")
    @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT')")
    @ResponseStatus(HttpStatus.OK)
    public void generateMfaChangePassword() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Generating changePassword MFA for user: {}", userDetails.getUsername());
        mfaService.generateMfaChangePassword(userDetails.getUsername());
    }

    @GetMapping("/generate-add-credits")
    @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT')")
    @ResponseStatus(HttpStatus.OK)
    public void generateMfaAddCredits() {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("Generating AddCredits MFA for user: {}", userDetails.getUsername());
        mfaService.generateMfaAddCredits(userDetails.getUsername());
    }
}
