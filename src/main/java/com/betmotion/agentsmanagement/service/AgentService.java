package com.betmotion.agentsmanagement.service;

import static com.betmotion.agentsmanagement.dao.impl.UserDetailInfoDaoImpl.AGENT_ID;
import static com.betmotion.agentsmanagement.dao.impl.UserDetailInfoDaoImpl.MAPPING_USER_DETAIL_INFO;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.AGENT_USER_LOGIN_ID;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.EMPTY_PAGE_WITH_USERS;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.MAPPING_AGENT_DEACTIVATION;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.MAPPING_DIRECT_AGENT_SUM_CREDITS_AND_BALANCE;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.MAPPING_PLAYER_INFO;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.MAPPING_USER_INFO;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.PARENT_AGENT_ID;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.PARENT_AGENT_ID_STRING;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.SEARCH;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.STATUSES;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.SUB_AGENT_ID_LIST;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.URL_FINANCAS;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.URL_USUARIO;
import static com.betmotion.agentsmanagement.domain.UserStatus.ACTIVE;
import static com.betmotion.agentsmanagement.domain.UserStatus.BLOCKED;
import static com.betmotion.agentsmanagement.domain.UserTransactionType.DEPOSIT;
import static com.betmotion.agentsmanagement.domain.UserTransactionType.PAYMENT;
import static com.betmotion.agentsmanagement.domain.UserTransactionType.PAYOUT;
import static com.betmotion.agentsmanagement.domain.UserTransactionType.WITHDRAWAL;
import static com.betmotion.agentsmanagement.domain.WalletTransactionType.CREDIT;
import static com.betmotion.agentsmanagement.domain.WalletTransactionType.DEBIT;
import static com.betmotion.agentsmanagement.service.converter.AgentConverter.buildHierarchy;
import static com.betmotion.agentsmanagement.service.converter.AgentConverter.convertUser;
import static com.betmotion.agentsmanagement.service.converter.UserConverter.convertPlayer;
import static com.betmotion.agentsmanagement.utils.Constants.DEFAULT_BORN_DATE;
import static com.betmotion.agentsmanagement.utils.Constants.EMPTY_STRING;
import static com.betmotion.agentsmanagement.utils.Constants.IN_LIMIT_SIZE;
import static com.betmotion.agentsmanagement.utils.Constants.PLATFORM_AGENT_USERNAME;
import static com.betmotion.agentsmanagement.utils.NameUtils.joinPartForName;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.List.of;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.betmotion.agentsmanagement.domain.*;
import com.betmotion.agentsmanagement.platform.api.dto.RegisterUserResponseDto;
import com.betmotion.agentsmanagement.platform.queue.PlatformQueueAdapter;
import com.betmotion.agentsmanagement.queue.dto.TransactionConfirmStatusDto;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.betmotion.agentsmanagement.dao.AgentPlayerRepository;
import com.betmotion.agentsmanagement.dao.AgentRepository;
import com.betmotion.agentsmanagement.dao.ComissionLogsRepository;
import com.betmotion.agentsmanagement.dao.PlayerRepository;
import com.betmotion.agentsmanagement.dao.PlayerWalletRepository;
import com.betmotion.agentsmanagement.dao.UserRepository;
import com.betmotion.agentsmanagement.dao.WalletRepository;
import com.betmotion.agentsmanagement.dao.impl.UserDetailInfoDaoImpl;
import com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl;
import com.betmotion.agentsmanagement.dao.projection.PlayerInfo;
import com.betmotion.agentsmanagement.dao.projection.UserSumCreditsAndBalance;
import com.betmotion.agentsmanagement.platform.api.PlatformApi;
import com.betmotion.agentsmanagement.platform.api.dto.FindByIdsRequestDto;
import com.betmotion.agentsmanagement.platform.api.dto.PlatformUserDto;
import com.betmotion.agentsmanagement.rest.dto.AgentStatusEnum;
import com.betmotion.agentsmanagement.rest.dto.PageData;
import com.betmotion.agentsmanagement.rest.dto.agent.AgentDeactivationDto;
import com.betmotion.agentsmanagement.rest.dto.agent.AgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.CreateAgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.ExtendedInfoAgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.HierarchAgentDto;
import com.betmotion.agentsmanagement.rest.dto.agent.HierarchyAware;
import com.betmotion.agentsmanagement.rest.dto.agent.UpdateAgentDto;
import com.betmotion.agentsmanagement.rest.dto.player.ChangePasswordPlayerDto;
import com.betmotion.agentsmanagement.rest.dto.player.CreatePlayerDto;
import com.betmotion.agentsmanagement.rest.dto.player.PlayerDetailDto;
import com.betmotion.agentsmanagement.rest.dto.player.PlayerDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserDetailInfoDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserInfoDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserSumCreditsBalancesDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserTypeIntervalEnum;
import com.betmotion.agentsmanagement.security.AppUserDetails;
import com.betmotion.agentsmanagement.service.converter.AgentConverter;
import com.betmotion.agentsmanagement.service.exceptions.ServiceException;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class AgentService {

  UserRepository userRepository;

  PasswordEncoder passwordEncoder;

  WalletService walletService;

  AgentRepository agentRepository;

  UserProvider userProvider;

  WalletRepository walletRepository;

  AgentCodeGeneratorService agentCodeGeneratorService;

  AgentPlayerRepository agentPlayerRepository;

  PlayerRepository playerRepository;

  UserTransactionService userTransactionService;

  WalletTransactionService walletTransactionService;

  PlayerWalletService playerWalletService;

  PlayerWalletRepository playerWalletRepository;

  PlatformApiService platformApiService;
  EntityManager em;
  UserInfoDaoImpl userInfoDao;
  UserDetailInfoDaoImpl userDetailInfoDao;
  AgentTransactionService agentTransactionService;
  PlatformApi platformApi;
  UserTransactionIntervalService userTransactionIntervalService;
  ComissionLogsRepository comissionLogsRepository;
  @Autowired
  UserService userService;
  private PlatformQueueAdapter platformQueueAdapter;
  private static final String ALL_HIERARCHY = "hierarchy";

  @Transactional
  public AgentDto create(CreateAgentDto dto) {
    checkUserWithNameAlreadyExists(dto);
    Agent parentAgent = validateAgentCanCreateSubAgentsAndReturnParentAgent();
    Agent agent = new Agent();
    agent.setWallet(walletService.createWallet(FALSE));
    agent.setCreditsWallet(walletService.createWallet(TRUE));
    agent.setCommission(dto.getCommission());
    agent.setCommissionType(dto.getCommissionType());
    agent.setCommissionCasino(dto.getCommissionCasino());
    agent.setCommissionSlots(dto.getCommissionSlots());
    agent.setCommissionSports(dto.getCommissionSports());
    agent.setLastCommissionUpdate(LocalDateTime.now());
    agent.setCanHaveSubAgents(dto.isCanHaveSubAgents());
    agent.setParentAgent(parentAgent);
    User user = new User();
    Date currentDate = new Date();
    user.setEmail(dto.getEmail());
    user.setBornDate(DEFAULT_BORN_DATE);
    user.setPhone(dto.getPhoneNumber());
    user.setLastLogin(currentDate);
    user.setReceiveEmail(TRUE);
    user.setTestUser(FALSE);
    user.setStatus(ACTIVE);
    user.setCreatedOn(currentDate);
    user.setUserName(dto.getUserName());
    user.setFirstName(dto.getFullName());
    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    user.setCurrency(Currency.ARS);
    user.setRole(UserRole.AGENT);
    userService.clearUserCache(user);
    userRepository.save(user);
    agent.setUser(user);
    agent.setCode(agentCodeGeneratorService.generateNewCode());
    agent.setBalance(0L);
    agentRepository.save(agent);
    agent.setParentTree(String.format("%s%s,", parentAgent.getParentTree(), agent.getId().toString()));
    agentRepository.save(agent);

    em.createNativeQuery(userInfoDao.setAgentPermission())
            .setParameter(1, user.getId()).executeUpdate();

    LogCommissionUpdate(agent.getId(), dto.getParentAgentId(), dto.getCommission(),
            dto.getCommissionSlots(), dto.getCommissionCasino(), dto.getCommissionSports(), dto.getCommissionType());

    return convertUser(agent);
  }

  @Transactional(readOnly = true)
  public Optional<AgentDto> getById(Integer id) {
    Agent agent = agentRepository.getEntity(id);
    AgentDto agentDto = AgentConverter.convertUser(agent);
    AppUserDetails appUserDetails = (AppUserDetails) userProvider.getCurrentUserDetails();
    Integer currentLoggedAgentId = appUserDetails.getId();
    buildChildHierarchy(agent, currentLoggedAgentId, agentDto, new Stack<>());
    return Optional.of(agentDto);
  }

  @Transactional(readOnly = true)
  public Optional<PageData<AgentDto>> getAgentListByParentId(Pageable pageRequest,
                                                             Integer parentId) {
    Page<Agent> rawData = agentRepository.findAllByParentId(pageRequest, parentId);
    Page<AgentDto> result = rawData.map(AgentConverter::convertUser);
    return Optional.of(new PageData<>(result.getContent(), result.getTotalPages(),
            result.getTotalElements()));
  }

  @Transactional
  public Optional<AgentDto> updateAgent(Integer id, UpdateAgentDto dto) {
    return agentRepository.findActiveAgentById(id).map(item -> updateAgent(item, dto));
  }

  @Transactional
  public Optional<AgentDto> delete(Integer id) {
    return agentRepository.findActiveAgentById(id).map(this::deleteAgent);
  }

  @Transactional(readOnly = true)
  public PageData<AgentDto> getAll(Pageable pageRequest, Integer parentId) {
    Page<Agent> rawData = parentId != null
            ? agentRepository.findActiveAgentsChildAgents(pageRequest, parentId)
            : agentRepository.findActiveAgents(pageRequest);
    Page<AgentDto> activeAgents = rawData.map(AgentConverter::convertUser);
    return new PageData<>(activeAgents.getContent(), activeAgents.getTotalPages(),
            activeAgents.getTotalElements());
  }

  @Transactional(readOnly = true)
  public PageData<AgentDto> searchAgents(Pageable pageRequest, String text) {
    String textForSearch = "%" + text + "%";
    Page<Agent> rawData = agentRepository.findActiveAgentsByText(pageRequest, textForSearch);
    Page<AgentDto> activeAgents = rawData.map(AgentConverter::convertUser);
    return new PageData<>(activeAgents.getContent(), activeAgents.getTotalPages(),
            activeAgents.getTotalElements());
  }

  @Transactional(readOnly = true)
  public UserDetailInfoDto getCurrentUserDetailInfo(Integer id) {
    Query queryUsers = em.createNativeQuery(userDetailInfoDao
            .findUserDetailInfoForAgentSql(), MAPPING_USER_DETAIL_INFO);
    queryUsers.setParameter(AGENT_ID, id);
    UserDetailInfoDto result = userDetailInfoDao.getUserDetailInfo(queryUsers);
    result.setExtendedInfoAgentDto(getExtendedInfoAgentDto(id));
    return result;
  }

  @Transactional(readOnly = true)
  public UserDetailInfoDto getUserDetailInfo(Integer id, UserRole role) {
    Integer parentAgentId = getAgentIdForCurrentUser();
    UserDetailInfoDto result = new UserDetailInfoDto();
    if (UserRole.AGENT.equals(role)) {
      throwExceptionIfNotSubAgent(parentAgentId, id);
      Query queryUsers = em.createNativeQuery(userDetailInfoDao
              .findUserDetailInfoForAgentSql(), MAPPING_USER_DETAIL_INFO);
      queryUsers.setParameter(AGENT_ID, id);
      result = userDetailInfoDao.getUserDetailInfo(queryUsers);

      result.setExtendedInfoAgentDto(getExtendedInfoAgentDto(id));
    } else if (UserRole.PLAYER.equals(role)) {
      List<Integer> allSubAgentsByParentAgent = agentRepository.findAllSubAgentsByParentAgent(
              parentAgentId);
      Player player = playerRepository.findByPlatformId(id);
      AgentPlayer agentPlayer = agentPlayerRepository.findByPlayerId(player.getId());
      Integer agentId = agentPlayer.getAgentId();
      if (!allSubAgentsByParentAgent.contains(agentId)) {
        throw new ServiceException("PL01", new Object[] { id, parentAgentId });
      }
      result.setId(player.getPlatformId().longValue());
      result.setUserName(player.getUserName());

      List<PlatformUserDto> platformUserDtoList = platformApiService.getPlatformUserDtos(
              singletonList(player));
      if (platformUserDtoList.isEmpty()) {
        throw new ServiceException("PL02", new Object[] { id });
      }
      PlatformUserDto platformUserDto = platformUserDtoList.get(0);
      result.setFullName(platformUserDto.getUserName());
      result.setEmail(platformUserDto.getEmail());
      result.setRole(UserRole.PLAYER);
      result.setCreatedOn(null); // not ready in the platform
      result.setExtendedInfoAgentDto(getExtendedInfoAgentDto(agentId));
      Agent parentAgent = agentRepository.findById(agentId).orElseThrow();
      result.setParentUserName(parentAgent.getUser().getUserName());
    } else {
      throw new ServiceException("GE01", new Object[] { role });
    }
    return result;
  }

  @Transactional
  public Optional<AgentDto> linkUsersToAgent(Integer agentId, List<Integer> userIds) {
    Agent agent = agentRepository.getReferenceById(agentId);
    userRepository.linkUsersToAgent(userIds, agent);
    return getById(agentId);
  }

  @Transactional
  public void addCreditsToPlayer(Integer playerId, Long amount, Boolean forFree, Long bonus) {
    Agent agent = getAgentForCurrentUser();

    Wallet wallet = walletRepository.findAndLockById(agent.getWalletId());
    PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(playerId);
    playerWallet.setPlatformBalance(playerWallet.getPlatformBalance() + amount + bonus);
    Player player = playerRepository.getEntity(playerId);
    LocalDateTime transactionDate = LocalDateTime.now();

    // It means that player has to pay money to agent later
    playerWallet.setBalance(playerWallet.getBalance() + amount);
    // It means money come to player wallet in platform
    UserTransaction depositTransaction = userTransactionService.create(agent,
            playerId, DEPOSIT, amount, playerWallet.getBalance(), EMPTY_STRING, transactionDate, bonus);

    depositTransaction.setTransactionStatus(UserTransactionStatus.PENDING);
    userTransactionService.update(depositTransaction);

    saveTransactionUser(playerId, DEPOSIT, transactionDate, UserTypeIntervalEnum.PLAYER);

    Long chargedRemoteId = null;
    if (BooleanUtils.isFalse(forFree)) {
      // It means money moved from Player to agent
      playerWallet.setBalance(playerWallet.getBalance() - amount);
      UserTransaction chargedUserTransaction = userTransactionService.create(agent,
              playerId, PAYMENT, amount, playerWallet.getBalance(), EMPTY_STRING, transactionDate, null);
      chargedUserTransaction.setTransactionStatus(UserTransactionStatus.PENDING);
      userTransactionService.update(chargedUserTransaction);
      chargedRemoteId = chargedUserTransaction.getId().longValue();

      saveTransactionUser(playerId,PAYMENT, transactionDate, UserTypeIntervalEnum.PLAYER);
      // It means money come to agent wallet
      plusBalance(wallet, amount);
      walletTransactionService.saveTransaction(wallet.getId(), amount, CREDIT, wallet.getBalance());
    }
    playerWalletRepository.save(playerWallet);

    // It means money left credit wallet of agent
    Wallet creditWallet = agent.getCreditsWallet();
    minusBalance(creditWallet, amount + bonus);
    Long creditsAfterOperation = creditWallet.getBalance();
    checkBalanceIsPositiveAfterOperation(amount + bonus, creditsAfterOperation);
    walletTransactionService.saveTransaction(creditWallet.getId(), amount + bonus, DEBIT,
            creditsAfterOperation);

    userService.clearUserCache(agent.getUser());

    platformApiService.deposit(player, amount + bonus, depositTransaction.getId().longValue(), chargedRemoteId);
  }

  @Transactional
  public void withdrawCreditsFromPlayer(Integer playerId, Long amount, Boolean forFree) {
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    if (userProvider.isAgent(currentUserDetails)) {
      AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
      Agent agent = agentRepository.getEntity(appUserDetails.getId());

      // This operation of increase of credits on credit wallet of agent
      Wallet wallet = walletRepository.findAndLockById(agent.getCreditWalletId());
      plusBalance(wallet, amount);
      walletTransactionService.saveTransaction(agent.getCreditWalletId(), amount,
              CREDIT, wallet.getBalance());

      // It means decrease of platform balance
      Player player = playerRepository.getEntity(playerId);
      PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(playerId);

      Query query = em.createNativeQuery(userInfoDao.getCreditsByUserName());
      query.setParameter("username", player.getUserName());
      Long creditsFromApi = ((BigInteger) query.getSingleResult()).longValue();

      playerWallet.setPlatformBalance(creditsFromApi - amount);

      checkBalanceIsPositiveAfterOperation(amount, playerWallet.getPlatformBalance());
      playerWalletRepository.save(playerWallet);

      // Log withdrawal operation
      playerWallet.setBalance(playerWallet.getBalance() - amount);
      LocalDateTime transactionDate = LocalDateTime.now();
      UserTransaction withDrawTransaction = userTransactionService
              .create(agent, playerId, WITHDRAWAL, amount,
                      playerWallet.getBalance(), EMPTY_STRING, transactionDate, null);
      saveTransactionUser(playerId, WITHDRAWAL, transactionDate, UserTypeIntervalEnum.PLAYER );
      withDrawTransaction.setTransactionStatus(UserTransactionStatus.PENDING);
      userTransactionService.update(withDrawTransaction);
      userService.clearUserCache(agent.getUser());
      Long chargedRemoteId = null;
      if (isTrue(forFree)) {
        // It means that agent debt is increasing
        playerWalletRepository.save(playerWallet);
      } else {
        // Pay cash to user
        playerWallet.setBalance(playerWallet.getBalance() + amount);
        UserTransaction chargedUserTransaction = userTransactionService
                .create(agent, playerId, PAYOUT, amount,
                        playerWallet.getBalance(), EMPTY_STRING, transactionDate, null);
        chargedUserTransaction.setTransactionStatus(UserTransactionStatus.PENDING);
        userTransactionService.update(chargedUserTransaction);
        chargedRemoteId = chargedUserTransaction.getId().longValue();
        Wallet walletWithMoney = walletRepository.findAndLockById(agent.getWalletId());
        minusBalance(walletWithMoney, amount);
        walletTransactionService.saveTransaction(walletWithMoney.getId(), amount, DEBIT,
                walletWithMoney.getBalance());
      }
      platformApiService.withdraw(amount, player, withDrawTransaction, chargedRemoteId);
    }
  }

  @Transactional
  public Optional<PlayerDto> newPlayer(CreatePlayerDto dto) {
    String userName = dto.getUserName();
    Agent agent = getPlatformAgent();
    User user = createNewUser(dto, agent.getUserId());

    checkIfUserNameAlreadyExists(userName);
    checkIfEmailAlreadyExists(dto.getEmail());

    saveNewUser(user);

    Player player = createNewPlayer(userName, user.getId());
    saveNewPlayer(player, user);

    AgentPlayer agentPlayer = createNewAgentPlayer(agent, player.getId());
    saveNewAgentPlayer(agentPlayer, player, user);

    saveNewPlayerWallet(player, user);

    Integer userPlatformId = registerUserInPlatform(dto, agent);
    updateNewUserWithPlatformId(player, userPlatformId, user);

    return Optional.of(convertPlayer(user));
  }

  @Transactional
  public Optional<PlayerDto> createPlayer(CreatePlayerDto dto) {
    String userName = dto.getUserName();
    Agent agent = getAgentFromCurrentUser(getAgentIdForCurrentUser());
    User user = createNewUser(dto, agent.getUserId());

    checkIfUserNameAlreadyExists(userName);
    checkIfEmailAlreadyExists(dto.getEmail());
    checkIfUserNameIsTheSameOfAnyAgent(userName);

    saveNewUser(user);

    Player player = createNewPlayer(userName, user.getId());
    saveNewPlayer(player, user);

    AgentPlayer agentPlayer = createNewAgentPlayer(agent, player.getId());
    saveNewAgentPlayer(agentPlayer, player, user);

    saveNewPlayerWallet(player, user);

    Integer userPlatformId = registerUserInPlatform(dto, agent);
    updateNewUserWithPlatformId(player, userPlatformId, user);

    return Optional.of(convertPlayer(user));
  }

  @Transactional
  public AgentDto changePassword(Integer id, String password) {
    Agent agent = agentRepository.getEntity(id);
    User user = agent.getUser();
    user.setPassword(passwordEncoder.encode(password));
    userService.clearUserCache(user);
    agentRepository.save(agent);
    return convertUser(agent);
  }

  @Transactional(readOnly = true)
  public PageData<UserInfoDto> getAllUsersForSubAgent(Pageable pageRequest,
                                                      Integer subAgentUserId, boolean withSubAgent) {
    throwExceptionIfNotSubAgent(getAgentIdForCurrentUser(), subAgentUserId);
    List<Integer> allSubAgentListForSubAgent = agentRepository.findAllSubAgentsByParentAgent(
            subAgentUserId);
    if (withSubAgent) {
      allSubAgentListForSubAgent.add(subAgentUserId);
    }
    return getAllUsers(pageRequest, allSubAgentListForSubAgent, subAgentUserId);
  }

  @Transactional(readOnly = true)
  public PageData<UserInfoDto> getDirectUsersForSubAgent(Pageable pageRequest,Integer subAgentUserId, UserRole role,
                                                         AgentStatusEnum statusDto, String currentUrl) {
    throwExceptionIfNotSubAgent(getAgentIdForCurrentUser(), subAgentUserId);
    return getDirectUsers(pageRequest, subAgentUserId, role, statusDto, currentUrl);
  }

  @Transactional(readOnly = true)
  public PageData<UserInfoDto> getDirectUsers(Pageable pageRequest, UserRole userRole,
                                              AgentStatusEnum status, String currentUrl) {
    Integer parentAgentId = getAgentIdForCurrentUser();
    return getDirectUsers(pageRequest, parentAgentId, userRole, status, currentUrl);
  }

  @Transactional(readOnly = true)
  public UserSumCreditsBalancesDto getDirectUsersSumCreditsAndBalance(String search, String role,
                                                                      AgentStatusEnum status, Integer parentAgentId) {

    UserSumCreditsBalancesDto result = sumCreditsBalanceForTree(search, role, status);

    if(Optional.ofNullable(result).isPresent())
      return result;

    List<String> statusesForSearch = convertToDbData(status);

    if (StringUtils.hasText(role)) {
      if (role.equals("AGENT")) {
        result = agentsSumCreditsAndBalance(search, parentAgentId, statusesForSearch);
      }

      if (role.equals("PLAYER")) {
        result = playersSumCreditsAndBalance(search, parentAgentId, statusesForSearch);
      }
    } else {
      UserSumCreditsBalancesDto agentsResult = agentsSumCreditsAndBalance(search, parentAgentId, statusesForSearch);
      UserSumCreditsBalancesDto playersResult = playersSumCreditsAndBalance(search, parentAgentId, statusesForSearch);

      Long totalBalance = agentsResult.getBalance() + playersResult.getBalance();
      Long totalCredits = agentsResult.getCredits() + playersResult.getCredits();

      UserSumCreditsBalancesDto totalResult = new UserSumCreditsBalancesDto(totalBalance, totalCredits);
      result = totalResult;
    }

    return result;
  }

  @Transactional(readOnly = true)
  public PageData<UserInfoDto> getAllUsers(Pageable pageRequest) {
    Integer parentAgentId = -1;
    List<Integer> subAgentIdList = emptyList();
    AppUserDetails currentUserDetails = (AppUserDetails) userProvider.getCurrentUserDetails();
    Agent agent = agentRepository.getEntity(currentUserDetails.getId());
    if (agent.getParentId() != null) {
      parentAgentId = getAgentIdForCurrentUser();
      subAgentIdList = agentRepository.findAllSubAgentsByParentAgent(parentAgentId);
    } else {
      subAgentIdList = agentRepository.findAllIdsOfAgentsByRole(UserRole.AGENT);
    }
    return getAllUsers(pageRequest, subAgentIdList, parentAgentId);
  }

  @Transactional(readOnly = true)
  public PageData<UserInfoDto> getAllUsers(Pageable pageRequest, UserRole role) {
    PageData<UserInfoDto> result;
    if (UserRole.AGENT.equals(role)) {
      result = getAllSubAgents(pageRequest);
    } else if (UserRole.PLAYER.equals(role)) {
      result = getAllPlayers(pageRequest);
    } else {
      throw new ServiceException("UR00", new Object[] { role });
    }
    return result;
  }

  @Transactional(readOnly = true)
  public PageData<UserInfoDto> searchUsersByUserName(Pageable pageRequest, String search,
                                                     UserRole role, AgentStatusEnum status, String currentUrl) {
    String[] sortParam = pageRequest.getSort().toString().split(":"); // Default para user_name
    String sortColumn = sortParam[0];
    boolean orderByValue = false;
    if(URL_USUARIO.equals(currentUrl) && sortColumn.equals("money")){
      sortColumn = "credits";
      orderByValue = true;
    }
    if(URL_FINANCAS.equals(currentUrl) && sortColumn.equals("money")){
      sortColumn = "balance";
    }
    String sortOrder = sortParam[1];
    String orderByClause = sortColumn + " " + sortOrder;

    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
    Integer parentAgentId = appUserDetails.getId();
    Agent agentFromDatabase = agentRepository.getEntity(appUserDetails.getId());
    if (agentFromDatabase.getParentId() == null) {
      parentAgentId = -1;
    }
    search = buildSearchString(search);
    List<String> statusesForSearch = convertToDbData(status);
    if (role == null) {
      parentAgentId = ((AppUserDetails) currentUserDetails).getId();
      Query queryCount = em.createNativeQuery(userInfoDao
              .countAllAgentsAndPlayersForAgentWithoutParentAgentSql());
      queryCount.setParameter(PARENT_AGENT_ID_STRING, String.format(",%s,", parentAgentId));
      queryCount.setParameter(PARENT_AGENT_ID, parentAgentId);
      queryCount.setParameter(SEARCH, search);
      queryCount.setParameter(STATUSES, statusesForSearch);
      long totalElements = ((Number) queryCount.getSingleResult()).longValue();
      if (totalElements > 0) {
        Query queryUsers = em.createNativeQuery(userInfoDao
                .findAllAgentsAndPlayersForAgentWithSearchByUserNameSql(orderByClause), MAPPING_USER_INFO);
        queryUsers.setParameter(PARENT_AGENT_ID, parentAgentId);
        queryUsers.setParameter(PARENT_AGENT_ID_STRING, String.format(",%s,", parentAgentId));
        queryUsers.setParameter(SEARCH, search);
        queryUsers.setParameter(STATUSES, statusesForSearch);

        return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers, orderByValue);
      }
      return EMPTY_PAGE_WITH_USERS;
    }
    if (UserRole.AGENT.equals(role)) {
      Query queryCount = em.createNativeQuery(userInfoDao
              .countAllAgentsForAgentWithoutParentAgentSql());
      queryCount.setParameter(PARENT_AGENT_ID, parentAgentId);
      queryCount.setParameter(PARENT_AGENT_ID_STRING, String.format(",%s,", parentAgentId));
      queryCount.setParameter(SEARCH, search);
      queryCount.setParameter(STATUSES, statusesForSearch);
      long totalElements = ((Number) queryCount.getSingleResult()).longValue();
      if (totalElements > 0) {
        Query queryUsers = em.createNativeQuery(userInfoDao
                .findAllAgentsForAgentWithSearchByUserNameSql(orderByClause), MAPPING_USER_INFO);
        queryUsers.setParameter(PARENT_AGENT_ID, parentAgentId);
        queryUsers.setParameter(PARENT_AGENT_ID_STRING, String.format(",%s,", parentAgentId));
        queryUsers.setParameter(SEARCH, search);
        queryUsers.setParameter(STATUSES, statusesForSearch);
        return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers, orderByValue);
      }
      return EMPTY_PAGE_WITH_USERS;
    }
    if (UserRole.PLAYER.equals(role)) {
      appUserDetails = (AppUserDetails) currentUserDetails;
      parentAgentId = appUserDetails.getId();
      Query queryCount = em.createNativeQuery(userInfoDao
              .countAllPlayersForAgentWithoutParentAgentSql());
      queryCount.setParameter(PARENT_AGENT_ID_STRING, String.format(",%s,", parentAgentId));
      queryCount.setParameter(SEARCH, search);
      queryCount.setParameter(STATUSES, statusesForSearch);
      long totalElements = ((Number) queryCount.getSingleResult()).longValue();
      if (totalElements > 0) {
        Query queryUsers = em.createNativeQuery(userInfoDao
                .findAllPlayersForAgentWithSearchByUserNameSql(orderByClause), MAPPING_USER_INFO);
        queryUsers.setParameter(PARENT_AGENT_ID_STRING, String.format(",%s,", parentAgentId));
        queryUsers.setParameter(SEARCH, search);
        queryUsers.setParameter(STATUSES, statusesForSearch);
        return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers, orderByValue);
      }
      return EMPTY_PAGE_WITH_USERS;
    }
    throw new IllegalArgumentException("Unsupportable role + " + role.name());
  }

  @Transactional(readOnly = true)
  public ExtendedInfoAgentDto getAgentHierarchy(Integer agentId) {
    Agent agent = agentRepository.findByIdFetchUser(agentId);
    ExtendedInfoAgentDto result = new ExtendedInfoAgentDto();
    result.setId(agent.getId());
    result.setUserName(agent.getUser().getUserName());
    result.setChild(buildHierarchyList(agentRepository.findAllAgentTree(String.format(",%s,", agentId), agentId), agent.getId()));

    return result;
  }

  @Transactional(readOnly = true)
  public ExtendedInfoAgentDto getExtendedInfoById(Integer id) {
    UserDetails userDetails = userProvider.getCurrentUserDetails();
    AppUserDetails appUserDetails = (AppUserDetails) userDetails;
    Agent agent = agentRepository.getEntity(appUserDetails.getId());
    ExtendedInfoAgentDto result = new ExtendedInfoAgentDto();
    result.setId(agent.getId());
    result.setUserName(agent.getUser().getUserName());
    List<Integer> agentIds = agentRepository.findAllSubAgentsByParentAgent(agent.getId());

    if (!isEmpty(agentIds)) {
      result.setChild(buildHierarchyList(findAgentByIds(agentIds), agent.getId()));
    }
    return result;
  }

  @Transactional
  public void blockAgent(Integer agentId) {
    changeAgentAllStatus(agentId, BLOCKED);
  }

  @Transactional
  public void unblockAgent(Integer agentId, String action) {
    if(ALL_HIERARCHY.equals(action)){
      changeAgentAllStatus(agentId, ACTIVE);
    }else{
      changeAgentStatus(agentId, ACTIVE);
    }
  }

  @Transactional
  public void blockPlayer(Integer playerId) {
    changePlayerStatus(playerId,
            com.betmotion.agentsmanagement.platform.api.dto.UserStatus.BLOCKED, BLOCKED);
  }

  @Transactional
  public void unblockPlayer(Integer playerId) {
    changePlayerStatus(playerId,
            com.betmotion.agentsmanagement.platform.api.dto.UserStatus.ACTIVE, ACTIVE);
  }

  @Transactional
  public PlayerDto changePasswordForPlayer(ChangePasswordPlayerDto dto) {
    Player player = playerRepository.getEntity(dto.getPlayerId());
    platformApiService.changePassword(player, dto.getPassword());
    return convertPlayerToDto(player);
  }

  @Transactional
  public void collect(Integer subAgentId, Long amount, String note) {
    Agent agent = agentRepository.getEntity(getAgentIdForCurrentUser());
    Agent subAgent = agentRepository.getEntity(subAgentId);

    throwExceptionIfNotSubAgent(agent.getId(), subAgent.getId());
    subAgent.setBalance(subAgent.getBalance() - amount);
    agentTransactionService.createTransaction(agent.getUser(), subAgent.getUser(), amount,
            AgentTransactionType.REMOVE_SALDO, note, subAgent.getBalance(), null);
    balanceTransfer(subAgent.getWallet(), agent.getWallet(), amount, note);
    userService.clearUserCache(subAgent.getUser());
    agentRepository.save(subAgent);
  }

  @Transactional
  public void payout(Integer subAgentId, Long amount, String note) {
    Agent agent = agentRepository.getEntity(getAgentIdForCurrentUser());
    Agent subAgent = agentRepository.getEntity(subAgentId);

    throwExceptionIfNotSubAgent(agent.getId(), subAgent.getId());
    subAgent.setBalance(subAgent.getBalance() + amount);
    agentTransactionService.createTransaction(agent.getUser(), subAgent.getUser(), amount,
            AgentTransactionType.ADD_SALDO, note, subAgent.getBalance(), null);
    userService.clearUserCache(subAgent.getUser());
    agentRepository.save(subAgent);
    balanceTransfer(agent.getWallet(), subAgent.getWallet(), amount, note);
  }

  @Transactional
  public void collectFromPlayer(Integer playerId, Long amount, String note) {
    Agent agent = getAgentForCurrentUser();
    plusBalance(agent.getWallet(), amount);
    walletTransactionService.saveTransaction(agent.getWalletId(), amount, CREDIT, note,
            agent.getWallet().getBalance());

    PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(playerId);
    playerWallet.setBalance(playerWallet.getBalance() - amount);
    playerWalletRepository.save(playerWallet);
    LocalDateTime transactionDate = LocalDateTime.now();
    userTransactionService.create(agent, playerId, PAYMENT, amount,
            playerWallet.getBalance(), note, transactionDate, null);

  }

  @Transactional
  public void payoutToPlayer(Integer playerId, Long amount, String note) {
    Agent agent = getAgentForCurrentUser();
    minusBalance(agent.getWallet(), amount);
    walletTransactionService.saveTransaction(agent.getWalletId(), amount, DEBIT, note,
            agent.getWallet().getBalance());

    PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(playerId);
    playerWallet.setBalance(playerWallet.getBalance() + amount);
    playerWalletRepository.save(playerWallet);
    LocalDateTime transactionDate = LocalDateTime.now();
    userTransactionService.create(agent, playerId, PAYOUT, amount,
            playerWallet.getBalance(), note, transactionDate, null);
  }

  @Transactional
  public void depositCreditToAgent(Integer agentId, Long amount, Long bonus) {
    AppUserDetails appUserDetails = (AppUserDetails) userProvider.getCurrentUserDetails();
    Agent creditAgent = agentRepository.getEntity(agentId);
    Agent agent = agentRepository.getEntity(appUserDetails.getId());

    transferBetweenWallets(agent.getCreditsWallet(), creditAgent.getCreditsWallet(), amount + (bonus == null ? 0 : bonus));
    checkBalanceIsPositiveAfterOperation(amount + (bonus == null ? 0 : bonus), agent.getCreditsWallet().getBalance());
    agentTransactionService.createTransaction(agent.getUser(), creditAgent.getUser(), amount,
            AgentTransactionType.DEPOSIT, "", creditAgent.getCreditsWallet().getBalance(), bonus);
    saveTransactionUser(creditAgent.getUserId(), DEPOSIT, LocalDateTime.now(), UserTypeIntervalEnum.AGENT);
    userService.clearUserCache(agent.getUser());
    userService.clearUserCache(creditAgent.getUser());
    agentRepository.save(creditAgent);
  }

  @Transactional
  public void withdrawCreditFromAgent(Integer agentId, Long amount) {
    AppUserDetails appUserDetails = (AppUserDetails) userProvider.getCurrentUserDetails();
    Agent creditAgent = agentRepository.getEntity(agentId);
    Agent agent = agentRepository.getEntity(appUserDetails.getId());
    transferBetweenWallets(creditAgent.getCreditsWallet(), agent.getCreditsWallet(), amount);
    checkBalanceIsPositiveAfterOperation(amount, creditAgent.getCreditsWallet().getBalance());
    agentTransactionService.createTransaction(agent.getUser(), creditAgent.getUser(), amount,
            AgentTransactionType.WITHDRAWAL, "", creditAgent.getCreditsWallet().getBalance(), null);
    userService.clearUserCache(creditAgent.getUser());
    userService.clearUserCache(agent.getUser());
    agentRepository.save(creditAgent);
    saveTransactionUser(creditAgent.getUserId(), WITHDRAWAL, LocalDateTime.now(), UserTypeIntervalEnum.AGENT);
  }

  @Transactional
  public Agent getAgentForCurrentUser() {
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
    return agentRepository.getEntity(appUserDetails.getId());
  }

  @Transactional
  public PlayerDetailDto getPlayerDetailInfo(Integer id) {
    Player player = playerRepository.getEntity(id);
    AppUserDetails appUserDetails = (AppUserDetails) userProvider.getCurrentUserDetails();
    Integer currentLoggedAgentId = appUserDetails.getId();
    return convertToPlayerDetails(player, currentLoggedAgentId);
  }

  @Transactional(readOnly = true)
  public PageData<UserInfoDto> searchUsersByUserNameAutoComplete(Pageable pageRequest, String search, AgentStatusEnum status) {
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    Integer agentUserLoginId =  ((AppUserDetails) currentUserDetails).getId();
    Agent agentFromDatabase = agentRepository.getEntity(agentUserLoginId);
    if (agentFromDatabase.getParentId() == null) {
      agentUserLoginId = -1;
    }
    search = buildSearchString(search);
    List<String> statusesForSearch = convertToDbData(status);

    long totalElements = countAllAgentsAndPlayer(search, agentUserLoginId, statusesForSearch);

    Query queryUsers;
    if (totalElements > 0) {
      queryUsers = findAllAgentAndPlayersTransactionInterval(search, agentUserLoginId, statusesForSearch);
      return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers);
    }

    return EMPTY_PAGE_WITH_USERS;
  }

  @Transactional
  public UserSumCreditsBalancesDto sumCreditsBalanceForTree(String search, String role, AgentStatusEnum status) {
    if(search.isBlank())
      return null;
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
    UserRole userRole = role.isBlank() ? null : UserRole.valueOf(role);
    Integer parentAgentId = appUserDetails.getId();
    if(UserRole.AGENT.equals(userRole)) {
      return findAgentsBalance(search, status, parentAgentId);
    }

    if(UserRole.PLAYER.equals(userRole)) {
      return findPlayersBalance(search, status, parentAgentId);
    }

    UserSumCreditsBalancesDto agentsBalance = findAgentsBalance(search, status, parentAgentId);
    UserSumCreditsBalancesDto playersBalance = findPlayersBalance(search, status, parentAgentId);
    return UserSumCreditsBalancesDto.builder()
            .balance(agentsBalance.getBalance() + playersBalance.getBalance())
            .credits(agentsBalance.getCredits() + playersBalance.getCredits())
            .build();
  }

  @Transactional
  public void deactivationAgents() {
    log.info("Find the agents that need to be deactivated");
    List<AgentDeactivationDto> agentsUserIds = fetchAgentsForDeactivation();

    if (agentsUserIds.isEmpty()) {
      return;
    }

    List<Integer> userIds = extractIds(agentsUserIds, AgentDeactivationDto::getId);
    List<Integer> agentIds = extractIds(agentsUserIds, AgentDeactivationDto::getAgentId);

    log.info("Split users IDs into smaller batches");
    List<List<Integer>> batchesUsers = createBatches(userIds, 2000);

    log.info("Split agent IDs into smaller batches");
    List<List<Integer>> batches = createBatches(agentIds, 2000);

    log.info("Search for subagents related to agents");
    List<AgentDeactivationDto> subAgentsUserIdsBathes = fetchSubAgents(batches);

    log.info("Search for players related to agents");
    List<AgentDeactivationDto> playersUserIdsBathes = fetchPlayers(batches);

    List<Integer> subUserIds = extractIds(subAgentsUserIdsBathes, AgentDeactivationDto::getId);
    List<Integer> playersUserIds = extractIds(playersUserIdsBathes, AgentDeactivationDto::getId);
    List<String> playersNames = extractStrings(playersUserIdsBathes, AgentDeactivationDto::getUserName);
    log.info("Split sub agent IDs into smaller batches");
    List<List<Integer>> batchesSubUserIds = createBatches(subUserIds, 2000);
    log.info("Split players IDs into smaller batches");
    List<List<Integer>> batchesPlayersUserIds = createBatches(playersUserIds, 2000);

    log.info("Update the status of users and subagents");
    batchesUsers.forEach(batch -> deactivateUsers(batch, false));
    log.info("Update the status of subagents");
    batchesSubUserIds.forEach(batch -> deactivateUsers(batch, false));
    log.info("Update the status of players");
    batchesPlayersUserIds.forEach(batch -> deactivateUsers(batch, true));
    if(!playersNames.isEmpty()){
      sendToApi(playersNames);
    }
  }

  @Transactional
  public void confirmTransactionDeposit(TransactionConfirmStatusDto transactionConfirmStatusDto) {
    Optional<UserTransaction> userTransactionOptional = userTransactionService.confirmTransaction(transactionConfirmStatusDto);
    if (userTransactionOptional.isPresent() && UserTransactionStatus.FAILED.equals(transactionConfirmStatusDto.getStatus())){
      rollbackDepositTransaction(userTransactionOptional.get(), transactionConfirmStatusDto.getChargedRemoteId());
    }
  }

  @Transactional
  public void confirmTransactionWithdraw(TransactionConfirmStatusDto transactionConfirmStatusDto) {
    Optional<UserTransaction> userTransactionOptional = userTransactionService.confirmTransaction(transactionConfirmStatusDto);
    if (userTransactionOptional.isPresent() && UserTransactionStatus.FAILED.equals(transactionConfirmStatusDto.getStatus())){
      rollbackDepositWithdraw(userTransactionOptional.get(), transactionConfirmStatusDto.getChargedRemoteId());
    }
  }

  @Transactional
  public void rollbackDepositTransaction(UserTransaction userTransaction, Long chargedRemoteId) {

    Agent agent = agentRepository.findByUserId(userTransaction.getUserid());
    Wallet agentWallet = walletRepository.findAndLockById(agent.getCreditWalletId());

    Player player = playerRepository.getEntity(userTransaction.getPlayerid());
    PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(player.getId());

    plusBalance(agentWallet, userTransaction.getAmount() + userTransaction.getBonus());

    playerWallet.setPlatformBalance(playerWallet.getPlatformBalance() - userTransaction.getAmount() - userTransaction.getBonus());

    if (chargedRemoteId == null) {
      playerWallet.setBalance(playerWallet.getBalance() - userTransaction.getAmount());
    }

    walletRepository.save(agentWallet);
    playerWalletRepository.save(playerWallet);

    walletTransactionService.saveTransaction(agent.getWalletId(),
            userTransaction.getAmount(),
            CREDIT,
            agentWallet.getBalance());

    createTransactionAndComplete(userTransaction, agent, player, playerWallet, UserTransactionType.DEPOSIT_ROLLBACK, userTransaction.getBonus());

    processChargedTransaction(userTransaction, chargedRemoteId, agent, player, playerWallet, UserTransactionType.PAYMENT_ROLLBACK);
  }

  public void rollbackDepositWithdraw(UserTransaction userTransaction, Long chargedRemoteId) {

    Agent agent = agentRepository.findByUserId(userTransaction.getUserid());
    Player player = playerRepository.getEntity(userTransaction.getPlayerid());

    Wallet agentWallet = walletRepository.findAndLockById(agent.getCreditWalletId());
    PlayerWallet playerWallet = playerWalletRepository.findAndLockByPlayerId(player.getId());

    minusBalance(agentWallet, userTransaction.getAmount());
    playerWallet.setPlatformBalance(playerWallet.getPlatformBalance() + userTransaction.getAmount());

    if (chargedRemoteId == null) {
      playerWallet.setBalance(playerWallet.getBalance() + userTransaction.getAmount());
    }

    walletRepository.save(agentWallet);
    playerWalletRepository.save(playerWallet);

    createTransactionAndComplete(userTransaction, agent, player, playerWallet, UserTransactionType.WITHDRAWAL_ROLLBACK, 0L);


    processChargedTransaction(userTransaction, chargedRemoteId, agent, player, playerWallet, UserTransactionType.PAYOUT_ROLLBACK);
  }

  public void updateNewUserWithPlatformId(RegisterUserResponseDto registerUserResponseDto) {
    Player player = playerRepository.findByUserName(registerUserResponseDto.getUserName());
    player.setPlatformId(registerUserResponseDto.getId());
    playerRepository.saveAndFlush(player);
  }

  public void LogCommissionUpdate (Integer id, Integer parentId, BigDecimal commission, BigDecimal commission_slots,
                                   BigDecimal commission_casino, BigDecimal commission_sport, String commission_type) {

    ComissionLogs commissionUpdate = new ComissionLogs();
    commissionUpdate.setAgentId(id);
    commissionUpdate.setDate(LocalDateTime.now());
    commissionUpdate.setParentAgentId(parentId);
    commissionUpdate.setCommission(commission);
    commissionUpdate.setCommissionSlots(commission_slots);
    commissionUpdate.setCommissionCasino(commission_casino);
    commissionUpdate.setCommissionSports(commission_sport);
    commissionUpdate.setCommissionType(commission_type);

    comissionLogsRepository.save(commissionUpdate);
  }

  public static <T> Stream<List<T>> batches(List<T> source, int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("length = " + length);
    }
    int size = source.size();
    if (size <= 0) {
      return Stream.empty();
    }
    int fullChunks = (size - 1) / length;
    return IntStream.range(0, fullChunks + 1).mapToObj(
            n -> source.subList(n * length, n == fullChunks ? size : (n + 1) * length));
  }

  private static <T> List<List<T>> partitionList(List<T> list, int size) {
    List<List<T>> partitions = new ArrayList<>();
    for (int i = 0; i < list.size(); i += size) {
      partitions.add(list.subList(i, Math.min(i + size, list.size())));
    }
    return partitions;
  }

  private void changeStatusToApi(Integer playerId, UserStatus status) {
    Player player = playerRepository.findByUserId(playerId);
    if(BLOCKED.equals(status)){
      platformApiService.changePlayerStatusWithPlatformApi(player, com.betmotion.agentsmanagement
              .platform.api.dto.UserStatus.BLOCKED);

    }else{
      platformApiService.changePlayerStatusWithPlatformApi(player, com.betmotion.agentsmanagement
              .platform.api.dto.UserStatus.ACTIVE);
    }
  }

  private AgentDto deleteAgent(Agent item) {
    item.getUser().setStatus(BLOCKED);
    userService.clearUserCache(item.getUser());
    userRepository.save(item.getUser());
    return convertUser(item);
  }

  private void checkUserWithNameAlreadyExists(CreateAgentDto dto) {
    String userName = dto.getUserName();
    User userByName = userRepository.findByUserName(userName);
    if (userByName != null) {
      throw new ServiceException("AG06", new Object[] { userName });
    }
  }

  private Agent validateAgentCanCreateSubAgentsAndReturnParentAgent() {
    Agent agent = getAgentForCurrentUser();
    if (!agent.isCanHaveSubAgents()) {
      throw new ServiceException("AG01", null);
    }
    return agent;
  }

  private void buildChildHierarchy(Agent agent, Integer currentLoggedAgentId,
                                   HierarchyAware hierarchyAware, Stack<HierarchAgentDto> predefinedData) {
    boolean needExit = false;
    while (!needExit) {
      HierarchAgentDto hierarchAgentDto = buildHierarchy(agent, -1);
      predefinedData.push(hierarchAgentDto);
      if (agent.getId().equals(currentLoggedAgentId)) {
        break;
      }
      agent = agent.getParentAgent();
      needExit = agent == null;
    }
    if (!predefinedData.isEmpty()) {
      HierarchAgentDto currentItem = predefinedData.pop();
      hierarchyAware.setHierarchy(singletonList(currentItem));
      while (!predefinedData.isEmpty()) {
        HierarchAgentDto currentChild = predefinedData.pop();
        currentItem.setChild(singletonList(currentChild));
        currentItem = currentChild;
      }
    }
  }

  private AgentDto updateAgent(Agent item, UpdateAgentDto dto) {
    checkUserCanUpdateAgent(item);
    User user = item.getUser();
    user.setBornDate(DEFAULT_BORN_DATE);
    user.setEmail(dto.getEmail());
    user.setCurrency(Currency.ARS);
    user.setPhone(dto.getPhoneNumber());
    if (!dto.getUserName().isBlank()) {
      user.setUserName(dto.getUserName());
    }
    if (!dto.getFullName().isBlank()) {
      user.setFirstName(dto.getFullName());
    }
    userService.clearUserCache(user);
    userRepository.save(user);
    item.setCommission(dto.getCommission());
    item.setCommissionType(dto.getCommissionType());
    item.setCommissionCasino(dto.getCommissionCasino());
    item.setCommissionSlots(dto.getCommissionSlots());
    item.setCommissionSports(dto.getCommissionSports());
    item.setCanHaveSubAgents(dto.isCanHaveSubAgents());
    item.setLastCommissionUpdate(LocalDateTime.now());
    agentRepository.save(item);

    if (dto.getCommissionUpdated()) {
      LogCommissionUpdate(item.getId(), dto.getParentAgentId(), dto.getCommission(),
              dto.getCommissionSlots(), dto.getCommissionCasino(), dto.getCommissionSports(), dto.getCommissionType());
    }

    return convertUser(item);
  }

  private void checkUserCanUpdateAgent(Agent item) {
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    if (userProvider.isAgent(currentUserDetails)) {
      AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
      Integer currentUserId = appUserDetails.getUserId();
      if (item.getParentAgent() != null) {
        Integer parentUserId = item.getParentAgent().getUser().getId();
        if (!parentUserId.equals(currentUserId)) {
          throw new ServiceException("AG02", null);
        }
      }
    }
  }

  private ExtendedInfoAgentDto getExtendedInfoAgentDto(Integer agentId) {
    Agent agent = agentRepository.getEntity(agentId);
    Integer parentAgentId = getAgentIdForCurrentUser();
    ExtendedInfoAgentDto extendedInfoAgentDto = new ExtendedInfoAgentDto();
    extendedInfoAgentDto.setId(agent.getId());
    extendedInfoAgentDto.setUserName(agent.getUser().getUserName());
    buildChildHierarchy(agent, parentAgentId, extendedInfoAgentDto, new Stack<>());
    return extendedInfoAgentDto;
  }

  private void saveTransactionUser(Integer userPlayerId, UserTransactionType type, LocalDateTime transactionDate, UserTypeIntervalEnum typeUser) {
    userTransactionIntervalService.saveTransactionUser(userPlayerId, type, transactionDate, typeUser);
  }

  private void checkBalanceIsPositiveAfterOperation(Long amount, Long balaneAfterOperation) {
    if (balaneAfterOperation < 0) {
      throw new ServiceException("WB00", new Object[] { balaneAfterOperation, amount });
    }
  }

  private void checkIfUserNameIsTheSameOfAnyAgent(String username) {
    Agent agent = agentRepository.findByUserName(username);
    if (agent != null) {
      throw new ServiceException("PL06", null);
    }
  }

  private Integer registerUserInPlatform(CreatePlayerDto dto, Agent agent) {
    return platformApiService.registerUserInPlatform(dto, agent);
  }

  private void checkIfUserNameAlreadyExists(String userName) {
    Optional<User> alreadyExists = userRepository.findByUserNameAndStatusNot(userName, UserStatus.CLOSED);
    if (alreadyExists.isPresent()) {
      throw new ServiceException("PL03", null);
    }
  }

  private void checkIfEmailAlreadyExists(String email) {
    if (!email.isBlank()) {
      List<User> alreadyExists = userRepository.findByEmail(email);
      if (!alreadyExists.isEmpty()) {
        Optional<User> optionalUser = alreadyExists.stream().filter(e -> e.getStatus().equals(UserStatus.CLOSED)).findAny();
        if (optionalUser.isEmpty()) {
          throw new ServiceException("PL05", null);
        }
      }
    }
  }

  private Agent getAgentFromCurrentUser(Integer id) {
    Agent agent = agentRepository.getEntity(id);
    return agent;
  }

  private Agent getPlatformAgent() {
    Agent platformAgent = agentRepository.findByUserName(PLATFORM_AGENT_USERNAME);
    return platformAgent;
  }

  private User createNewUser(CreatePlayerDto dto, Integer currentUserId) {
    User user = new User();
    Date currentDate = new Date();
    user.setAgentId(currentUserId);
    user.setBornDate(DEFAULT_BORN_DATE);
    user.setCreatedOn(currentDate);
    user.setCurrency(Currency.ARS);
    user.setEmail(dto.getEmail());
    user.setLastLogin(currentDate);
    user.setPassword(dto.getPassword());
    user.setPhone(dto.getPhoneNumber());
    user.setReceiveEmail(false);
    user.setRole(UserRole.PLAYER);
    user.setStatus(ACTIVE);
    user.setTestUser(false);
    user.setUserName(dto.getUserName());
    return user;
  }

  private Player createNewPlayer(String userName, Integer userId) {
    Player player = new Player();
    player.setPlatformId(0);
    player.setUserName(userName);
    player.setStatus(ACTIVE);
    player.setUserId(userId);
    return player;
  }

  private AgentPlayer createNewAgentPlayer(Agent agent, Integer playerId) {
    AgentPlayer agentPlayer = new AgentPlayer();
    agentPlayer.setPlayerId(playerId);
    agentPlayer.setAgent(agent);
    return agentPlayer;
  }

  private void saveNewUser(User user) {
    try {
      userService.clearUserCache(user);
      userRepository.saveAndFlush(user);
    } catch (Exception e) {
      log.error("Error while saving on users table", e);
      throw new ServiceException("PL04", null);
    }
  }

  private void saveNewPlayer(Player player, User user) {
    try {
      playerRepository.saveAndFlush(player);
    } catch (Exception e) {
      userRepository.delete(user);
      log.error("Error while saving on players table", e);
      throw new ServiceException("PL04", null);
    }
  }

  private void saveNewAgentPlayer(AgentPlayer agentPlayer, Player player, User user) {
    try {
      agentPlayerRepository.saveAndFlush(agentPlayer);
    } catch (Exception e) {
      userRepository.delete(user);
      playerRepository.delete(player);
      log.error("Error while saving on agents_players table", e);
      throw new ServiceException("PL04", null);
    }
  }

  private void saveNewPlayerWallet(Player player, User user) {
    try {
      playerWalletService.createWalletForPlayer(player);
    } catch (Exception e) {
      userRepository.delete(user);
      playerRepository.delete(player);
      agentPlayerRepository.deleteByPlayer(player.getId());
      log.error("Error while saving on player_wallet table", e);
      throw new ServiceException("PL04", null);
    }
  }

  private void updateNewUserWithPlatformId(Player player, Integer platformId, User user) {
    try {
      player.setPlatformId(platformId);
      playerRepository.saveAndFlush(player);
    } catch (Exception e) {
      userRepository.delete(user);
      playerRepository.delete(player);
      agentPlayerRepository.deleteByPlayer(player.getId());
      playerWalletService.deleteByPlayer(player.getId());
      log.error("Error while creating player on Platform", e);
      throw new ServiceException("PL04", null);
    }
  }

  private PageData<UserInfoDto> getAllUsers(Pageable pageRequest,
                                            List<Integer> subAgentIdList,
                                            Integer parentAgentId) {
    Query queryCount = em.createNativeQuery(userInfoDao.countFindAllAgentsAndPlayersForAgentSql());
    queryCount.setParameter(SUB_AGENT_ID_LIST, subAgentIdList);
    queryCount.setParameter(PARENT_AGENT_ID, parentAgentId);
    long totalElements = ((Number) queryCount.getSingleResult()).longValue();
    Query queryUsers = em.createNativeQuery(userInfoDao.findAllAgentsAndPlayersForAgentSql(),
            MAPPING_USER_INFO);
    queryUsers.setParameter(SUB_AGENT_ID_LIST, subAgentIdList);
    queryUsers.setParameter(PARENT_AGENT_ID, parentAgentId);
    return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers);
  }

  private PageData<UserInfoDto> getDirectUsers(Pageable pageRequest, Integer parentAgentId,
                                               UserRole userRole, AgentStatusEnum status, String currentUrl) {
    List<String> statusesForSearch = convertToDbData(status);
    String[] sortParam = pageRequest.getSort().toString().split(":"); // Default para user_name
    String sortColumn = sortParam[0];
    boolean orderByValue = false;
    if(URL_USUARIO.equals(currentUrl) && sortColumn.equals("money")){
      sortColumn = "credits";
      orderByValue = true;
    }
    if(URL_FINANCAS.equals(currentUrl) && sortColumn.equals("money")){
      sortColumn = "balance";
    }
    String sortOrder = sortParam[1];
    String orderByClause = sortColumn + " " + sortOrder;

    if (userRole == null) {
      Query queryCount = em.createNativeQuery(userInfoDao
              .countDirectSubAgentsAndDirectPlayersForAgentListSql());
      queryCount.setParameter(PARENT_AGENT_ID, parentAgentId);
      queryCount.setParameter(STATUSES, statusesForSearch);
      long totalElements = ((Number) queryCount.getSingleResult()).longValue();
      if (totalElements > 0) {
        Query queryUsers = em.createNativeQuery(userInfoDao
                .findDirectSubAgentsAndDirectPlayersForAgentListSql(orderByClause), MAPPING_USER_INFO);
        queryUsers.setParameter(PARENT_AGENT_ID, parentAgentId);
        queryUsers.setParameter(STATUSES, statusesForSearch);
        return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers, orderByValue);
      }
      return EMPTY_PAGE_WITH_USERS;
    }
    if (UserRole.AGENT.equals(userRole)) {
      Query queryCount = em.createNativeQuery(userInfoDao
              .countDirectSubAgentsForAgentListSql());
      queryCount.setParameter(PARENT_AGENT_ID, parentAgentId);
      queryCount.setParameter(STATUSES, statusesForSearch);
      long totalElements = ((Number) queryCount.getSingleResult()).longValue();
      if (totalElements > 0) {
        Query queryUsers = em.createNativeQuery(userInfoDao
                .findDirectSubAgentsForAgentListSql(orderByClause), MAPPING_USER_INFO);
        queryUsers.setParameter(PARENT_AGENT_ID, parentAgentId);
        queryUsers.setParameter(STATUSES, statusesForSearch);
        return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers);
      }
      return EMPTY_PAGE_WITH_USERS;
    }
    if (UserRole.PLAYER.equals(userRole)) {
      Query queryCount = em.createNativeQuery(userInfoDao
              .countDirectPlayersForAgentListSql());
      queryCount.setParameter(PARENT_AGENT_ID, parentAgentId);
      queryCount.setParameter(STATUSES, statusesForSearch);
      long totalElements = ((Number) queryCount.getSingleResult()).longValue();
      if (totalElements > 0) {
        Query queryUsers = em.createNativeQuery(userInfoDao
                .findDirectPlayersForAgentListSql(orderByClause), MAPPING_USER_INFO);
        queryUsers.setParameter(PARENT_AGENT_ID, parentAgentId);
        queryUsers.setParameter(STATUSES, statusesForSearch);
        return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers, orderByValue);
      }
      return EMPTY_PAGE_WITH_USERS;
    }
    throw new IllegalArgumentException("Unsupportable user role: " + userRole);
  }

  private UserSumCreditsBalancesDto agentsSumCreditsAndBalance(String search, Integer parentAgentId,
                                                               List<String> statusesForSearch) {
    Query agentQuery = em.createNativeQuery(userInfoDao
            .sumDirectAgentsCreditsAndBalance(), MAPPING_DIRECT_AGENT_SUM_CREDITS_AND_BALANCE);
    agentQuery.setParameter("parentAgentId", parentAgentId);
    agentQuery.setParameter("statuses", statusesForSearch);
    agentQuery.setParameter("search", search);

    UserSumCreditsAndBalance sum = (UserSumCreditsAndBalance) agentQuery.getSingleResult();
    return UserSumCreditsBalancesDto.fromUserSumCreditsAndBalance(sum);
  }

  private UserSumCreditsBalancesDto playersSumCreditsAndBalance(String search, Integer parentAgentId,
                                                                List<String> statusesForSearch) {
    Query playerQuery = em.createNativeQuery(userInfoDao
            .directPlayersCreditsAndBalance(), MAPPING_PLAYER_INFO);
    playerQuery.setParameter("parentAgentId", parentAgentId);
    playerQuery.setParameter("statuses", statusesForSearch);
    playerQuery.setParameter("search", search);

    return getUserSumCreditsBalancesDto(playerQuery);
  }

  private UserSumCreditsBalancesDto getUserSumCreditsBalancesDto(Query playerQuery) {
    List<PlayerInfo> playerInfo = playerQuery.getResultList();
    Set<Integer> ids = playerInfo.stream().map(PlayerInfo::getId).collect(toSet());

    List<List<Integer>> idBatches = getPlayersIdsBatches(new ArrayList<>(ids));

    Long credits = 0L;
    for (List<Integer> batch : idBatches) {
      FindByIdsRequestDto requestDto = new FindByIdsRequestDto();
      requestDto.setUserIds(new HashSet<>(batch));
      credits += platformApi.findSumBYIds(requestDto);
    }

    Long balance = playerInfo.stream().mapToLong(PlayerInfo::getBalance).sum();
    return new UserSumCreditsBalancesDto(balance, credits);
  }

  private List<List<Integer>> getPlayersIdsBatches(List<Integer> lista) {
    Integer batchSize = 2000;
    List<List<Integer>> batches = new ArrayList<>();
    for (int i = 0; i < lista.size(); i += batchSize) {
      int lastPosition = Math.min(i + batchSize, lista.size());
      List<Integer> batch = lista.subList(i, lastPosition);
      batches.add(batch);
    }
    return batches;
  }

  private PageData<UserInfoDto> getAllSubAgents(Pageable pageRequest) {
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
    Agent agentFromDatabase = agentRepository.getEntity(appUserDetails.getId());
    if (agentFromDatabase.getParentId() != null) {
      Integer parentAgentId = getAgentIdForCurrentUser();
      List<Integer> subAgentIdList = agentRepository.findAllSubAgentsByParentAgent(parentAgentId);
      long totalElements = subAgentIdList.size();

      Query queryUsers = em.createNativeQuery(userInfoDao.findAllSubAgentsSql(), MAPPING_USER_INFO);
      queryUsers.setParameter(SUB_AGENT_ID_LIST, subAgentIdList);
      return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers);
    } else {
      UserRole userRole = UserRole.AGENT;
      long totalElements = agentRepository.countByRole(userRole);
      Query queryUsers = em.createNativeQuery(userInfoDao.findAllSubAgentsForOperatorSql(),
              MAPPING_USER_INFO);
      return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers);
    }
  }

  private PageData<UserInfoDto> getAllPlayers(Pageable pageRequest) {
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
    Agent agentFromDatabase = agentRepository.getEntity(appUserDetails.getId());
    if (agentFromDatabase.getParentId() != null) {
      Integer parentAgentId = getAgentIdForCurrentUser();
      List<Integer> subAgentIdList = agentRepository.findAllSubAgentsByParentAgent(
              parentAgentId);
      subAgentIdList.add(parentAgentId);
      Query queryCount = em.createNativeQuery(userInfoDao.countFindAllPlayersForAgentListSql());
      queryCount.setParameter(SUB_AGENT_ID_LIST, subAgentIdList);
      long totalElements = ((Number) queryCount.getSingleResult()).longValue();
      Query queryUsers = em.createNativeQuery(userInfoDao.findAllPlayersForAgentListSql(),
              MAPPING_USER_INFO);
      queryUsers.setParameter(SUB_AGENT_ID_LIST, subAgentIdList);
      return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers);
    } else {
      long totalElements = playerRepository.count();
      Query queryUsers = em.createNativeQuery(userInfoDao.findAllPlayersForOperatorSql(),
              MAPPING_USER_INFO);
      return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers);
    }
  }

  private String buildSearchString(String search) {
    if (search.isBlank() || search.length() < 3) {
      throw new ServiceException("SR00", new String[] { search });
    }
    return "%" + search + "%";
  }

  private List<Agent> findAgentByIds(List<Integer> agentIds) {
    if (!CollectionUtils.isEmpty(agentIds)) {
      return batches(agentIds, IN_LIMIT_SIZE)
              .map(agentRepository::findAllById)
              .flatMap(Collection::stream)
              .collect(toList());
    }
    return Collections.emptyList();
  }

  private List<HierarchAgentDto> buildHierarchyList(List<Agent> agents, Integer parentId) {
    if (!CollectionUtils.isEmpty(agents)) {
      Map<Integer, HierarchAgentDto> convertedItems = agents
              .stream()
              .filter(agent -> ACTIVE.equals(agent.getUser().getStatus()))
              .collect(toMap(Agent::getId, item -> buildHierarchy(item, parentId)));
      Map<Integer, List<HierarchAgentDto>> itemsByParent = convertedItems
              .values()
              .stream()
              .collect(groupingBy(HierarchAgentDto::getParentId));
      itemsByParent.values().forEach(item -> item.sort(comparing(HierarchAgentDto::getUserName)));
      convertedItems.values().forEach(item -> item.setChild(itemsByParent.get(item.getId())));
      return itemsByParent.get(parentId);
    }
    return emptyList();
  }

  private void changeAgentStatus(Integer agentId, UserStatus status) {
    throwExceptionIfNotSubAgent(getAgentIdForCurrentUser(), agentId);
    Agent agent = agentRepository.getEntity(agentId);
    Integer userId = agent.getUserId();
    User user = userRepository.getEntity(userId);
    user.setStatus(status);
  }

  private void changeAgentAllStatus(Integer agentId, UserStatus status) {
    throwExceptionIfNotSubAgent(getAgentIdForCurrentUser(), agentId);
    List<User> allUserToChanceStatus = userRepository.findAllUserToChanceStatus(String.format(",%s,", agentId));
    List<User> playersUsers = allUserToChanceStatus.stream().filter(user -> UserRole.PLAYER.equals(user.getRole())).collect(toList());
    List<Integer> userIdList = allUserToChanceStatus.stream().map(User::getId).collect(toList());
    List<Integer> playersUsersIdList = playersUsers.stream().map(User::getId).collect(toList());

    for (List<Integer> ids : partitionList(userIdList, 2000)) {
      userRepository.changeStatus(ids, status);
    }

    for (List<Integer> ids : partitionList(playersUsersIdList, 2000)) {
      playerRepository.changeStatus(ids, status);
    }

    if(!playersUsers.isEmpty()){
      playersUsersIdList.forEach(playerId -> {
        changeStatusToApi(playerId, status);
      });
    }
  }

  private void changePlayerStatus(Integer playerId,
                                  com.betmotion.agentsmanagement.platform.api.dto.UserStatus status, UserStatus domainStatus) {
    Player player = playerRepository.getReferenceById(playerId);
    Integer currentUserId = getAgentIdForCurrentUser();
    AgentPlayer agentPlayer = agentPlayerRepository.findByPlayerId(playerId);
    if (agentPlayer == null) {
      throw new ServiceException("US02", new Object[] { playerId, currentUserId });
    }
    if (currentUserId.equals(agentPlayer.getAgentId())) {
      platformApiService.changePlayerStatusWithPlatformApi(player, status);
      player.setStatus(domainStatus);
      playerRepository.save(player);
    } else {
      Agent currentAgent = agentRepository.getReferenceById(agentPlayer.getAgentId());
      if (currentAgent.getParentId() == null) {
        throw new ServiceException("US02", new Object[] { playerId, currentUserId });
      }
      List<Integer> allAgentsWithPermissionToUnblock = agentRepository.findAllIdsOfAgentsHasPermissionUnblock();
      if (allAgentsWithPermissionToUnblock.contains(currentUserId)) {
        platformApiService.changePlayerStatusWithPlatformApi(player, status);
        player.setStatus(domainStatus);
        playerRepository.save(player);
      } else {
        throw new ServiceException("US02", new Object[] { playerId, currentUserId });
      }
    }
  }

  private PlayerDto convertPlayerToDto(Player player) {
    PlayerDto result = new PlayerDto();
    result.setId(player.getId());
    result.setUserName(player.getUserName());
    return result;
  }

  private void balanceTransfer(Wallet from, Wallet to, Long amount, String note) {
    minusBalance(from, amount);
    walletTransactionService.saveTransaction(from.getId(), amount, DEBIT, note, from.getBalance());
    plusBalance(to, amount);
    walletTransactionService.saveTransaction(to.getId(), amount, CREDIT, note, to.getBalance());
  }

  private void plusBalance(Wallet wallet, Long amount) {
    Assert.isTrue(amount > 0, "Amount must be positive");
    Long balance = wallet.getBalance();
    wallet.setBalance(balance + amount);
  }

  private void minusBalance(Wallet wallet, Long amount) {
    Assert.isTrue(amount > 0, "Amount must be positive");
    Long balance = wallet.getBalance();
    /*
     * if (balance < amount) {
     * throw new ServiceException("WB00", new Object[]{balance, amount});
     * }
     *
     */
    wallet.setBalance(balance - amount);
  }

  private void transferBetweenWallets(Wallet sourceWallet, Wallet targetWallet, Long amount) {
    minusBalance(sourceWallet, amount);
    walletTransactionService.saveTransaction(sourceWallet.getId(), amount, DEBIT,
            sourceWallet.getBalance());
    plusBalance(targetWallet, amount);
    walletTransactionService.saveTransaction(targetWallet.getId(), amount, CREDIT,
            targetWallet.getBalance());
  }

  private Integer getAgentIdForCurrentUser() {
    return getAgentForCurrentUser().getId();
  }

  private void throwExceptionIfNotSubAgent(Integer parentAgentId, Integer checkableAgentId) {
    if (isNotSubAgent(parentAgentId, checkableAgentId)) {
      throw new ServiceException("AG05", new Object[] { checkableAgentId, parentAgentId });
    }
  }

  private boolean isNotSubAgent(Integer parentAgentId, Integer checkableAgentId) {
    return !isSubAgent(parentAgentId, checkableAgentId);
  }

  private boolean isSubAgent(Integer parentAgentId, Integer checkableAgentId) {
    return agentRepository.findAllSubAgentsByParentAgent(parentAgentId).contains(checkableAgentId);
  }

  private List<String> convertToDbData(AgentStatusEnum status) {
    if (status == null) {
      return of(ACTIVE.name());
    }
    if (AgentStatusEnum.ALL.equals(status)) {
      return of(ACTIVE.name(), BLOCKED.name());
    }
    if (AgentStatusEnum.ONLY_BLOCKED.equals(status)) {
      return of(BLOCKED.name());
    }
    if (AgentStatusEnum.ONLY_UNBLOCKED.equals(status)) {
      return singletonList(ACTIVE.name());
    }
    throw new IllegalArgumentException("Unsupportable status: " + status);
  }

  private PlayerDetailDto convertToPlayerDetails(Player player, Integer currentLoggedAgentId) {
    PlayerDetailDto result = new PlayerDetailDto();
    result.setId(player.getId());
    result.setUserName(player.getUserName());

    List<PlatformUserDto> platformUserDtos = platformApiService.getPlatformUserDtos(
            singletonList(player));
    PlatformUserDto platformUserDto = platformUserDtos.get(0);
    result.setEmail(platformUserDto.getEmail());
    result.setFullName(buildFullName(platformUserDto));
    result.setCreatedOn(platformUserDto.getCreatedOn());
    result.setPhone(platformUserDto.getCellPhone());

    AgentPlayer agentPlayer = agentPlayerRepository.findByPlayerId(player.getId());
    Agent agentCreator = agentRepository.getEntity(agentPlayer.getAgentId());
    result.setParent(agentCreator.getUser().getUserName());

    Stack<HierarchAgentDto> predefinedData = new Stack<>();
    predefinedData.push(buildHierarchy(agentCreator, player));
    buildChildHierarchy(agentCreator, currentLoggedAgentId, result, predefinedData);

    return result;
  }

  private String buildFullName(PlatformUserDto platformUserDto) {
    return joinPartForName(platformUserDto.getFirstName(), platformUserDto.getLastName());
  }

  private Query findAllAgentAndPlayersTransactionInterval(String search, Integer agentUserLoginId, List<String> statusesForSearch) {
    Query queryUsers;
    queryUsers = em.createNativeQuery(userInfoDao
            .findAllAgentsAndPlayersForAutoCompleteAgentWithSearchByUserNameSql(), MAPPING_USER_INFO);
    queryUsers.setParameter(AGENT_USER_LOGIN_ID, agentUserLoginId);
    queryUsers.setParameter(PARENT_AGENT_ID_STRING, String.format(",%s,", agentUserLoginId));
    queryUsers.setParameter(SEARCH, search);
    queryUsers.setParameter(STATUSES, statusesForSearch);
    return queryUsers;
  }

  private long countAllAgentsAndPlayer(String search, Integer agentUserLoginId, List<String> statusesForSearch) {
    Query queryCount;
    queryCount = em.createNativeQuery(userInfoDao
            .countAllAgentsAndPlayersForAgentWithoutParentAgentSql());
    queryCount.setParameter(PARENT_AGENT_ID_STRING, String.format(",%s,", agentUserLoginId));
    queryCount.setParameter(PARENT_AGENT_ID, agentUserLoginId);
    queryCount.setParameter(SEARCH, search);
    queryCount.setParameter(STATUSES, statusesForSearch);
    long totalElements = ((Number) queryCount.getSingleResult()).longValue();
    return totalElements;
  }

  private List<AgentDeactivationDto> fetchAgentsForDeactivation() {
    Query query = em.createNativeQuery(userInfoDao.findAgentsIdsForDeactivationByInactivitySql(), MAPPING_AGENT_DEACTIVATION);
    return query.getResultList();
  }

  private List<List<Integer>> createBatches(List<Integer> ids, int batchSize) {
    List<List<Integer>> batches = new ArrayList<>();
    int totalSize = ids.size();
    for (int i = 0; i < totalSize; i += batchSize) {
      batches.add(ids.subList(i, Math.min(i + batchSize, totalSize)));
    }
    return batches;
  }

  private List<AgentDeactivationDto> fetchSubAgents(List<List<Integer>> batches) {
    List<AgentDeactivationDto> subAgentsUserIds = new ArrayList<>();
    for (List<Integer> batch : batches) {
      Query querySub = em.createNativeQuery(userInfoDao.findAllSubAgentsByParentIdSql(), MAPPING_AGENT_DEACTIVATION);
      querySub.setParameter(PARENT_AGENT_ID, batch);
      subAgentsUserIds.addAll(querySub.getResultList());
    }
    return subAgentsUserIds;
  }

  private List<AgentDeactivationDto> fetchPlayers(List<List<Integer>> batches) {
    List<AgentDeactivationDto> playersUserIds = new ArrayList<>();
    for (List<Integer> batch : batches) {
      Query querySub = em.createNativeQuery(userInfoDao.findAllPlayersSubAgentsByParentIdSql(), MAPPING_AGENT_DEACTIVATION);
      querySub.setParameter(PARENT_AGENT_ID, batch);
      playersUserIds.addAll(querySub.getResultList());
    }
    return playersUserIds;
  }

  private List<Integer> extractIds(List<AgentDeactivationDto> userIds, Function<AgentDeactivationDto, Integer> extractor) {
    return userIds.stream()
            .map(extractor)
            .collect(Collectors.toList());
  }

  private List<String> extractStrings(List<AgentDeactivationDto> userIds, Function<AgentDeactivationDto, String> extractor) {
    return userIds.stream()
            .map(extractor)
            .collect(Collectors.toList());
  }

  private void deactivateUsers(List<Integer> userIds, boolean isPlayer) {
    if (!userIds.isEmpty()) {
      Query updateQuery = em.createQuery("UPDATE User u SET u.status = :status WHERE u.id IN :playerIds");
      updateQuery.setParameter("status", UserStatus.CLOSED);
      updateQuery.setParameter("playerIds", userIds);

      updateQuery.executeUpdate();
    }
    if (!userIds.isEmpty() && isPlayer) {
      Query updateQuery = em.createQuery("UPDATE Player p SET p.status = :status WHERE p.userId IN :playerIds");
      updateQuery.setParameter("status", UserStatus.CLOSED);
      updateQuery.setParameter("playerIds", userIds);

      updateQuery.executeUpdate();
    }
  }

  private void sendToApi(List<String> userNames){
    log.info("Sending users to api after close agents by inactive");
    platformApi.closeInactiveUsers(userNames);
  }

  private UserSumCreditsBalancesDto findPlayersBalance(String search, AgentStatusEnum status, Integer parentAgentId) {
    List<String> statusesForSearch = convertToDbData(status);
    Query queryUsers = em.createNativeQuery(userInfoDao
            .findAllPlayersForSumAgentWithSearchByUserNameSql(), MAPPING_PLAYER_INFO);
    queryUsers.setParameter(PARENT_AGENT_ID_STRING, String.format(",%s,", parentAgentId));
    queryUsers.setParameter(SEARCH, buildSearchString(search));
    queryUsers.setParameter(STATUSES, statusesForSearch);
    return getUserSumCreditsBalancesDto(queryUsers);
  }

  private UserSumCreditsBalancesDto findAgentsBalance(String search, AgentStatusEnum status, Integer parentAgentId) {
    List<String> statusesForSearch = convertToDbData(status);
    Query queryUsers = em.createNativeQuery(userInfoDao
            .sumBalanceForAgentWithSearchByUserNameSql(), MAPPING_DIRECT_AGENT_SUM_CREDITS_AND_BALANCE);
    queryUsers.setParameter(PARENT_AGENT_ID, parentAgentId);
    queryUsers.setParameter(PARENT_AGENT_ID_STRING, String.format(",%s,", parentAgentId));
    queryUsers.setParameter(SEARCH, buildSearchString(search));
    queryUsers.setParameter(STATUSES, statusesForSearch);
    UserSumCreditsAndBalance balance = (UserSumCreditsAndBalance) queryUsers.getSingleResult();
    return UserSumCreditsBalancesDto.fromUserSumCreditsAndBalance(balance);
  }

  private void processChargedTransaction(UserTransaction userTransaction, Long chargedRemoteId, Agent agent, Player player, PlayerWallet playerWallet, UserTransactionType userTransactionType) {
    if (chargedRemoteId != null) {
      Optional<UserTransaction> chargedTransaction = userTransactionService.findById(chargedRemoteId.intValue());
      if (chargedTransaction.isPresent()) {
        Wallet wallet = walletRepository.findAndLockById(agent.getWalletId());
        wallet.setBalance(UserTransactionType.PAYMENT_ROLLBACK.equals(userTransactionType) ?
                wallet.getBalance() - chargedTransaction.get().getAmount() :
                wallet.getBalance() + chargedTransaction.get().getAmount());
        walletRepository.save(wallet);

        createTransactionAndComplete(userTransaction, agent, player, playerWallet, userTransactionType, 0L);
      }
    }
  }

  private void createTransactionAndComplete(UserTransaction userTransaction, Agent agent, Player player, PlayerWallet playerWallet, UserTransactionType userTransactionType, Long bonus) {
    UserTransaction transaction = userTransactionService.create(agent,
            player.getId(),
            userTransactionType,
            userTransaction.getAmount(),
            playerWallet.getBalance(),
            EMPTY_STRING,
            LocalDateTime.now(),
            bonus);

    transaction.setTransactionStatus(UserTransactionStatus.COMPLETED);
    userTransactionService.update(transaction);
  }

}
