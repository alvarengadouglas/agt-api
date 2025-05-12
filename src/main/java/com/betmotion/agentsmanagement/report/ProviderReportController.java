package com.betmotion.agentsmanagement.report;

import static com.betmotion.agentsmanagement.utils.Constants.REPORTS_API_BASE_URI;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.ResponseEntity.ok;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.betmotion.agentsmanagement.rest.dto.reports.AgentEspecificProviderReport;
import com.betmotion.agentsmanagement.rest.dto.reports.AgentProviderReport;
import com.betmotion.agentsmanagement.rest.dto.reports.AgentProviderReportRequest;
import com.betmotion.agentsmanagement.service.ProviderReportService;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
@RestController
@RequestMapping(REPORTS_API_BASE_URI)
@AllArgsConstructor
public class ProviderReportController {

	private final ProviderReportService reportService;
	
	@PostMapping(value = "/agents/providers")
	@PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
	public ResponseEntity<AgentProviderReport> getReportProvider(@RequestBody @Valid AgentProviderReportRequest providerReportRequest) {
		return ok(reportService.getProviderReport(providerReportRequest));
	}

	@PostMapping(value = "/agents/especific-provider")
	@PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
	public ResponseEntity<AgentEspecificProviderReport> getReportEspecificProvider(@RequestBody @Valid AgentProviderReportRequest providerReportRequest) {
		return ok(reportService.getEspecificProviderReport(providerReportRequest));
	}

	
}
