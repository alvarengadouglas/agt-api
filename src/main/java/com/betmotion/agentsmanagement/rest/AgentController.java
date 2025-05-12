package com.betmotion.agentsmanagement.rest;


import static com.betmotion.agentsmanagement.utils.Constants.*;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.http.ResponseEntity.accepted;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.of;
import static org.springframework.http.ResponseEntity.ok;

import com.betmotion.agentsmanagement.annotations.ApiPageable;
import com.betmotion.agentsmanagement.dao.AgentRepository;
import com.betmotion.agentsmanagement.domain.*;
import com.betmotion.agentsmanagement.rest.dto.AgentStatusEnum;
import com.betmotion.agentsmanagement.rest.dto.CollectAgentDto;
import com.betmotion.agentsmanagement.rest.dto.CollectPlayerDto;
import com.betmotion.agentsmanagement.rest.dto.CreditBalanceDto;
import com.betmotion.agentsmanagement.rest.dto.PageData;
import com.betmotion.agentsmanagement.rest.dto.PayoutAgentDto;
import com.betmotion.agentsmanagement.rest.dto.PayoutPlayerDto;
import com.betmotion.agentsmanagement.rest.dto.agent.AgentCreditOperation;
import com.betmotion.agentsmanagement.rest.dto.agent.AgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.CreateAgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.DepositAgentUserBalanceOperation;
import com.betmotion.agentsmanagement.rest.dto.agent.ExtendedInfoAgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.LinkUsersDto;
import com.betmotion.agentsmanagement.rest.dto.agent.UpdateAgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.WithdrawalAgentUserBalanceOperation;
import com.betmotion.agentsmanagement.rest.dto.player.ChangePasswordPlayerDto;
import com.betmotion.agentsmanagement.rest.dto.player.CreatePlayerDto;
import com.betmotion.agentsmanagement.rest.dto.player.PlayerDetailDto;
import com.betmotion.agentsmanagement.rest.dto.player.PlayerDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserDetailInfoDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserInfoDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserSumCreditsBalancesDto;
import com.betmotion.agentsmanagement.security.AppUserDetails;
import com.betmotion.agentsmanagement.service.*;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.Valid;

import com.betmotion.agentsmanagement.service.exceptions.SemaphoreControlNumberOfAttemptsException;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
@RequestMapping("/api/agent")
public class AgentController {

  AgentService agentService;
  ServicesConfiguration servicesConfiguration;
  CreditsService creditsService;
  UserProvider userProvider;
  AgentRepository agentRepository;
  SemaphoreControlService semaphoreControlService;

  @GetMapping(value = "/{id}")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<AgentDto> getById(@PathVariable(value = "id") Integer id) {
    return of(agentService.getById(id));
  }

  @GetMapping(value = "/creditBalance")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<CreditBalanceDto> getCreditBalance() {
    return ResponseEntity.ok(creditsService.getCreditBalance());
  }

  @GetMapping(value = "/{id}/extendedInfo")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<ExtendedInfoAgentDto> getExtendedInfoById(@PathVariable(value = "id")
                                                                  Integer id) {
    return ok(agentService.getExtendedInfoById(id));
  }

  @GetMapping(value = "/parent/{id}")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PageData<AgentDto>> getAgentListByParentId(
          @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
          @SortDefault(sort = "id", direction = ASC) Pageable pageRequest,
          @PathVariable("id") Integer id) {
    return of(agentService.getAgentListByParentId(pageRequest, id));
  }

  @GetMapping
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PageData<AgentDto>> getAll(
          @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
          @SortDefault.SortDefaults({@SortDefault(sort = "id", direction = Sort.Direction.ASC)
          }) Pageable pageRequest,
          @RequestParam(value = "parentId", required = false) Integer parentId) {
    return ok(agentService.getAll(pageRequest, parentId));
  }

  @GetMapping("/search")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PageData<AgentDto>> searchByText(
          @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
          @SortDefault.SortDefaults({@SortDefault(sort = "id", direction = Sort.Direction.ASC)
          }) Pageable pageRequest,
          @RequestParam(value = "text") String text) {
    return ok(agentService.searchAgents(pageRequest, text));
  }

  @GetMapping("/user/detail/{id}")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<UserDetailInfoDto> getUserDetailInfo(@PathVariable(value = "id") Integer id,
                                                             @RequestParam(name = "role") UserRole role) {
    return ok(agentService.getUserDetailInfo(id, role));
  }

  @GetMapping("/user/current-detail/{id}")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<UserDetailInfoDto> getCurrentUserDetailInfo(@PathVariable(value = "id") Integer id) {
    return ok(agentService.getCurrentUserDetailInfo(id));
  }

  @GetMapping("/player/detail/{id}")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PlayerDetailDto> getPlayerDetails(@PathVariable(value = "id") Integer id) {
    return ok(agentService.getPlayerDetailInfo(id));
  }

  @PostMapping
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity<AgentDto> create(@Valid @RequestBody CreateAgentDto createOperatorDto)
          throws URISyntaxException {
    AgentDto result = agentService.create(createOperatorDto);
    String url = servicesConfiguration.getApplicationBaseUrl() + "/api/agent" + "/"
            + result.getId();

    return created(new URI(url)).body(result);
  }

  @PutMapping(value = "/{id}")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity<AgentDto> updateAgent(@PathVariable(value = "id") Integer id,
                                              @RequestBody @Valid UpdateAgentDto dto) {
    return of(agentService.updateAgent(id, dto));
  }

  @PutMapping(value = "/{id}/linkUsers")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity<AgentDto> linkUsers(@PathVariable(value = "id") Integer id,
                                            @RequestBody @Valid LinkUsersDto data) {
    return of(agentService.linkUsersToAgent(id, data.getUserIds()));
  }

  @PostMapping(value = "/new-player")
  public ResponseEntity<PlayerDto> newPlayer(@RequestBody @Valid CreatePlayerDto dto) {
    return of(agentService.newPlayer(dto));
  }

  @PostMapping(value = "/player")
  @PreAuthorize("hasAuthority('AGENT_DEFAULT')")
  public ResponseEntity<PlayerDto> createUser(@RequestBody @Valid CreatePlayerDto dto) {
    return of(agentService.createPlayer(dto));
  }

  @PostMapping(value = "/player/change-password")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity<PlayerDto> changePasswordForPlayer(@RequestBody @Valid
                                                           ChangePasswordPlayerDto dto) {
    return ok(agentService.changePasswordForPlayer(dto));
  }

  @DeleteMapping(value = "/{id}")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity<AgentDto> deleteAgent(@PathVariable(value = "id") Integer id) {
    return of(agentService.delete(id));
  }

  @PostMapping("/player/addCredits")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public void addCreditsToPlayer(@Valid @RequestBody DepositAgentUserBalanceOperation dto) {
    Agent agent = agentService.getAgentForCurrentUser();

    try {
      if (dto.getPlayerId().compareTo(agent.getId()) != 0) {
        semaphoreControlService.tryLock(dto.getPlayerId(), 0);
      }
      semaphoreControlService.tryLock(agent.getId(), 0);
      agentService.addCreditsToPlayer(dto.getPlayerId(), dto.getAmount(), dto.getForFree(),
              dto.getBonus() != null ? dto.getBonus() : 0);
      semaphoreControlService.unlock(dto.getPlayerId());
      semaphoreControlService.unlock(agent.getId());
    } catch (SemaphoreControlNumberOfAttemptsException e) {
      log.info(e.getMessage());
      throw new RuntimeException(e);
    } catch (Exception e) {
      log.info(e.getMessage());
      semaphoreControlService.unlock(dto.getPlayerId());
      semaphoreControlService.unlock(agent.getId());
      throw new RuntimeException(e);
    }
  }

  @PostMapping("/player/withdrawCredits")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public void withdrawCreditsFromPlayer(
          @Valid @RequestBody WithdrawalAgentUserBalanceOperation dto) {
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    if (userProvider.isAgent(currentUserDetails)) {
      AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
      Agent agent = agentRepository.getEntity(appUserDetails.getId());
      try {
        if (dto.getPlayerId().compareTo(agent.getId()) != 0) {
          semaphoreControlService.tryLock(dto.getPlayerId(), 0);
        }
        semaphoreControlService.tryLock(agent.getId(), 0);
        agentService.withdrawCreditsFromPlayer(dto.getPlayerId(), dto.getAmount(), dto.getForFree());
        semaphoreControlService.unlock(dto.getPlayerId());
        semaphoreControlService.unlock(agent.getId());
      } catch (SemaphoreControlNumberOfAttemptsException e) {
        log.info(e.getMessage());
        throw new RuntimeException(e);
      } catch (Exception e) {
        log.info(e.getMessage());
        semaphoreControlService.unlock(dto.getPlayerId());
        semaphoreControlService.unlock(agent.getId());
        throw new RuntimeException(e);
      }
    }
  }

  @PutMapping(value = "/{id}/changePassword")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<AgentDto> changePassword(@PathVariable(value = "id") Integer id,
                                                 @RequestBody @Valid String password) {
    return ok(agentService.changePassword(id, password));
  }

  @GetMapping(value = "/allUsers")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PageData<UserInfoDto>> getAllUsers(
          @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
          @SortDefault.SortDefaults({@SortDefault(sort = "user_name", direction = Sort.Direction.ASC)
          }) Pageable pageRequest,
          @RequestParam(name = "role", required = false) UserRole role) {
    PageData<UserInfoDto> result = role == null
            ? agentService.getAllUsers(pageRequest)
            : agentService.getAllUsers(pageRequest, role);
    return ok(result);
  }

  @GetMapping(value = "/allUsers/subAgentId/{subAgentId}")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PageData<UserInfoDto>> getAllUsersForSubAgent(
          @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
          @SortDefault.SortDefaults({@SortDefault(sort = "user_name", direction = Sort.Direction.ASC)
          }) Pageable pageRequest,
          @PathVariable(name = "subAgentId") Integer userId,
          @RequestParam(name = "withSubAgent", required = false, defaultValue = "false")
          boolean withSubAgent) {
    return ok(agentService.getAllUsersForSubAgent(pageRequest, userId, withSubAgent));
  }

  @GetMapping(value = "/directUsersSumCreditsAndBalance")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<UserSumCreditsBalancesDto>  getDirectUsersSumCreditsAndBalance(
          @RequestParam(name = "search", required = false) String search,
          @RequestParam(name = "role", required = false) String role,
          @RequestParam(name = "status", required = false) AgentStatusEnum status,
          @RequestParam(name = "parentId", required = false) Integer parentId) {

    return ok(agentService.getDirectUsersSumCreditsAndBalance(search, role, status, parentId));
  }

  @GetMapping(value = "/directUsers")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PageData<UserInfoDto>> getDirectUsers(
          @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
          @SortDefault.SortDefaults({@SortDefault(sort = "user_name", direction = Sort.Direction.ASC)
          }) Pageable pageRequest,
          @RequestParam(name = "role", required = false) UserRole role,
          @RequestParam(name = "status", required = false) AgentStatusEnum status,
          @RequestParam(name = "currentUrl", required = false) String currentUrl) {
    return ok(agentService.getDirectUsers(pageRequest, role, status, currentUrl));
  }

  @GetMapping(value = "/directUsers/{subAgentId}")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PageData<UserInfoDto>> getDirectUsersForSubAgent(
          @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
          @SortDefault.SortDefaults({@SortDefault(sort = "user_name", direction = Sort.Direction.ASC)
          }) Pageable pageRequest,
          @RequestParam(name = "role", required = false) UserRole role,
          @RequestParam(name = "status", required = false) AgentStatusEnum status,
          @RequestParam(name = "currentUrl", required = false) String currentUrl,
          @PathVariable(name = "subAgentId") Integer userId) {
    return ok(agentService.getDirectUsersForSubAgent(pageRequest, userId, role, status, currentUrl));
  }

  @GetMapping(value = "/searchUsers")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PageData<UserInfoDto>> searchUsersByUserName(
          @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
          @SortDefault.SortDefaults({@SortDefault(sort = "user_name", direction = Sort.Direction.ASC)
          }) Pageable pageRequest,
          @RequestParam(name = "search") String search,
          @RequestParam(name = "role", required = false) UserRole role,
          @RequestParam(name = "currentUrl", required = false) String currentUrl,
          @RequestParam(name = "status", required = false) AgentStatusEnum status) {
    return ok(agentService.searchUsersByUserName(pageRequest, search, role, status, currentUrl));
  }

  @PutMapping(value = "/{id}/block")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity blockAgent(@PathVariable(value = "id") Integer id) {
    agentService.blockAgent(id);
    return accepted().build();
  }

  @PutMapping(value = "/{id}/unblock")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity unblockAgent(@PathVariable(value = "id") Integer id, @RequestParam(name = "action", required = false) String action) {
    agentService.unblockAgent(id, action);
    return accepted().build();
  }

  @PutMapping(value = "/player/{id}/block")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity blockPlayer(@PathVariable(value = "id") Integer id) {
    agentService.blockPlayer(id);
    return accepted().build();
  }

  @PutMapping(value = "/player/{id}/unblock")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity unblockPlayer(@PathVariable(value = "id") Integer id) {
    agentService.unblockPlayer(id);
    return accepted().build();
  }

  @PutMapping(value = "/collect")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity collect(@Valid @RequestBody CollectAgentDto data) {
    Agent agent = agentRepository.getEntity(agentService.getAgentForCurrentUser().getId());
    Agent subAgent = agentRepository.getEntity(data.getAgentId());

    try {
      semaphoreControlService.tryLock(agent.getId(), 0);
      semaphoreControlService.tryLock(subAgent.getId(), 0);
      agentService.collect(data.getAgentId(), data.getAmount(), data.getNote());
      semaphoreControlService.unlock(subAgent.getId());
      semaphoreControlService.unlock(agent.getId());
    } catch (SemaphoreControlNumberOfAttemptsException e) {
      log.info(e.getMessage());
      throw new RuntimeException(e);
    } catch (Exception e) {
      log.info(e.getMessage());
      semaphoreControlService.unlock(subAgent.getId());
      semaphoreControlService.unlock(agent.getId());
      throw new RuntimeException(e);
    }
    return accepted().build();
  }

  @PutMapping(value = "/payout")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity payout(@Valid @RequestBody PayoutAgentDto data) {
    Agent agent = agentRepository.getEntity(agentService.getAgentForCurrentUser().getId());
    Agent subAgent = agentRepository.getEntity(data.getAgentId());

    try {
      semaphoreControlService.tryLock(agent.getId(), 0);
      semaphoreControlService.tryLock(subAgent.getId(), 0);
      agentService.payout(data.getAgentId(), data.getAmount(), data.getNote());
      semaphoreControlService.unlock(subAgent.getId());
      semaphoreControlService.unlock(agent.getId());
    } catch (SemaphoreControlNumberOfAttemptsException e) {
      log.info(e.getMessage());
      throw new RuntimeException(e);
    } catch (Exception e) {
      log.info(e.getMessage());
      semaphoreControlService.unlock(subAgent.getId());
      semaphoreControlService.unlock(agent.getId());
      throw new RuntimeException(e);
    }
    return accepted().build();
  }

  @PostMapping(value = "/player/collect")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity collectFromPlayer(@Valid @RequestBody CollectPlayerDto data) {
    Agent agent = agentService.getAgentForCurrentUser();

    try {
      if (data.getPlayerId().compareTo(agent.getId()) != 0) {
        semaphoreControlService.tryLock(data.getPlayerId(), 0);
      }
      semaphoreControlService.tryLock(agent.getId(), 0);
      agentService.collectFromPlayer(data.getPlayerId(), data.getAmount(), data.getNote());
      semaphoreControlService.unlock(data.getPlayerId());
      semaphoreControlService.unlock(agent.getId());
    } catch (SemaphoreControlNumberOfAttemptsException e) {
      log.info(e.getMessage());
      throw new RuntimeException(e);
    } catch (Exception e) {
      log.info(e.getMessage());
      semaphoreControlService.unlock(data.getPlayerId());
      semaphoreControlService.unlock(agent.getId());
      throw new RuntimeException(e);
    }
    return accepted().build();
  }

  @PostMapping(value = "/player/payout")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity payoutToPlayer(@Valid @RequestBody PayoutPlayerDto data) {
    agentService.payoutToPlayer(data.getPlayerId(), data.getAmount(), data.getNote());
    return accepted().build();
  }

  @PostMapping(value = "/credits/deposit")
  @PreAuthorize("hasAnyAuthority('AGENT_DEFAULT','OPERATOR_DEFAULT')")
  public ResponseEntity depositCreditToAgent(@Valid @RequestBody AgentCreditOperation data) {
    AppUserDetails appUserDetails = (AppUserDetails) userProvider.getCurrentUserDetails();
    Agent creditAgent = agentRepository.getEntity(data.getAgentId());
    Agent agent = agentRepository.getEntity(appUserDetails.getId());

    try {
      semaphoreControlService.tryLock(agent.getId(), 0);
      semaphoreControlService.tryLock(creditAgent.getId(), 0);
      agentService.depositCreditToAgent(data.getAgentId(), data.getAmount(), data.getBonus());
      semaphoreControlService.unlock(creditAgent.getId());
      semaphoreControlService.unlock(agent.getId());
    } catch (SemaphoreControlNumberOfAttemptsException e) {
      log.info(e.getMessage());
      throw new RuntimeException(e);
    } catch (Exception e) {
      log.info(e.getMessage());
      semaphoreControlService.unlock(creditAgent.getId());
      semaphoreControlService.unlock(agent.getId());
      throw new RuntimeException(e);
    }
    return accepted().build();
  }

  @PostMapping(value = "/credits/withdraw")
  @PreAuthorize("hasAnyAuthority('AGENT_DEFAULT','OPERATOR_DEFAULT')")
  public ResponseEntity withdrawCreditsFromAgent(@Valid @RequestBody AgentCreditOperation data) {
    AppUserDetails appUserDetails = (AppUserDetails) userProvider.getCurrentUserDetails();
    Agent creditAgent = agentRepository.getEntity(data.getAgentId());
    Agent agent = agentRepository.getEntity(appUserDetails.getId());
    try {
      semaphoreControlService.tryLock(agent.getId(), 0);
      semaphoreControlService.tryLock(creditAgent.getId(), 0);
      agentService.withdrawCreditFromAgent(data.getAgentId(), data.getAmount());
      semaphoreControlService.unlock(creditAgent.getId());
      semaphoreControlService.unlock(agent.getId());
    } catch (SemaphoreControlNumberOfAttemptsException e) {
      log.info(e.getMessage());
      throw new RuntimeException(e);
    } catch (Exception e) {
      log.info(e.getMessage());
      semaphoreControlService.unlock(creditAgent.getId());
      semaphoreControlService.unlock(agent.getId());
      throw new RuntimeException(e);
    }
    return accepted().build();
  }

  @GetMapping(value = "/searchUsers-autocomplete")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT', 'READONLY_ADMIN')")
  public ResponseEntity<PageData<UserInfoDto>> searchUsersByUserNameAutoComplete(
          @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
          @SortDefault.SortDefaults({@SortDefault(sort = "user_name", direction = Sort.Direction.ASC)
          }) Pageable pageRequest,
          @RequestParam(name = "search") String search,
          @RequestParam(name = "role", required = false) UserRole role,
          @RequestParam(name = "currentUrl", required = false) String currentUrl,
          @RequestParam(name = "status", required = false) AgentStatusEnum status) {
    return ok(agentService.searchUsersByUserNameAutoComplete(pageRequest, search, status));

  }

  @PutMapping(value = "/inactiveAgents")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT','AGENT_DEFAULT')")
  public ResponseEntity inactiveAgents() {
    agentService.deactivationAgents();
    return accepted().build();

  }
}
