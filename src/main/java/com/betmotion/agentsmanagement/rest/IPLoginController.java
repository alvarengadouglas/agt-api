package com.betmotion.agentsmanagement.rest;

import com.betmotion.agentsmanagement.annotations.ApiPageable;
import com.betmotion.agentsmanagement.rest.dto.reports.IPLoginsResponse;
import com.betmotion.agentsmanagement.rest.dto.reports.IpLoginReportRequest;
import com.betmotion.agentsmanagement.service.IpLoginAgentService;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import static org.springframework.http.ResponseEntity.ok;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static lombok.AccessLevel.PRIVATE;


@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
@RequestMapping("/api")
public class IPLoginController {

    IpLoginAgentService ipLoginAgentService;

    @RequestMapping(value = "/risk/ipLogins.do", method = RequestMethod.POST)
    @ApiPageable
    @PreAuthorize("hasAnyAuthority('RISK_ADMIN')")
    public ResponseEntity<IPLoginsResponse> allLoginsReport(
            @RequestBody @Valid IpLoginReportRequest ipLoginReportRequest
    ) {
        return ok(ipLoginAgentService.getAllLogins(ipLoginReportRequest));
    }
}
