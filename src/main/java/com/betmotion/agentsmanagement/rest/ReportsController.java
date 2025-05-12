package com.betmotion.agentsmanagement.rest;

import com.betmotion.agentsmanagement.annotations.ApiPageable;
import com.betmotion.agentsmanagement.rest.dto.PageData;
import com.betmotion.agentsmanagement.rest.dto.reports.*;
import com.betmotion.agentsmanagement.service.PlatformApiService;
import com.betmotion.agentsmanagement.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

import static com.betmotion.agentsmanagement.utils.Constants.*;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
@RequestMapping(REPORTS_API_BASE_URI)
public class ReportsController {

  ReportService reportService;
  PlatformApiService platformApiService;

  @PostMapping(value = "/players/transactions")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PageData<PlayerCreditsTransaction>> getPlayersTransactions(
      @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
      @SortDefault.SortDefaults({@SortDefault(sort = "id", direction = Sort.Direction.ASC)
      }) Pageable pageRequest, @RequestBody @Valid PlayerTransactionsRequest request) {
    return ok(reportService.getPlayerTransactions(pageRequest, request));
  }

    @PostMapping(value = "/players/transactionsTotals")
    @ApiPageable
    @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
    public ResponseEntity<TransactionTotalsDto> getPlayersTransactionsTotals(@RequestBody @Valid PlayerTransactionsRequest request) {
        return ok(reportService.getPlayerTransactionsTotals(request));
    }

  @PostMapping(value = "/players/creditReport")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PlayerCreditReportTransactionsResponse> getPlayersCreditReport(
      @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
      @SortDefault.SortDefaults({@SortDefault(sort = "id", direction = Sort.Direction.ASC)
      }) Pageable pageRequest, @RequestBody @Valid PlayerCreditReportTransactionsRequest request) {
    return ok(reportService.getPlayerCreditReport(pageRequest, request));
  }

  @PostMapping(value = "/agents/creditReport")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<AgentCreditReportTransactionsResponse> getAgentsCreditReport(
      @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
      @SortDefault.SortDefaults({@SortDefault(sort = "id", direction = Sort.Direction.ASC)
      }) Pageable pageRequest, @RequestBody @Valid AgentCreditReportTransactionsRequest request) {
    return ok(reportService.getAgentCreditReport(pageRequest, request));
  }


  @PostMapping(value = "/agents/transactions")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PageData<AgentWalletTransaction>> getAgentsTransactions(
      @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
      @SortDefault.SortDefaults({@SortDefault(sort = "id", direction = Sort.Direction.ASC)
      }) Pageable pageRequest, @RequestBody @Valid AgentTransactionsRequest request) {
    return ok(reportService.getAgentTransactions(pageRequest, request));
  }

    @PostMapping(value = "/agents/transactionsTotals")
    @ApiPageable
    @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
    public ResponseEntity<TransactionTotalsDto> getAgentsTransactionsTotals( @RequestBody @Valid AgentTransactionsRequest request) {
        return ok(reportService.getAgentTransactionsTotals(request));
    }

  @PostMapping(value = "/agents/payment")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<AgentPaymentChipReport> getPayment(
      @RequestBody @Valid AgentPaymentReportRequest paymentReportRequest) {
    return ok(reportService.getAgentPayment(paymentReportRequest));
  }

  @PostMapping(value = "/agents/commissionjava")
    @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<CommissionReport> getAgentUsernamesAndCommissionJava(
          @RequestBody @Valid CommissionPayload commissionPayload) {
    AgentCommissions commissions = reportService.getCommission(commissionPayload.getAgentId());
    CommissionAgent commissionAgent = platformApiService.getCommissionReportJava(commissionPayload);
    return ok(reportService.getCommissionReport(commissions, commissionAgent));
  }

    @PostMapping(value = "/agents/commission")
    @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
    public ResponseEntity<CommissionReport> getAgentUsernamesAndCommission(
            @RequestBody @Valid CommissionPayload commissionPayload) {
      AgentCommissions commissions = reportService.getCommission(commissionPayload.getAgentId());
      CommissionAgent commissionAgent = platformApiService.getCommissionReportNew(commissionPayload);
        return ok(reportService.getCommissionReport(commissions, commissionAgent));
    }

    @PostMapping(value = "/chips-transactions")
    @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
    public ResponseEntity<ChipsTransactionsReport> chipsTransactions(
            @RequestBody @Valid ChipsTransactionsReportRequest chipsTransactionsReportRequest) {
        return ok(reportService.getChipsTransactions(chipsTransactionsReportRequest));
    }

    @GetMapping(value = "/players/transactionsReport")
    @ApiPageable
    @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
    public ResponseEntity<PlayerMoneyTransactionsReportDTO> getPlayerTransactionsReport(
            @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
            @SortDefault.SortDefaults({@SortDefault(sort = "id", direction = Sort.Direction.ASC)
            }) Pageable pageRequest, @RequestParam(name = "player") @Valid Integer playerId) {

        return ok(platformApiService.getMoneyTransactionsFromPlayer(pageRequest, playerId));
    }

}
