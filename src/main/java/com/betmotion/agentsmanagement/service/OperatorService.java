package com.betmotion.agentsmanagement.service;


import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.MAPPING_USER_INFO;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.PARENT_AGENT_ID;
import static com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl.SUB_AGENT_ID_LIST;
import static com.betmotion.agentsmanagement.service.converter.OperatorConverter.convert;
import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.dao.AgentRepository;
import com.betmotion.agentsmanagement.dao.MfaCodeRepository;
import com.betmotion.agentsmanagement.dao.PlayerRepository;
import com.betmotion.agentsmanagement.dao.WalletRepository;
import com.betmotion.agentsmanagement.dao.impl.UserInfoDaoImpl;
import com.betmotion.agentsmanagement.domain.*;
import com.betmotion.agentsmanagement.rest.dto.PageData;
import com.betmotion.agentsmanagement.rest.dto.operator.ChangePasswordDto;
import com.betmotion.agentsmanagement.rest.dto.operator.OperatorAddCreditsDto;
import com.betmotion.agentsmanagement.rest.dto.operator.OperatorDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserInfoDto;
import com.betmotion.agentsmanagement.security.AppUserDetails;
import com.betmotion.agentsmanagement.service.converter.OperatorConverter;
import com.betmotion.agentsmanagement.service.exceptions.ServiceException;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class OperatorService {

  AgentRepository agentRepository;
  PlayerRepository playerRepository;
  PasswordEncoder passwordEncoder;
  EntityManager em;
  UserInfoDaoImpl userInfoDao;

  UserProvider userProvider;
  MfaCodeRepository mfaCodeRepository;
  WalletRepository walletRepository;

  @Autowired
  UserService userService;

  @Transactional(readOnly = true)
  public OperatorDto get() {
    return OperatorConverter.convert(getOperator());
  }

  private Agent getOperator() {
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
    return agentRepository.getEntity(appUserDetails.getId());
  }

  @Transactional
  public OperatorDto changePassword(ChangePasswordDto dto) {

    Agent operator = getOperator();
    User user = operator.getUser();

    MfaCode mfaCode = mfaCodeRepository.findValidCodeByUserId(user.getId(), dto.getCode(), Calendar.getInstance().getTime())
            .orElseThrow(() -> new ServiceException("OP03", null));

    mfaCodeRepository.delete(mfaCode);

    user.setPassword(passwordEncoder.encode(dto.getPassword()));
    agentRepository.save(operator);
    return convert(operator);
  }

  @Transactional(readOnly = true)
  public PageData<UserInfoDto> getAllUsers(Pageable pageRequest, Integer userId) {
    List<Integer> allSubAgentListForAgent = agentRepository.findAllSubAgentsByParentAgent(
        userId);
    Query queryCount = em.createNativeQuery(userInfoDao.countFindAllAgentsAndPlayersForAgentSql());
    queryCount.setParameter(SUB_AGENT_ID_LIST, allSubAgentListForAgent);
    queryCount.setParameter(PARENT_AGENT_ID, userId);
    long totalElements = ((Number) queryCount.getSingleResult()).longValue();
    Query queryUsers = em.createNativeQuery(userInfoDao.findAllAgentsAndPlayersForAgentSql(),
        MAPPING_USER_INFO);
    queryUsers.setParameter(SUB_AGENT_ID_LIST, allSubAgentListForAgent);
    queryUsers.setParameter(PARENT_AGENT_ID, userId);
    return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers);
  }

  @Transactional(readOnly = true)
  public PageData<UserInfoDto> getAllUsers(Pageable pageRequest) {
    Query queryCount = em.createNativeQuery(userInfoDao.countFindAllUsersSql());
    long totalElements = ((Number) queryCount.getSingleResult()).longValue();
    Query queryUsers = em.createNativeQuery(userInfoDao.findAllUsersSql(), MAPPING_USER_INFO);
    return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers);
  }

  @Transactional(readOnly = true)
  public PageData<UserInfoDto> getAllUsers(Pageable pageRequest, UserRole role) {
    PageData<UserInfoDto> result;
    if (UserRole.AGENT.equals(role)) {
      result = getAllAgents(pageRequest);
    } else if (UserRole.PLAYER.equals(role)) {
      result = getAllPlayers(pageRequest);
    } else {
      throw new ServiceException("UR00", new Object[]{role});
    }
    return result;
  }

  @Transactional
  public void addOperatorCredits(OperatorAddCreditsDto dto) {
    Agent operator = getOperator();
    User user = operator.getUser();
    log.info("Adding credits to operator: {}, amount: {}, code: {}", user.getId(), dto.getAmount(), dto.getCode());

    MfaCode mfaCode = mfaCodeRepository.findValidCodeByUserId(user.getId(), dto.getCode(), Calendar.getInstance().getTime())
            .orElseThrow(() -> new ServiceException("OP03", null));

    mfaCodeRepository.delete(mfaCode);

    Wallet wallet = operator.getCreditsWallet();
    wallet.setBalance(wallet.getBalance() + dto.getAmount());
    agentRepository.save(operator);
    userService.clearUserCache(user);
    log.info("Credits added to operator: {}, new balance: {}", user.getId(), wallet.getBalance());
  }

  private PageData<UserInfoDto> getAllAgents(Pageable pageRequest) {
    long totalElements = agentRepository.countByRole(UserRole.AGENT);
    Query queryUsers = em.createNativeQuery(userInfoDao.findAllAgentsSql(), MAPPING_USER_INFO);
    return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers);
  }

  private PageData<UserInfoDto> getAllPlayers(Pageable pageRequest) {
    long totalElements = playerRepository.count();
    Query queryUsers = em.createNativeQuery(userInfoDao.findAllPlayersSql(), MAPPING_USER_INFO);
    return userInfoDao.getPageableUserInfo(pageRequest, totalElements, queryUsers);
  }
}
