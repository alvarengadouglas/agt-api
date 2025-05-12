package com.betmotion.agentsmanagement.platform.api;


import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import java.util.Map;

import com.betmotion.agentsmanagement.platform.api.dto.*;
import com.betmotion.agentsmanagement.rest.dto.reports.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import com.betmotion.agentsmanagement.platform.api.config.PlatformApiClientConfiguration;

@FeignClient(name = "platformApi", configuration = PlatformApiClientConfiguration.class,
    url = "${platform.api.url}")
public interface PlatformApi {

  @RequestMapping(value = "/agent/user/register.do", method = RequestMethod.POST,
      consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  RegisterUserResponseDto createUser(@RequestBody RegisterUserRequestDto requestDto);

  @RequestMapping(value = "/agent/user/change-status.do", method = RequestMethod.POST,
      consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  ChangeStatusResponseDto changeStatus(@RequestBody ChangeStatusRequestDto requestDto);

  @RequestMapping(value = "/agent/user/find-by-ids.do", method = RequestMethod.POST,
      consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  List<PlatformUserDto> findByIds(@RequestBody FindByIdsRequestDto requestDto);

    @RequestMapping(value = "/agent/user/sum-by-ids.do", method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    Long findSumBYIds(@RequestBody FindByIdsRequestDto requestDTO);

  @RequestMapping(value = "/agent/user/change-password.do", method = RequestMethod.POST,
      consumes = APPLICATION_JSON_VALUE)
  void changePassword(@RequestBody ChangePasswordRequestDto requestDto);

  @RequestMapping(value = "/agent/user/transactions.do", method = RequestMethod.POST,
          consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  PlayerMoneyTransactionsReportResponse getMoneyTransactionsFromPlayer(@RequestBody UserTransactionFilterDTO requestDto);

  @RequestMapping(value = "/agent/deposit/apply.do", method = RequestMethod.POST,
      consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  DepositUserResponseDto deposit(@RequestBody DepositUserRequestDto requestDto);

  @RequestMapping(value = "/agent/withdrawal/apply.do", method = RequestMethod.POST,
      consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  WithdrawalUserResponseDto withdrawal(@RequestBody WithdrawalUserRequestDto requestDto);
  
  @RequestMapping(value = "/agent/report/provider-transaction.do", method = RequestMethod.POST,
	      consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	  AgentProviderReport providerReport(@RequestBody AgentProviderReportPlatformRequest providerReportRequest);
  
  @RequestMapping(value = "/agent/report/especific-provider-transaction.do", method = RequestMethod.POST,
	      consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
	  AgentEspecificProviderReport especificProviderReport(@RequestBody AgentProviderReportPlatformRequest providerReportRequest);

  @RequestMapping(value = "/closeUsers/getInactiveUsers.do", method = RequestMethod.GET)
  List<String> getAllInactiveUsers();

  @RequestMapping(value = "/agent/report/commission.do", method = RequestMethod.POST,
          consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  CommissionAgent getCommissionReport(@RequestBody CommissionPayload payload);

  @RequestMapping(value = "/agent/report/commissionjava.do", method = RequestMethod.POST,
          consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  CommissionAgent getCommissionReportJava(@RequestBody CommissionPayload payload);

  @RequestMapping(value = "/closeUsers/closeInactiveUsers.do", method = RequestMethod.POST,
          consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  void closeInactiveUsers(@RequestBody List<String> inactiveUsers);

  @RequestMapping(value = "/email/send-email-code/change-password.do", method = RequestMethod.POST,
          consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  void mfaSendEmailCodeChangePassword(@RequestBody Map<String, Object> body);

  @RequestMapping(value = "/email/send-email-code/add-credits.do", method = RequestMethod.POST,
          consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
  void mfaSendEmailCodeAddCredits(@RequestBody Map<String, Object> body);
}
