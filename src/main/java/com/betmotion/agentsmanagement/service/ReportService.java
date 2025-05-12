package com.betmotion.agentsmanagement.service;

import static com.betmotion.agentsmanagement.domain.apt.UserTransactionDomainModel.BONUS_REPORT_AMOUNT;
import static com.betmotion.agentsmanagement.domain.apt.UserTransactionDomainModel.REPORT_AMOUNT;
import static com.betmotion.agentsmanagement.rest.dto.reports.AgentRestTransactionType.findByAgentTransactionType;
import static com.betmotion.agentsmanagement.rest.dto.reports.PlayerCreditTransactionType.findByUserTransactionType;
import static com.betmotion.agentsmanagement.service.specifications.AgentTransactionsSpecifications.hasTargetUsers;
import static com.betmotion.agentsmanagement.service.specifications.SpecificationUtils.joinSpecifications;
import static com.betmotion.agentsmanagement.service.specifications.UserTransactionsSpecifications.*;
import static java.util.Collections.emptyMap;
import static java.util.List.of;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import javax.validation.Valid;

import com.betmotion.agentsmanagement.dao.impl.AgentTransactionDaoImpl;
import com.betmotion.agentsmanagement.domain.*;
import com.betmotion.agentsmanagement.rest.dto.agent.AgentTotalQueryDTO;
import com.betmotion.agentsmanagement.rest.dto.reports.*;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.betmotion.agentsmanagement.dao.AgentPlayerRepository;
import com.betmotion.agentsmanagement.dao.AgentRepository;
import com.betmotion.agentsmanagement.dao.AgentTransactionRepository;
import com.betmotion.agentsmanagement.dao.UserTransactionRepository;
import com.betmotion.agentsmanagement.domain.apt.AgentTransactionDomainModel;
import com.betmotion.agentsmanagement.domain.apt.UserTransactionDomainModel;
import com.betmotion.agentsmanagement.process.AgentcalCulateCommission;
import com.betmotion.agentsmanagement.rest.dto.PageData;
import com.betmotion.agentsmanagement.service.specifications.AgentTransactionsSpecifications;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class ReportService {

  UserTransactionRepository userTransactionRepository;

  AgentTransactionRepository agentTransactionRepository;

  AgentRepository agentRepository;

  AgentPlayerRepository agentPlayerRepository;

  EntityManager entityManager;

  AgentTransactionDaoImpl agentTransactionDao;

  Logger logger = LoggerFactory.getLogger(ReportService.class);

  private static final Sort SORTING_PLAYER_TRANSACTIONS_SORTING =
      Sort.by(Order.desc(UserTransactionDomainModel.OPERATION_DATE),
          Order.asc(UserTransactionDomainModel.TRANSACTION_TYPE_INDEX),
          Order.desc(UserTransactionDomainModel.ID));

  private static final Sort SORTING_PLAYER_TRANSACTIONS_CREDIT_REPORT_SORTING =
      Sort.by(Order.desc(UserTransactionDomainModel.OPERATION_DATE),
          Order.asc(UserTransactionDomainModel.TRANSACTION_TYPE_INDEX),
          Order.desc(UserTransactionDomainModel.ID));

  private Specification<UserTransaction> buildSpecificationForSearch(
      PlayerTransactionsRequest request) {
    List<Specification<UserTransaction>> items = new ArrayList<>();
    if (request.getDateFrom() != null) {
      items.add(startFrom(request.getDateFrom()));
    }
    if (request.getDateTo() != null) {
      items.add(endTo(request.getDateTo()));
    }
    if (!isEmpty(request.getTransactionTypes())) {
      List<UserTransactionType> types = request.getTransactionTypes().stream()
          .map(PlayerCreditTransactionType::getTransactionType).collect(toList());
      items.add(hasTransactionTypes(types));
    }
    if (!isEmpty(request.getPlayerIds())) {
      items.add(hasPlayers(request.getPlayerIds()));
    }
    if (!isEmpty(items)) {
      return joinSpecifications(items);
    }
    return null;
  }

  @Transactional(readOnly = true)
  public PageData<PlayerCreditsTransaction> getPlayerTransactions(Pageable pageRequest,
      PlayerTransactionsRequest request) {
    Specification<UserTransaction> result = buildSpecificationForSearch(request);
    Pageable requestToDb = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(),
        SORTING_PLAYER_TRANSACTIONS_SORTING);
    Page<UserTransaction> resultData =
        result == null ? userTransactionRepository.findAll(requestToDb)
            : userTransactionRepository.findAll(result, requestToDb);
    List<PlayerCreditsTransaction> items = resultData.getContent().stream()
        .map(this::convertToPlayerCreditsTransaction).collect(toList());
    return new PageData<>(items, resultData.getTotalPages(), resultData.getTotalElements());
  }

  @Transactional(readOnly = true)
  public TransactionTotalsDto getPlayerTransactionsTotals(PlayerTransactionsRequest request) {
    Specification<UserTransaction> result = buildSpecificationForSearch(request);
    List<UserTransaction> resultData = userTransactionRepository.findAll(result);

    TransactionTotalsDto totals = new TransactionTotalsDto();

    totals.setBalance(resultData.stream()
            .mapToLong(UserTransaction::getReportAmount)
            .sum());
    totals.setBonus(resultData.stream()
            .mapToLong(UserTransaction::getReportBonus)
            .sum());

    return totals;
  }

  @Transactional(readOnly = true)
  public PageData<AgentWalletTransaction> getAgentTransactions(Pageable pageRequest,
      AgentTransactionsRequest request) {
    Specification<AgentTransaction> result = buildSpecificationForSearchAgentTransactions(request);
    Pageable requestToDb = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(),
        Direction.DESC, AgentTransactionDomainModel.OPERATION_DATE, AgentTransactionDomainModel.ID);
    Page<AgentTransaction> resultData =
        result == null ? agentTransactionRepository.findAll(requestToDb)
            : agentTransactionRepository.findAll(result, requestToDb);
    List<AgentWalletTransaction> items =
        resultData.getContent().stream().map(this::convertToWalletTransaction).collect(toList());
    return new PageData<>(items, resultData.getTotalPages(), resultData.getTotalElements());
  }

  @Transactional(readOnly = true)
  public TransactionTotalsDto getAgentTransactionsTotals(AgentTransactionsRequest request) {
    Specification<AgentTransaction> result = buildSpecificationForSearchAgentTransactions(request);
    List<AgentTransaction> resultData = agentTransactionRepository.findAll(result);

    TransactionTotalsDto totals = new TransactionTotalsDto();

    totals.setBalance(resultData.stream()
            .mapToLong(AgentTransaction::getReportAmount)
            .sum());

    return totals;
  }

  private AgentWalletTransaction convertToWalletTransaction(AgentTransaction agentTransaction) {
    AgentWalletTransaction result = new AgentWalletTransaction();
    result.setTransactionType(findByAgentTransactionType(agentTransaction.getOperationType()));
    result.setAmount(agentTransaction.getReportAmount());
    result.setBonus(agentTransaction.getBonus());
    result.setAgentName(agentTransaction.getTargetUser().getUserName());
    result.setTransactionDate(agentTransaction.getOperationDate());
    result.setBalance(agentTransaction.getBalance());
    result.setNote(agentTransaction.getNote());
    return result;
  }

  private Specification<AgentTransaction> buildSpecificationForSearchAgentTransactions(
      AgentTransactionsRequest request) {

    List<Specification<AgentTransaction>> items = new ArrayList<>();
    if (!isEmpty(request.getAgentIds())) {
      List<Agent> agents = agentRepository.findAllById(request.getAgentIds());
      List<User> users = agents.stream().map(Agent::getUser).collect(toList());
      if (!isEmpty(users)) {
        items.add(hasTargetUsers(users));
      }
    }
    if (request.getDateFrom() != null) {
      items.add(AgentTransactionsSpecifications.startFrom(request.getDateFrom()));
    }
    if (request.getDateTo() != null) {
      items.add(AgentTransactionsSpecifications.endTo(request.getDateTo()));
    }
    if (!isEmpty(request.getTransactionTypes())) {
      List<AgentTransactionType> types = request.getTransactionTypes().stream()
          .map(AgentRestTransactionType::getTransactionType).collect(toList());
      items.add(AgentTransactionsSpecifications.hasTransactionTypes(types));
    }
    if (!isEmpty(items)) {
      return joinSpecifications(items);
    }
    return null;
  }

  private PlayerCreditsTransaction convertToPlayerCreditsTransaction(UserTransaction item) {
    PlayerCreditsTransaction result = new PlayerCreditsTransaction();
    result.setPlayerId(item.getPlayer().getId());
    result.setPlayerName(item.getPlayer().getUserName());
    result.setAmount(item.getReportAmount());
    result.setBonus(item.getBonus());
    result.setTransactionDate(item.getOperationDate());
    result.setTransactionType(findByUserTransactionType(item.getOperationType()));
    result.setBalance(item.getBalance());
    result.setNote(item.getNote());
    result.setStatus(item.getTransactionStatus());
    return result;
  }

  @Transactional(readOnly = true)
  public PlayerCreditReportTransactionsResponse getPlayerCreditReport(Pageable pageRequest,
      PlayerCreditReportTransactionsRequest request) {
    Specification<UserTransaction> result =
        buildSpecificationForSearchCreditReportForPlayer(request);
    Pageable requestToDb = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(),
        SORTING_PLAYER_TRANSACTIONS_CREDIT_REPORT_SORTING);
    Page<UserTransaction> resultData =
        result == null ? userTransactionRepository.findAll(requestToDb)
            : userTransactionRepository.findAll(result, requestToDb);
    Map<Integer, Agent> executorAgents = findAgentsForExecutors(resultData.getContent());
    Map<Integer, Agent> directAgentsForPlayers =
        findDirectAgentsForPlayers(resultData.getContent());

    List<PlayerCreditReportTransactionsRow> items = resultData.getContent().stream().map(
            item ->
                convertToPlayerResponseTransactionRow(item, executorAgents, directAgentsForPlayers))
        .collect(toList());
    PageData<PlayerCreditReportTransactionsRow> pageData =
        new PageData<>(items, resultData.getTotalPages(), resultData.getTotalElements());
    PlayerCreditReportTransactionsResponse response = new PlayerCreditReportTransactionsResponse();
    response.setData(pageData);
    long total = 0L;
    long amount = 0L;
    long bonus = 0L;
    if (pageData.getTotalElements() > 0) {
      BigDecimal reportTotalAmount = userTransactionRepository.sum(result, BigDecimal.class,
              BONUS_REPORT_AMOUNT, UserTransaction.class);
      total = reportTotalAmount.longValue();

      BigDecimal reportAmount = userTransactionRepository.sum(result, BigDecimal.class,
              REPORT_AMOUNT, UserTransaction.class);

      amount = reportAmount.longValue();

      bonus = total - amount;

    }
    response.setAmountTotal(amount);
    response.setBonusTotal(bonus);
    response.setTotal(total);
    return response;
  }

  private Map<Integer, Agent> findDirectAgentsForPlayers(List<UserTransaction> content) {
    if (!isEmpty(content)) {
      Set<Integer> playerIds = content.stream().map(UserTransaction::getPlayerid).collect(toSet());
      if (!isEmpty(playerIds)) {
        return agentPlayerRepository.findByPlayerId(playerIds).stream()
            .collect(toMap(AgentPlayer::getPlayerId, AgentPlayer::getAgent));
      }
    }
    return emptyMap();
  }

  private Map<Integer, Agent> findAgentsForExecutors(List<UserTransaction> items) {
    if (!isEmpty(items)) {
      Set<Integer> userIds = items.stream().map(UserTransaction::getUserid).collect(toSet());
      return findAgentsByUserIds(userIds);
    }
    return emptyMap();
  }

  private Map<Integer, Agent> findAgentsByUserIds(Set<Integer> userIds) {
    if (!isEmpty(userIds)) {
      List<Agent> agents = agentRepository.findByUserIds(userIds);
      return agents.stream().collect(toMap(Agent::getUserId, Function.identity()));
    }
    return Collections.emptyMap();
  }

  private PlayerCreditReportTransactionsRow convertToPlayerResponseTransactionRow(
      UserTransaction userTransaction, Map<Integer, Agent> agentsForExecutors,
      Map<Integer, Agent> directAgentsForPlayers) {
    PlayerCreditReportTransactionsRow result = new PlayerCreditReportTransactionsRow();
    result.setTransactionDate(userTransaction.getOperationDate());
    result.setTransactionType(findByUserTransactionType(userTransaction.getOperationType()));
    result.setAmount(userTransaction.getReportAmount());
    result.setBonus(userTransaction.getBonus());
    Agent executor = agentsForExecutors.get(userTransaction.getUserid());
    result.setExecutor(convertAgentToUserInfo(executor));
    result.setPlayer(convertPlayerToReportUserInfo(userTransaction.getPlayer()));
    Agent directAgent = directAgentsForPlayers.get(userTransaction.getPlayerid());
    result.setDirectAgent(convertAgentToUserInfo(directAgent));
    result.setSuperior(result.getDirectAgent().getId().equals(result.getExecutor().getId()));
    result.setStatus(userTransaction.getTransactionStatus());
    return result;
  }

  private ReportUserInfo convertPlayerToReportUserInfo(Player player) {
    ReportUserInfo result = new ReportUserInfo();
    result.setId(player.getId());
    result.setName(player.getUserName());
    return result;
  }

  private ReportUserInfo convertAgentToUserInfo(Agent agent) {
    ReportUserInfo result = new ReportUserInfo();
    result.setId(agent.getUserId());
    User user = agent.getUser();
    result.setName(user.getUserName());
    return result;
  }


  private Specification<UserTransaction> buildSpecificationForSearchCreditReportForPlayer(
      PlayerCreditReportTransactionsRequest request) {
    List<Specification<UserTransaction>> items = new ArrayList<>();
    if (request.getDateFrom() != null) {
      items.add(startFrom(request.getDateFrom()));
    }
    if (request.getDateTo() != null) {
      items.add(endTo(request.getDateTo()));
    }
    if (!isEmpty(request.getTransactionTypes())) {
      List<UserTransactionType> types = request.getTransactionTypes().stream()
          .map(PlayerCreditTransactionType::getTransactionType).collect(toList());
      items.add(hasTransactionTypes(types));
    }
    if (request.getTransactionStatus() != null) {
      items.add(hasTransactionStatus(request.getTransactionStatus()));
    }
    if (!isEmpty(request.getPlayerIds())) {
      items.add(hasPlayers(request.getPlayerIds()));
    }
    if (!isEmpty(items)) {
      return joinSpecifications(items);
    }
    return null;
  }

  private Specification<AgentTransaction> buildSpecificationForhAgentCreditReport(
      AgentCreditReportTransactionsRequest request) {

    List<Specification<AgentTransaction>> items = new ArrayList<>();
    if (!isEmpty(request.getAgentIds())) {
      List<Agent> agents = agentRepository.findAllById(request.getAgentIds());
      List<User> users = agents.stream().map(Agent::getUser).collect(toList());
      if (!isEmpty(users)) {
        items.add(hasTargetUsers(users));
      }
    }
    if (request.getDateFrom() != null) {
      items.add(AgentTransactionsSpecifications.startFrom(request.getDateFrom()));
    }
    if (request.getDateTo() != null) {
      items.add(AgentTransactionsSpecifications.endTo(request.getDateTo()));
    }
    if (!isEmpty(request.getTransactionTypes())) {
      List<AgentTransactionType> types = request.getTransactionTypes().stream()
          .map(AgentRestTransactionType::getTransactionType).collect(toList());
      items.add(AgentTransactionsSpecifications.hasTransactionTypes(types));
    }
    if (!isEmpty(items)) {
      return joinSpecifications(items);
    }
    return null;
  }


  @Transactional(readOnly = true)
  public AgentCreditReportTransactionsResponse getAgentCreditReport(Pageable pageRequest,
                                                                    AgentCreditReportTransactionsRequest request) {
    Query countQuery = entityManager.createNativeQuery(agentTransactionDao.getCountAgentTransactionsByTargetAgentIdDateAgentTransactionType());
    countQuery.setParameter("agentId", request.getAgentIds().stream().findFirst().get());
    countQuery.setParameter("startDate", request.getDateFrom());
    countQuery.setParameter("endDate", request.getDateTo());
    countQuery.setParameter("operationType", request.getTransactionTypes().stream()
            .map(agentRestTransactionType -> agentRestTransactionType.getTransactionType().toString())
            .collect(Collectors.toUnmodifiableList()));
    int count = (int) countQuery.getSingleResult();

    if(count == 0) {
      return AgentCreditReportTransactionsResponse.builder()
              .data(new PageData<>(Collections.emptyList(), 0, 0))
              .total(0L)
              .build();
    }

    Query query = entityManager.createNativeQuery(agentTransactionDao.getAgentTransactionsByTargetAgentIdDateAgentTransactionType());
    query.setParameter("agentId", request.getAgentIds().stream().findFirst().get());
    query.setParameter("startDate", request.getDateFrom());
    query.setParameter("endDate", request.getDateTo());
    query.setParameter("offset", pageRequest.getOffset());
    query.setParameter("pageSize", pageRequest.getPageSize());
    query.setParameter("operationType", request.getTransactionTypes().stream()
            .map(agentRestTransactionType -> agentRestTransactionType.getTransactionType().toString())
            .collect(Collectors.toUnmodifiableList()));
    List<AgentTransactionReport> agentTransactionReportList = query.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.aliasToBean(AgentTransactionReport.class)).getResultList();

    AgentCreditReportTransactionsResponse response = new AgentCreditReportTransactionsResponse();
    List<AgentCreditReportTransactionsRow> items = agentTransactionReportList.stream().map(this::convertAgentCreditReportTransactionsRow).collect(Collectors.toUnmodifiableList());

    int totalPages = (int) Math.ceil((double) count / pageRequest.getPageSize());
    PageData<AgentCreditReportTransactionsRow> agentWalletTransactionPageData =
            new PageData<>(items, totalPages, count);
    response.setData(agentWalletTransactionPageData);

    Query queryTotal = entityManager.createNativeQuery(agentTransactionDao.getSumAtentTransactionsByTargetAgentIdDateAgentTransactionType());
    queryTotal.setParameter("agentId", request.getAgentIds().stream().findFirst().get());
    queryTotal.setParameter("startDate", request.getDateFrom());
    queryTotal.setParameter("endDate", request.getDateTo());
    queryTotal.setParameter("operationType", request.getTransactionTypes().stream()
            .map(agentRestTransactionType -> agentRestTransactionType.getTransactionType().toString())
            .collect(Collectors.toUnmodifiableList()));
    AgentTotalQueryDTO agentTotalQueryDTO = (AgentTotalQueryDTO) queryTotal.unwrap(org.hibernate.Query.class).setResultTransformer(Transformers.aliasToBean(AgentTotalQueryDTO.class))
            .getSingleResult();

    response.setAmountTotal(agentTotalQueryDTO.getAmount().longValue());
    response.setBonusTotal(agentTotalQueryDTO.getBonus().longValue());
    response.setTotal(agentTotalQueryDTO.getTotal().longValue());
    return response;
  }


  private AgentCreditReportTransactionsRow convertAgentCreditReportTransactionsRow(AgentTransactionReport agentTransactionReport) {
    AgentCreditReportTransactionsRow result = new AgentCreditReportTransactionsRow();
    result.setBonus(agentTransactionReport.getBonus().longValue());
    result.setTransactionDate(agentTransactionReport.getOperationDate());
    result.setAmount(agentTransactionReport.getAmount().longValue());
    result.setTransactionType(findByAgentTransactionType(agentTransactionReport.getAgentTransactionType()));
    result.setSuperior(false);
    result.setAgent(ReportUserInfo.builder().name(agentTransactionReport.getUserName()).build());
    result.setDirectAgent(ReportUserInfo.builder().name(agentTransactionReport.getUserName()).build());
    result.setExecutor(ReportUserInfo.builder().name(agentTransactionReport.getUserName()).build());
    return result;
  }

  private AgentCreditReportTransactionsRow convertToAgentReportCreditRow(
      AgentTransaction agentTransaction, Map<Integer, Agent> agentsByUserIds) {
    AgentCreditReportTransactionsRow result = new AgentCreditReportTransactionsRow();
    result.setTransactionType(findByAgentTransactionType(agentTransaction.getOperationType()));
    result.setAmount(agentTransaction.getReportAmount());
    result.setTransactionDate(agentTransaction.getOperationDate());
    Agent targetAgent = agentsByUserIds.get(agentTransaction.getTargetUser().getId());
    result.setAgent(convertAgentToUserInfo(targetAgent));
    result.setExecutor(
        convertAgentToUserInfo(agentsByUserIds.get(agentTransaction.getSourceUser().getId())));
    boolean superiorValue = false;
    // IT is null for operator
    if (targetAgent.getParentAgent() != null) {
      result.setDirectAgent(convertAgentToUserInfo(targetAgent.getParentAgent()));
      superiorValue = result.getDirectAgent().getId().equals(result.getExecutor().getId());
    }
    result.setSuperior(superiorValue);
    return result;
  }

  public AgentCommissions getCommission(Integer agentId) {
    Query query = entityManager.createNativeQuery(
            getSqlCommission());
    query.setParameter("agentId", agentId);

    AgentCommissions agentCommissions =
            (AgentCommissions) query.unwrap(org.hibernate.Query.class)
                .setResultTransformer(Transformers.aliasToBean(AgentCommissions.class)).getSingleResult();

    return agentCommissions;
  }

  public AgentPaymentChipReport getAgentPayment(
      @Valid AgentPaymentReportRequest paymentReportRequest) {

    long startTime = new Date().getTime();
    logger.info("START Agent Payment Report: " + startTime);
    Query query = entityManager.createNativeQuery(
        getSqlTextPaymentChipReport(paymentReportRequest));
    query.setParameter("agentId", String.format(",%s,", paymentReportRequest.getAgentId()));
		query.setParameter("beginDate", paymentReportRequest.getBeginDate());
		query.setParameter("endDate", paymentReportRequest.getEndDate());

    setParameterAgentName(paymentReportRequest, query);

    List<AgentPaymentChipRowReport> agentPaymentChipRowReport =
        query.unwrap(org.hibernate.Query.class)
            .setResultTransformer(Transformers.aliasToBean(AgentPaymentChipRowReport.class)).list();

    List<AgentPaymentChipRowReport> rowReports = consolidateAgents(agentPaymentChipRowReport, paymentReportRequest.getAgentId());

    AgentcalCulateCommission calculateComission =
        new AgentcalCulateCommission(getAgentPercentCommission(paymentReportRequest), rowReports);

    AgentPaymentChipReport agentPaymentChipReport = createAgentPaymentChipReport(
            rowReports, calculateComission);

    agentPaymentChipReport.setAgentPaymentChipRowReport(
        paginatedList(paymentReportRequest, rowReports));
    long endtime = new Date().getTime();
    logger.info("END Agent Payment Report: " + endtime);
    logger.info("ELAPSED TIME Agent Payment Report: " + (endtime - startTime));
    return agentPaymentChipReport;
  }

  public List<AgentPaymentChipRowReport> consolidateAgents(List<AgentPaymentChipRowReport> agents, Integer parentAgentId) {

    List<AgentPaymentChipRowReport> consolidatedList = new ArrayList<>();
    Map<Integer, AgentPaymentChipRowReport> groupedAgentsMap = new HashMap<>();


    for (AgentPaymentChipRowReport agent : agents) {
      String[] split = agent.getParentTree().split(String.format(",%s,", parentAgentId));
      if (split.length > 1) {
        Integer subAgentId = Integer.valueOf(split[1].split(",")[0]);
        AgentPaymentChipRowReport subAgent = groupedAgentsMap.get(subAgentId);
        if (subAgent == null){
          groupedAgentsMap.put(subAgentId, agent);
        } else {
          subAgent.addValues(agent);
        }
      } else {
        groupedAgentsMap.put(agent.getAgentId(), agent);
      }
    }

    for (Map.Entry<Integer, AgentPaymentChipRowReport> entry : groupedAgentsMap.entrySet())
    {
      if (!entry.getKey().equals(entry.getValue().getAgentId())) {
        Agent activeAgen = agentRepository.findByIdFetchUser(entry.getKey());
        entry.getValue().setAgentName(activeAgen.getUser().getUserName());
      }
      consolidatedList.add(entry.getValue());
    }

    return consolidatedList;
  }

  private BigDecimal getAgentPercentCommission(AgentPaymentReportRequest paymentReportRequest) {
      Query queryAgentCommission = entityManager.createNativeQuery(getSqlTextCommissionAgent());
      queryAgentCommission.setParameter("agentId", paymentReportRequest.getAgentId());
      return ((BigDecimal) queryAgentCommission.getSingleResult());
  }

  private AgentPaymentChipReport createAgentPaymentChipReport(
      List<AgentPaymentChipRowReport> agentPaymentChipRowReport,
      AgentcalCulateCommission calculateComission) {
    AgentPaymentChipReport agentPaymentChipReport = new AgentPaymentChipReport();
    agentPaymentChipReport.setComission(calculateComission.getCommission());

    agentPaymentChipReport.setCommissionPercent(calculateComission.getCommissionPercent());
    agentPaymentChipReport.setTotalPay(calculateComission.getTotalPay());
    agentPaymentChipReport.setAgentPaymentChipRowReport(agentPaymentChipRowReport);

    agentPaymentChipReport.setTotalBalanceChip(agentPaymentChipRowReport.stream()
        .map(AgentPaymentChipRowReport::getTotalChip).reduce(BigInteger.ZERO, BigInteger::add));
    agentPaymentChipReport.setTotalChipIn(agentPaymentChipRowReport.stream()
        .map(AgentPaymentChipRowReport::getChipIn).reduce(BigInteger.ZERO, BigInteger::add));
    agentPaymentChipReport.setTotalChipOut(agentPaymentChipRowReport.stream()
        .map(AgentPaymentChipRowReport::getChipOut).reduce(BigInteger.ZERO, BigInteger::add));
    agentPaymentChipReport.setTotalBonus(agentPaymentChipRowReport.stream()
        .map(AgentPaymentChipRowReport::getBonus).reduce(BigInteger.ZERO, BigInteger::add));
    agentPaymentChipReport.setTotalAgents(agentPaymentChipRowReport.size());
    return agentPaymentChipReport;
  }

  private String getSqlTextPaymentChipReport(AgentPaymentReportRequest paymentReportRequest) {

    String sql = "SELECT " +
            " a.id as agentId, " +
            " u.user_name as agentName, " +
            " COALESCE(a.commission, " +
            " 0) as commission, " +
            " COALESCE(deposits.amount,0) AS chipIn, " +
            " COALESCE(deposits.bonus,0) AS bonus, " +
            " COALESCE(withdrawal.amount,0) AS chipOut , " +
            " COALESCE(deposits.amount,0) + COALESCE(deposits.bonus,0) - COALESCE(withdrawal.amount,0) totalChip, " +
            " a.parent_agent_id as parentAgentId, " +
            " a.parent_tree as parentTree " +
            "from " +
            " users u " +
            "join agents a on " +
            " u.id = a.user_id " +
            "LEFT JOIN ( " +
            " SELECT " +
            "  direct_player_agent_id as user_id, " +
            "  sum(amount) amount, " +
            "  sum(bonus) bonus " +
            " FROM " +
            "  dbo.user_transaction trans_deposit " +
            " join players p on trans_deposit.player_id = p.id " +
            " WHERE " +
            "  trans_deposit.operation_type = 'DEPOSIT' " +
            "  AND trans_deposit.operation_date BETWEEN :beginDate AND :endDate " +
            "  AND trans_deposit.transaction_status != 'FAILED'" +
            "  AND p.status != 'CLOSED' " +
            " GROUP BY " +
            "  direct_player_agent_id) deposits ON " +
            " u.id = deposits.user_id " +
            "LEFT JOIN ( " +
            " SELECT " +
            "  direct_player_agent_id as user_id, " +
            "  sum(amount) amount " +
            " FROM " +
            "  dbo.user_transaction trans_withdrawal " +
            " join players p on trans_withdrawal.player_id = p.id " +
            " WHERE " +
            "  trans_withdrawal.operation_type = 'WITHDRAWAL' " +
            "  AND trans_withdrawal.operation_date BETWEEN :beginDate AND :endDate " +
            "  AND trans_withdrawal.transaction_status != 'FAILED'" +
            "  AND p.status != 'CLOSED' " +
            " GROUP BY " +
            "  direct_player_agent_id) withdrawal ON " +
            " u.id = withdrawal.user_id " +
            "  where charindex(:agentId, a.parent_tree) > 0  " +
            "  AND (deposits.amount > 0 or withdrawal.amount > 0) ";
    sql += StringUtils.isNotEmpty(paymentReportRequest.getSearchAgentName()) ? " a.user_name like :agentName " : "";
    sql += " order by u.user_name ";

    return sql;

  }

  private String getSqlCommission () {
    return "SELECT " +
            " a.comission_sports AS commissionSports, a.comission_slots AS commissionSlots, a.comission_casino AS commissionCasino  " +
            "FROM agents a " +
            " WHERE " +
            " a.id = :agentId   ";
  }

  private String setGGRUsernames() {
    return "DECLARE @agentId NVARCHAR(255); " +
            " SET @agentId = ?; " +
            " INSERT INTO dbo.ggr_usernames (username, request_agent) " +
            " SELECT " +
            "    u.user_name, " +
            "    @agentId AS request_agent " +
            "FROM " +
            "    agents a " +
            "JOIN " +
            "    users u " +
            "ON " +
            "    a.user_id = u.id " +
            "WHERE " +
            "    CHARINDEX(',' + CAST(@agentId AS VARCHAR) + ',', a.parent_tree) > 0";
  }

  private String getAPISql () {
    return "SELECT " +
            "   mt.money_transaction_type AS mt_type, " +
            "   g.game_type AS game_type, " +
            "   CAST(mt.amount AS BIGINT) AS amount, " +
            "    FROM " +
            "     LocalPulpobetDB.pulpobet.dbo.money_transaction mt " +
            "  INNER JOIN " +
            "        LocalPulpobetDB.pulpobet.dbo.users u ON mt.user_id = u.id" +
            "   INNER JOIN " +
            "     dbo.ggr_usernames ggu " +
            "    ON " +
            "       ggu.username = u.agent COLLATE SQL_Latin1_General_CP1_CI_AS" +
            "    INNER JOIN " +
            "        LocalPulpobetDB.pulpobet.dbo.game g ON mt.game_id = g.id " +
            "    WHERE  " +
            "        mt.trans_date BETWEEN ? AND ? " +
            " AND " +
            "  mt.money_transaction_type IN (0, 1) " +
            " AND " +
            " ggu.request_agent = ?";
  }

  private String getApiSqlSum () {
    return "SELECT " +
            "   COALESCE (CAST(COUNT(CASE WHEN mt.money_transaction_type = 0 AND g.game_type = 0 THEN 1 END) AS BIGINT),0) as cassinoBets, " +
            " COALESCE(SUM(CASE WHEN mt.money_transaction_type = 0 AND g.game_type = 0 THEN mt.amount ELSE 0 END), 0) AS cassinoBetsValue, " +
            " COALESCE(SUM(CASE WHEN mt.money_transaction_type = 1 AND g.game_type = 0 THEN mt.amount ELSE 0 END), 0) AS cassinoWins, " +
            " COALESCE (CAST(COUNT(CASE WHEN mt.money_transaction_type = 0 and g.game_type <> 0 and g.game_type <> 2 THEN 1 END)  AS BIGINT),0) as slotsBets, " +
            " COALESCE(SUM(CASE WHEN mt.money_transaction_type = 0 AND g.game_type <> 0 and g.game_type <> 2 THEN mt.amount ELSE 0 END), 0) AS slotsBetsValue, " +
            " COALESCE(SUM(CASE WHEN mt.money_transaction_type = 1 AND g.game_type <> 0 and g.game_type <> 2 THEN mt.amount ELSE 0 END) , 0) AS slotsWins, " +
            " COALESCE (CAST(COUNT(CASE WHEN mt.money_transaction_type = 0 and g.game_type = 2 THEN 1 END)  AS BIGINT),0) as sportsBets, " +
            " COALESCE(SUM(CASE WHEN mt.money_transaction_type = 0 AND g.game_type = 2 THEN mt.amount ELSE 0 END), 0) AS sportsBetsValue, " +
            " COALESCE(SUM(CASE WHEN mt.money_transaction_type = 1 AND g.game_type = 2 THEN mt.amount ELSE 0 END), 0) AS sportsWins" +
            "    FROM " +
            "     LocalPulpobetDB.pulpobet.dbo.money_transaction mt " +
            "  INNER JOIN " +
            "        LocalPulpobetDB.pulpobet.dbo.users u ON mt.user_id = u.id" +
            "   INNER JOIN " +
            "     dbo.ggr_usernames ggu " +
            "    ON " +
            "       ggu.username = u.agent COLLATE SQL_Latin1_General_CP1_CI_AS" +
            "    INNER JOIN " +
            "        LocalPulpobetDB.pulpobet.dbo.game g ON mt.game_id = g.id " +
            "    WHERE  " +
            "        mt.trans_date BETWEEN ? AND ? " +
            " AND " +
            "  mt.money_transaction_type IN (0, 1) " +
            " AND " +
            " ggu.request_agent = ?";

  }

  private String getSqlClearUsernames() {
    return "DELETE FROM dbo.ggr_usernames WHERE request_agent = ?";
  }

  private String getSqlTextPaymentCommissionReport () {

    return "   WITH ParentTree AS (" +
            "    SELECT " +
            "        a.id AS agent_id, " +
            "        a.comission_sports, " +
            "        a.comission_slots, " +
            "        a.comission_casino, " +
            "        a.parent_tree, " +
            "        a.comission_type " +
            "    FROM " +
            "        agents a " +
            "    WHERE " +
            "        CHARINDEX(','+CAST(:agentId AS VARCHAR)+',', a.parent_tree) > 0 " +
            ") " +
            "SELECT " +
            "    (SELECT pt.comission_sports FROM ParentTree pt JOIN agents a ON a.id = pt.agent_id WHERE a.id = :agentId) AS commissionSports," +
            "    (SELECT pt.comission_slots FROM ParentTree pt JOIN agents a ON a.id = pt.agent_id WHERE a.id = :agentId) AS commissionSlots, " +
            "    (SELECT pt.comission_casino FROM ParentTree pt JOIN agents a ON a.id = pt.agent_id WHERE a.id =:agentId) AS commissionCasino, " +
            "    STRING_AGG(CAST(u.user_name AS NVARCHAR(MAX)), ', ') AS usernames " +
            "FROM " +
            "    ParentTree pt " +
            "JOIN " +
            "    agents a " +
            "ON " +
            "    a.id = pt.agent_id " +
            "JOIN " +
            "    users u " +
            "ON " +
            "    a.user_id = u.id ";
  }

  private String getSqlTextCommissionAgent() {
	  return "SELECT commission FROM dbo.agents WHERE id=:agentId";
  }

  private void addParameterAgentName(AgentPaymentReportRequest paymentReportRequest,
      StringBuilder sqlText) {
    if (StringUtils.isNotEmpty(paymentReportRequest.getSearchAgentName())) {
      sqlText.append("WHERE ah.user_name like :agentName ");
    }
  }

  private void setParameterAgentName(AgentPaymentReportRequest paymentReportRequest, Query query) {
    if (StringUtils.isNotEmpty(paymentReportRequest.getSearchAgentName())) {
      query.setParameter("agentName", paymentReportRequest.getSearchAgentName() + "%");
    }
  }

  private List<AgentPaymentChipRowReport> paginatedList(
      AgentPaymentReportRequest paymentReportRequest,
      List<AgentPaymentChipRowReport> agentPaymentChipRowReport) {

    return agentPaymentChipRowReport.stream()
        .skip(paymentReportRequest.getPageNumber() * paymentReportRequest.getLimitPerPage())
        .limit(paymentReportRequest.getLimitPerPage())
        .collect(Collectors.toList());
  }

  public ChipsTransactionsReport getChipsTransactions(ChipsTransactionsReportRequest chipsTransactionsReportRequest) {
    Query query = entityManager.createNativeQuery(
            getSqlTextChipsTransactionsReport(chipsTransactionsReportRequest));

    if (chipsTransactionsReportRequest.getMyTransactions()) {
      query.setParameter("loggedAgentId", chipsTransactionsReportRequest.getLoggedAgentId());
      if  (!chipsTransactionsReportRequest.getAgentId().equals(chipsTransactionsReportRequest.getLoggedAgentId())) {
        query.setParameter("agentId", String.format(",%s,", chipsTransactionsReportRequest.getAgentId()));
      }
    } else {
      query.setParameter("agentId", String.format(",%s,", chipsTransactionsReportRequest.getAgentId()));
    }

    if (chipsTransactionsReportRequest.getBeginDate() != null && chipsTransactionsReportRequest.getEndDate() != null) {
      query.setParameter("beginDate", chipsTransactionsReportRequest.getBeginDate());
      query.setParameter("endDate", chipsTransactionsReportRequest.getEndDate());
    }
    if (chipsTransactionsReportRequest.getTransactionType() != null && !chipsTransactionsReportRequest.getTransactionType().isEmpty()) {
      query.setParameter("transactionType", chipsTransactionsReportRequest.getTransactionTypesString());
    } else {
      query.setParameter("transactionType", AgentTransactionType.getDepositWithdrawToFilter());
    }

    if (StringUtils.isNotEmpty(chipsTransactionsReportRequest.getPlayerName())) {
      query.setParameter("playerName", chipsTransactionsReportRequest.getPlayerName());
    }

    if(!UserRole.AGENT.equals(chipsTransactionsReportRequest.getUserRole())) {
      query.setParameter("status", UserStatus.CLOSED.toString());
    }

    if (Optional.ofNullable(chipsTransactionsReportRequest.getTransactionStatus()).isPresent()) {
      query.setParameter("transactionStatus", chipsTransactionsReportRequest.getTransactionStatus().name());
    }

    List<ChipTransactionRowReport> chipTransactionRows =
            query.unwrap(org.hibernate.Query.class)
                    .setResultTransformer(Transformers.aliasToBean(ChipTransactionRowReport.class)).list();

    long total = chipTransactionRows.stream()
                  .mapToLong(ctr -> ctr.getAmount().multiply(BigInteger.valueOf(ctr.getOperationType().getReportAmountMutiplier())).longValue()
                                     + (ctr.getBonus() != null ? ctr.getBonus().longValue() : 0L))
                  .sum();


    ChipsTransactionsReport chipsTransactionsReport = new ChipsTransactionsReport();
    chipsTransactionsReport.setAgentPaymentChipRowReport(chipTransactionRows);
    chipsTransactionsReport.setTotalBalance(BigInteger.valueOf(total));

    return chipsTransactionsReport;
  }

  private String getSqlTextChipsTransactionsReport(ChipsTransactionsReportRequest chipsTransactionsReportRequest) {
    StringBuilder agentsSql = new StringBuilder();
    StringBuilder usersSql = new StringBuilder();

//    if (StringUtils.isNotEmpty(chipsTransactionsReportRequest.getPlayerName())) {
//      chipsTransactionsReportRequest.setUserRole(UserRole.PLAYER);
//    }

    String order = " order by operationDate DESC ";
    agentsSql.append("Select ");
    agentsSql.append(" at.amount, ");
    agentsSql.append(" at.bonus, ");
    agentsSql.append(" su.user_name as sourceName, ");
    agentsSql.append(" tu.user_name as targetName, ");
    agentsSql.append(" at.operation_type as operationType, ");
    agentsSql.append(" at.operation_date as operationDate, ");
    agentsSql.append(" null as status ");
    agentsSql.append(" FROM agent_transaction at ");
    agentsSql.append(" join agents a on a.user_id = at.source_user_id ");
    agentsSql.append(" join agents target on target.user_id = at.target_user_id ");
    agentsSql.append(" join users su on su.id = at.source_user_id  ");
    agentsSql.append(" join users tu on tu.id = at.target_user_id   ");
    agentsSql.append(" WHERE ");

    if (chipsTransactionsReportRequest.getMyTransactions()) {
      if  (!chipsTransactionsReportRequest.getAgentId().equals(chipsTransactionsReportRequest.getLoggedAgentId())) {
        agentsSql.append(" a.id = :loggedAgentId and charindex(:agentId, target.parent_tree) > 0");
      } else {
        agentsSql.append(" (a.id = :loggedAgentId or target.id = :loggedAgentId) ");
      }
    } else {
      agentsSql.append(" charindex(:agentId, a.parent_tree) > 0 ");
    }

    agentsSql.append(" and at.operation_date BETWEEN :beginDate AND :endDate ");
    agentsSql.append(
            StringUtils.isNotEmpty(chipsTransactionsReportRequest.getPlayerName())
                    && (chipsTransactionsReportRequest.getUserRole() == null || UserRole.AGENT.equals(chipsTransactionsReportRequest.getUserRole()))
                    ? " and (su.user_name = :playerName or tu.user_name = :playerName)" : ""
    );
    agentsSql.append(" and at.operation_type IN :transactionType ");

    if(UserRole.AGENT.equals(chipsTransactionsReportRequest.getUserRole()))
      return agentsSql.append(order).toString();

    agentsSql.append(" and tu.status != :status");

    usersSql.append("  ");
    usersSql.append("Select ");
    usersSql.append(" ut.amount, ");
    usersSql.append(" ut.bonus, ");
    usersSql.append(" su.user_name as sourceName, ");
    usersSql.append(" p.user_name as targetName, ");
    usersSql.append(" ut.operation_type as operationType, ");
    usersSql.append(" ut.operation_date as operationDate, ");
    usersSql.append(" ut.transaction_status as status ");
    usersSql.append(" FROM user_transaction ut ");
    usersSql.append(" join agents a2 on a2.user_id = ut.user_id ");
    usersSql.append(" join users su on su.id = ut.user_id  ");
    usersSql.append(" join players p on ut.player_id = p.id   ");
    if (chipsTransactionsReportRequest.getMyTransactions() &&
          !chipsTransactionsReportRequest.getAgentId().equals(chipsTransactionsReportRequest.getLoggedAgentId())) {
      usersSql.append(" join agents_players ap on ap.players_id = ut.player_id ");
      usersSql.append(" join agents a3 on ap.agent_id = a3.id ");
    }
    usersSql.append(" WHERE ");

    if (chipsTransactionsReportRequest.getMyTransactions()) {
      usersSql.append(" a2.id = :loggedAgentId ");
      if (!chipsTransactionsReportRequest.getAgentId().equals(chipsTransactionsReportRequest.getLoggedAgentId())) {
        usersSql.append(" and charindex(:agentId, a3.parent_tree) > 0  ");
      }
    } else {
      usersSql.append(" charindex(:agentId, a2.parent_tree) > 0  ");
    }

    usersSql.append(" and ut.operation_date BETWEEN :beginDate AND :endDate ");
    usersSql.append(
            StringUtils.isNotEmpty(chipsTransactionsReportRequest.getPlayerName())
                    && (chipsTransactionsReportRequest.getUserRole() == null || UserRole.PLAYER.equals(chipsTransactionsReportRequest.getUserRole()))
                    ? " and (p.user_name = :playerName or su.user_name = :playerName) " : ""
    );
    usersSql.append(" and ut.operation_type IN :transactionType");
    usersSql.append(" and p.status != :status");

    if (Optional.ofNullable(chipsTransactionsReportRequest.getTransactionStatus()).isPresent()) {
      usersSql.append(" and ut.transaction_status = :transactionStatus");
    }

    if(UserRole.PLAYER.equals(chipsTransactionsReportRequest.getUserRole())) {
      return usersSql.append(order).toString();
    }


    return agentsSql.append(" union all ").append(usersSql).append(order).toString();
  }

  public CommissionReport getCommissionReport(AgentCommissions namesAndCommission,
                                              CommissionAgent commissionAgent) {
    CommissionReport commissionReport = new CommissionReport();
    commissionReport.setCommissionSlots(namesAndCommission.getCommissionSlots());
    commissionReport.setCommissionSports(namesAndCommission.getCommissionSports());
    commissionReport.setCommissionCasino(namesAndCommission.getCommissionCasino());
    commissionReport.setCommissionAgents(commissionAgent);
    return commissionReport;
  }

}
