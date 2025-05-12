package com.betmotion.agentsmanagement.dao.impl;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.Query;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import com.betmotion.agentsmanagement.dao.projection.UserInfo;
import com.betmotion.agentsmanagement.rest.dto.PageData;
import com.betmotion.agentsmanagement.rest.dto.user.UserInfoDto;
import com.betmotion.agentsmanagement.security.AppUserDetails;
import com.betmotion.agentsmanagement.service.UserInfoEnricher;
import com.betmotion.agentsmanagement.service.UserProvider;
import com.betmotion.agentsmanagement.service.converter.UserInfoConverter;

import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Repository
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class UserInfoDaoImpl {

  public static final String MAPPING_USER_INFO = "Mapping.userInfo";
  public static final String MAPPING_AGENT_DEACTIVATION = "Mapping.agentDeactivation";
  public static final String MAPPING_PLAYER_INFO = "Mapping.playerInfo";
  public static final String MAPPING_DIRECT_AGENT_SUM_CREDITS_AND_BALANCE = "Mapping.directAgentSumCreditsAndBalance";
  public static final String SUB_AGENT_ID_LIST = "subAgentIdList";
  public static final String PARENT_AGENT_ID = "parentAgentId";
  public static final String PARENT_AGENT_ID_STRING = "parentAgentIdString";
  public static final String STATUSES = "statuses";

  public static final String SEARCH = "search";
  public static final String SKIP_SIZE = "skipSize";
  public static final String TAKE_SIZE = "takeSize";

  public static final String AGENT_USER_LOGIN_ID = "agentUserLoginId";
  public static final String URL_USUARIO = "/users";
  public static final String URL_FINANCAS = "/financial/operations";
  public static final int SKIP_SIZE_MIN = 0;
  public static final int TAKE_SIZE_MAX = 10000;

  public static PageData<UserInfoDto> EMPTY_PAGE_WITH_USERS =
      new PageData<>(emptyList(), 0, 0);

  UserInfoEnricher userInfoEnricher;

  UserProvider userProvider;

  public PageData<UserInfoDto> getPageableUserInfo(Pageable pageRequest,
      long totalElements, Query queryUsers) {
    return getPageableUserInfo(pageRequest,totalElements, queryUsers, false);
  }

  public PageData<UserInfoDto> getPageableUserInfo(Pageable pageRequest,
                                                   long totalElements, Query queryUsers, boolean orderByValue) {
    Integer skipSize = pageRequest.getPageNumber() * pageRequest.getPageSize();
    Integer takeSize = pageRequest.getPageSize();
    if(orderByValue){
      queryUsers.setParameter(SKIP_SIZE, SKIP_SIZE_MIN);
      queryUsers.setParameter(TAKE_SIZE, TAKE_SIZE_MAX);
    }else {
      queryUsers.setParameter(SKIP_SIZE, skipSize);
      queryUsers.setParameter(TAKE_SIZE, takeSize);
    }
    List<UserInfo> usersInfoList = queryUsers.getResultList();
    Long currentAgentId = getCurrentAgentId();
    List<UserInfoDto> userInfoDtoList = usersInfoList.stream()
            .map(item -> UserInfoConverter.convert(item, currentAgentId))
            .collect(toList());

//    userInfoEnricher.enrichDataWithPlatformData(userInfoDtoList);

    if(orderByValue) {
      userInfoDtoList = sortUserInfoList(userInfoDtoList, pageRequest);

      userInfoDtoList = paginateList(userInfoDtoList, pageRequest);
    }

    int totalPages = (int) Math.ceil((double) totalElements / pageRequest.getPageSize());
    return new PageData<>(userInfoDtoList, totalPages, totalElements);
  }

  private List<UserInfoDto> sortUserInfoList(List<UserInfoDto> userInfoDtoList, Pageable pageRequest) {
      Sort.Order orderForCredits = pageRequest.getSort().getOrderFor("money");
      if (orderForCredits != null) {
        if (Sort.Direction.ASC.equals(orderForCredits.getDirection())) {
          userInfoDtoList.sort(Comparator.comparing(UserInfoDto::getCredits));
        } else {
          userInfoDtoList.sort(Comparator.comparing(UserInfoDto::getCredits).reversed());
        }
    }
    return userInfoDtoList;
  }

  private List<UserInfoDto> paginateList(List<UserInfoDto> userInfoDtoList, Pageable pageRequest) {
    int startIndex = pageRequest.getPageNumber() * pageRequest.getPageSize();
    int endIndex = Math.min(startIndex + pageRequest.getPageSize(), userInfoDtoList.size());
    return (startIndex < userInfoDtoList.size())
            ? userInfoDtoList.subList(startIndex, endIndex)
            : Collections.emptyList();
  }

  private Long getCurrentAgentId() {
    UserDetails currentUserDetails = userProvider.getCurrentUserDetails();
    AppUserDetails appUserDetails = (AppUserDetails) currentUserDetails;
    return appUserDetails.getId().longValue();
  }

  public String setAgentPermission() {
    return "INSERT INTO user_group (group_id, user_id)" +
            " VALUES ((SELECT id FROM groups WHERE description = 'Agent Default'), ? );";
  }

  public String findAllUsersSql() {
    return ""
        + " select "
        + "   result.user_name as userName, "
        + "   result.user_role as role, "
        + "   result.balance as balance, "
        + "   result.credits as  credits, "
        + "   result.status as status, "
        + "   result.id as id,"
        + "   result.platform_id as platformId,"
        + "   result.parentId as parentId "
        + " from ( "
        + "   select "
        + "     u.user_name, "
        + "     u.user_role, "
        + "     u.status, "
        + "     w.balance, "
        + "     c.balance as credits, "
        + "     a.id as id,"
        + "     0 as platform_id,"
        + "     a.parent_agent_id as parentId"
        + "   from "
        + "     users u "
        + "     left join agents a on u.id = a.user_id "
        + "     join wallets w on w.id = a.wallet_id "
        + "     join wallets c on c.id = a.credit_wallet_id "
        + "   where "
        + "     u.user_role = 'AGENT' "
        + "   union "
        + "   select "
        + "     p.user_name, "
        + "     'PLAYER' as user_role, "
        + "     p.status as status, "
        + "     pw.balance as balance, "
        + "     0 as credits, "
        + "     p.id as id,"
        + "     p.platform_id,"
        + "     ap.agent_id as parentId "
        + "   from "
        + "     players p "
        + "     join agents_players ap on ap.players_id = p.id "
        + "     join player_wallet pw on pw.player_id = p.id "
        + " ) result "
        + " order by result.user_name "
        + " offset :skipSize rows fetch next :takeSize rows only ";
  }

  public String countFindAllUsersSql() {
    return ""
        + " select "
        + "   count(*) "
        + " from ( "
        + "   select "
        + "     u.user_name "
        + "   from "
        + "     users u "
        + "     left join agents a on u.id = a.user_id "
        + "   where "
        + "     u.user_role = 'AGENT' "
        + "   union "
        + "   select "
        + "     p.user_name "
        + "   from "
        + "     players p "
        + "     join agents_players ap on ap.players_id = p.id "
        + " ) result ";
  }

  public String findAllAgentsAndPlayersForAgentSql() {
    return ""
        + " select "
        + "   result.user_name as userName, "
        + "   result.user_role as role, "
        + "   result.balance as balance, "
        + "   result.credits as  credits, "
        + "   result.status as status, "
        + "   result.id as id, "
        + "   result.platform_id as platformId, "
        + "   result.parent_id as parentId "
        + " from ( "
        + "   select "
        + "     u.user_name as user_name, "
        + "     u.user_role as user_role, "
        + "     u.status as status, "
        + "     w.balance as balance, "
        + "     c.balance as credits, "
        + "     a.id as id,"
        + "     0 as platform_id,"
        + "     a.parent_agent_id as parent_id "
        + "   from "
        + "     users u "
        + "     left join agents a on u.id = a.user_id  "
        + "     join wallets w on w.id = a.wallet_id "
        + "     join wallets c on c.id = a.credit_wallet_id "
        + "   where "
        + "     a.id in (:subAgentIdList) "
        + "   union all"
        + "   select "
        + "     p.user_name as user_name,  "
        + "     'PLAYER' as user_role, "
        + "     p.status as status, "
        + "     pw.balance as balance, "
        + "     0 as credits, "
        + "     p.id as id, "
        + "     p.platform_id , "
        + "     ap.agent_id as parent_id"
        + "   from "
        + "     players p "
        + "     join agents_players ap on ap.players_id = p.id "
        + "     join player_wallet pw on pw.player_id = p.id "
        + "       and ap.agent_id in (:subAgentIdList, :parentAgentId) "
        + " ) result "
        + " order by result.user_name "
        + " offset :skipSize rows fetch next :takeSize rows only ";
  }

  public String countFindAllAgentsAndPlayersForAgentSql() {
    return ""
        + " select "
        + "   count(*) "
        + " from ( "
        + "   select "
        + "     u.user_name "
        + "   from "
        + "     users u "
        + "     left join agents a on u.id = a.user_id "
        + "   where "
        + "     a.id in (:subAgentIdList) "
        + "   union "
        + "   select "
        + "     p.user_name "
        + "   from "
        + "     players p "
        + "     join agents_players ap on ap.players_id = p.id "
        + "   where "
        + "     ap.agent_id in (:subAgentIdList, :parentAgentId) "
        + " ) result ";
  }

  public String findAllAgentsSql() {
    return ""
        + " select "
        + "   u.user_name as userName, "
        + "   u.user_role as role, "
        + "   w.balance as balance, "
        + "   c.balance as credits, "
        + "   u.status as status, "
        + "   a.id as id,"
        + "   0 as platformId,"
        + "   a.parent_agent_id as parentId"
        + " from "
        + "   agents a "
        + "   left join users u on u.id = a.user_id"
        + "   left join wallets w on w.id = a.wallet_id "
        + "   left join wallets c on c.id = a.credit_wallet_id "
        + "   where u.user_role = 'AGENT'"
        + " order by u.user_name "
        + " offset :skipSize rows fetch next :takeSize rows only ";
  }

  public String findAllPlayersSql() {
    return ""
        + " select "
        + "   p.user_name as userName, "
        + "   'PLAYER' as role, "
        + "   pw.balance as balance, "
        + "   0 as credits, "
        + "   p.status as status, "
        + "   p.id as id,"
        + "   p.platform_id as platformId,"
        + "   ap.agent_id as parentId"
        + " from "
        + "   players p "
        + "   join agents_players ap on ap.players_id = p.id "
        + "   join player_wallet pw on pw.player_id = p.id "
        + " order by p.user_name "
        + " offset :skipSize rows fetch next :takeSize rows only ";
  }

  public String findAllSubAgentsSql() {
    return ""
        + " select "
        + "   u.user_name as userName, "
        + "   u.user_role as role, "
        + "   w.balance as balance, "
        + "   c.balance as credits, "
        + "   u.status as status, "
        + "   a.id as id,"
        + "   0 as platformId,"
        + "   a.parent_agent_id as parentId "
        + " from "
        + "   agents a "
        + "   left join users u on u.id = a.user_id "
        + "   left join wallets w on w.id = a.wallet_id "
        + "   left join wallets c on c.id = a.credit_wallet_id "
        + " where "
        + "   a.id in :subAgentIdList "
        + " order by u.user_name "
        + " offset :skipSize rows fetch next :takeSize rows only ";
  }

  public String findAllSubAgentsForOperatorSql() {
    return ""
        + " select "
        + "   u.user_name as userName, "
        + "   u.user_role as role, "
        + "   w.balance as balance, "
        + "   c.balance as credits, "
        + "   u.status as status, "
        + "   a.id as id,"
        + "   0 as platformId,"
        + "   a.parent_agent_id as parentId "
        + " from "
        + "   agents a "
        + "   left join users u on u.id = a.user_id "
        + "   left join wallets w on w.id = a.wallet_id "
        + "   left join wallets c on c.id = a.credit_wallet_id "
        + " where "
        + "   u.user_role = 'AGENT' "
        + " order by u.user_name "
        + " offset :skipSize rows fetch next :takeSize rows only ";
  }

  public String findAllPlayersForAgentListSql() {
    return ""
        + " select "
        + "   p.user_name as userName, "
        + "   'PLAYER' as role, "
        + "   pw.balance as balance, "
        + "   0 as credits, "
        + "   p.status as status, "
        + "   p.id as id,"
        + "   ap.agent_id as parentId,"
        + "   p.platform_id as platformId"
        + " from "
        + "   players p "
        + "   join agents_players ap on ap.players_id = p.id "
        + "   join player_wallet pw on pw.player_id = p.id "
        + " where "
        + "   ap.agent_id in (:subAgentIdList) "
        + " order by p.user_name "
        + " offset :skipSize rows fetch next :takeSize rows only ";
  }

  public String findAllPlayersForOperatorSql() {
    return ""
        + " select "
        + "   p.user_name as userName, "
        + "   'PLAYER' as role, "
        + "   pw.balance as balance, "
        + "   0 as credits, "
        + "   p.status as status, "
        + "   p.id as id,"
        + "   p.platform_id as platformId,"
        + "   ap.agent_id as parentId"
        + " from "
        + "   players p "
        + "   join agents_players ap on ap.players_id = p.id "
        + "   join player_wallet pw on pw.player_id = p.id "
        + " order by p.user_name "
        + " offset :skipSize rows fetch next :takeSize rows only ";
  }

  public String countFindAllPlayersForAgentListSql() {
    return ""
        + " select "
        + "   count(*) "
        + " from ( "
        + "   select "
        + "     p.user_name as userName, "
        + "     'PLAYER' as role, "
        + "     pw.balance as balance, "
        + "     0 as credits,  "
        + "     p.status as status, "
        + "     p.id as id, "
        + "     p.platform_id as platformId"
        + "   from "
        + "     players p "
        + "     join agents_players ap on ap.players_id = p.id "
        + "     join player_wallet pw on pw.player_id = p.id "
        + "   where "
        + "     ap.agent_id in (:subAgentIdList) "
        + " ) result ";
  }

  public String findAllAgentsAndPlayersForAgentWithSearchByUserNameSql(String orderByClause) {
    return
        " SELECT " +
        "    vwuw.user_name AS userName, " +
        "    vwuw.user_role AS role, " +
        "    vwuw.balance, " +
        "    vwuw.credits, " +
        "    vwuw.status, " +
        "    vwuw.id, " +
        "    vwuw.platform_id AS platformId, " +
        "    vwuw.parent_id AS parentId " +
        "FROM ext_vw_user_wallet vwuw " +
        "JOIN agents a ON a.id = vwuw.parent_id " +
        "WHERE vwuw.id != :parentAgentId " +
        "AND CHARINDEX(:parentAgentIdString,a.parent_tree) > 0 " +
        "AND lower(vwuw.user_name) like(:search) " +
        "AND vwuw.status in (:statuses) " +
        "AND vwuw.status != 'CLOSED' " +
        "order by " + orderByClause +
        " offset :skipSize rows fetch next :takeSize rows only ; ";
  }

  public String findAllPlayersForAgentWithSearchByUserNameSql(String orderByClause) {
    return
            " SELECT " +
            "    vwuw.user_name AS userName, " +
            "    vwuw.user_role AS role, " +
            "    vwuw.balance, " +
            "    vwuw.credits, " +
            "    vwuw.status, " +
            "    vwuw.id, " +
            "    vwuw.platform_id AS platformId, " +
            "    vwuw.parent_id AS parentId " +
            "FROM ext_vw_user_wallet vwuw " +
            "JOIN agents a ON a.id = vwuw.parent_id " +
            "WHERE vwuw.id != :parentAgentId " +
            "AND CHARINDEX(:parentAgentIdString,a.parent_tree) > 0 " +
            "AND lower(vwuw.user_name) like(:search) " +
            "AND vwuw.status in (:statuses) " +
            "AND vwuw.status != 'CLOSED' " +
            "AND vwuw.user_role = 'PLAYER' " +
            "order by " + orderByClause +
            " offset :skipSize rows fetch next :takeSize rows only ; ";
  }

  public String findAllPlayersForSumAgentWithSearchByUserNameSql() {
    return "   select "
            + "     pw.balance as balance, "
            + "     p.platform_id as id"
            + "   from "
            + "     players p "
            + "     join agents_players ap on ap.players_id = p.id  "
            + "     join player_wallet pw on pw.player_id = p.id "
            + "     join agents a on ap.agent_id = a.id"
            + "   where "
            + "    charindex(:parentAgentIdString, a.parent_tree) > 0 "
            + "   and lower(p.user_name) like lower(:search) "
            + "   and p.status in (:statuses)";
  }

  public String findAllAgentsForAgentWithSearchByUserNameSql(String orderByClause) {
    return
            " SELECT " +
            "    vwuw.user_name AS userName, " +
            "    vwuw.user_role AS role, " +
            "    vwuw.balance, " +
            "    vwuw.credits, " +
            "    vwuw.status, " +
            "    vwuw.id, " +
            "    vwuw.platform_id AS platformId, " +
            "    vwuw.parent_id AS parentId " +
            "FROM ext_vw_user_wallet vwuw " +
            "JOIN agents a ON a.id = vwuw.parent_id " +
            "WHERE vwuw.id != :parentAgentId " +
            "AND CHARINDEX(:parentAgentIdString,a.parent_tree) > 0 " +
            "AND lower(vwuw.user_name) like(:search) " +
            "AND vwuw.status in (:statuses) " +
            "AND vwuw.status != 'CLOSED' " +
            "AND vwuw.user_role = 'AGENT' " +
            "order by " + orderByClause +
            " offset :skipSize rows fetch next :takeSize rows only ; ";
  }

  public String getCreditsByUserName() {
    return "select credits from ext_vw_user_wallet where user_name = :username and status = 'ACTIVE'";
  }

  public String sumBalanceForAgentWithSearchByUserNameSql() {
    return
            "   select "
                    + "     coalesce(sum(a.balance),0) as balance, "
                    + "     coalesce(sum(c.balance),0) as credits "
                    + "   from agents a"
                    + "     join users u on u.id = a.user_id"
                    + "     join wallets c on c.id = a.credit_wallet_id "
                    + "   where "
                    + "    charindex(:parentAgentIdString, a.parent_tree) > 0 "
                    + "   and (lower(u.user_name) like lower(:search) and u.status in (:statuses)) "
                    + "   and a.id != :parentAgentId ";
  }


  public String countAllAgentsAndPlayersForAgentWithoutParentAgentSql() {
    return
        " select "
            + "   count(*) "
            + " from ( "
            + "   select "
            + "     u.user_name, "
            + "     u.user_role "
            + "   from "
            + "     users u "
            + "     left join agents a on u.id = a.user_id"
            + "   where "
            + "     charindex(:parentAgentIdString, a.parent_tree) > 0 "
            + "   and  u.status in (:statuses) and a.id != :parentAgentId"
            + "   and u.status != 'CLOSED'"
            + "   union "
            + "   select "
            + "     p.user_name, "
            + "     'PLAYER' "
            + "   from "
            + "     players p "
            + "     join agents_players ap on ap.players_id = p.id "
            + " join agents a on ap.agent_id = a.id"
            + "   where "
            + "     charindex(:parentAgentIdString, a.parent_tree) > 0 "
            + "   and  p.status in (:statuses) "
            + "   and p.status != 'CLOSED'"
            + " ) result "
            + " where "
            + "   lower(user_name) like lower(:search) ";
  }

  public String countAllPlayersForAgentWithoutParentAgentSql() {
    return "   select count(*) "
            + "   from "
            + "     players p "
            + "     join agents_players ap on ap.players_id = p.id "
            + " join agents a on ap.agent_id = a.id"
            + "   where "
            + "     charindex(:parentAgentIdString, a.parent_tree) > 0 "
            + "   and lower(p.user_name) like lower(:search) "
            + "   and  p.status in (:statuses) "
            + "   and p.status != 'CLOSED'";
  }

  public String countAllAgentsForAgentWithoutParentAgentSql() {
    return " select count(user_id) from agents a join users u on u.id = a.user_id"
        + " where charindex(:parentAgentIdString, a.parent_tree) > 0 "
        + " and lower(u.user_name) like lower(:search) and u.status in (:statuses) and u.status != 'CLOSED' and a.id != :parentAgentId";
  }

  public String findDirectSubAgentsAndDirectPlayersForAgentListSql(String orderByClause) {
    return "   select "
        + "     user_name as userName, "
        + "     user_role as role, "
        + "     balance , "
        + "     credits, "
        + "     status , "
        + "     id ,"
        + "     platform_id as platformId,"
        + "     parent_id as parentId"
        + " from ext_vw_user_wallet vwuw "
        + " where isNULL(vwuw.parent_id, -1) = :parentAgentId "
        + " and vwuw.status in (:statuses) "
        + " order by " + orderByClause
        + " offset :skipSize rows fetch next :takeSize rows only";
  }

  public String countDirectSubAgentsAndDirectPlayersForAgentListSql() {
    return ""
        + " select "
        + "   count(*) "
        + " from ("
        + "   select "
        + "     a.id "
        + "   from "
        + "     agents a "
        + "     join users u on u.id = a.user_id "
        + "   where "
        + "     isNULL(a.parent_agent_id, -1) = :parentAgentId "
        + "and u.status in (:statuses)"
        + "   union "
        + "   select "
        + "     p.id "
        + "   from "
        + "     players p "
        + "     join agents_players ap on ap.players_id = p.id "
        + "   where "
        + "     ap.agent_id = :parentAgentId and p.status in (:statuses)"
        + " ) result";
  }

  public String countDirectSubAgentsForAgentListSql() {
    return ""
        + " select "
        + "   count(*) "
        + " from "
        + "     agents a "
        + "     join users u on u.id = a.user_id "
        + "   where "
        + "     ISNULL(a.parent_agent_id, -1) = :parentAgentId"
        + " and u.status in (:statuses) ";
  }

  public String findDirectSubAgentsForAgentListSql(String orderByClause) {
    return ""
        + "   select "
        + "     u.user_name as userName, "
        + "     u.user_role as role, "
        + "     a.balance as balance, "
        + "     c.balance as credits, "
        + "     u.status as status, "
        + "     a.id as id,"
        + "     0 as platformId,"
        + "     a.parent_agent_id as parentId"
        + "   from "
        + "     agents a "
        + "     join users u on u.id = a.user_id "
        + "     join wallets c on c.id = a.credit_wallet_id "
        + "   where "
        + "     ISNULL(a.parent_agent_id, -1) = :parentAgentId "
        + "and u.status in (:statuses)"
        + " order by " + orderByClause
        + " offset :skipSize rows fetch next :takeSize rows only";
  }

  public String countDirectPlayersForAgentListSql() {
    return "select count(*) from (\n" +
            "   select CASE\n" +
            "        WHEN ub.status = 0 THEN 'ACTIVE'\n" +
            "        WHEN ub.status = 1 THEN 'BLOCKED'\n" +
            "        WHEN ub.status = 2 THEN 'EMAIL_CONFIRMATION_REQUIRED'\n" +
            "        WHEN ub.status = 3 THEN 'AUTO_BLOCKED'\n" +
            "        WHEN ub.status = 4 THEN 'INACTIVITY_BLOCKED'\n" +
            "        WHEN ub.status = 5 THEN 'CLOSED'\n" +
            "  END AS status\n" +
            "   from\n" +
            "     players p\n" +
            "     join agents_players ap on ap.players_id = p.id\n" +
            "     join player_wallet pw on pw.player_id = p.id\n" +
            "     inner join ext_vw_user_balance ub on ub.user_id = p.platform_id\n" +
            "   where\n" +
            "     ap.agent_id = :parentAgentId and p.status <> 'CLOSED' ) a\n" +
            "   where a.status in (:statuses) ";
  }

  public String findDirectPlayersForAgentListSql(String orderByClause) {
    return " select * from ( "
        + "   select "
        + "     p.user_name as userName, "
        + "     'PLAYER' as role, "
        + "     pw.balance as balance, "
        + "     ub.balance as credits, "
        + "     CASE "
        + "        WHEN ub.status = 0 THEN 'ACTIVE'"
        + "        WHEN ub.status = 1 THEN 'BLOCKED'"
        + "        WHEN ub.status = 2 THEN 'EMAIL_CONFIRMATION_REQUIRED'"
        + "        WHEN ub.status = 3 THEN 'AUTO_BLOCKED'"
        + "        WHEN ub.status = 4 THEN 'INACTIVITY_BLOCKED'"
        + "        WHEN ub.status = 5 THEN 'CLOSED'"
        + "  END AS status, "
        + "     p.id as id, "
        + "     p.platform_id as platformId, "
        + "     ap.agent_id as parentId"
        + "   from "
        + "     players p "
        + "     join agents_players ap on ap.players_id = p.id "
        + "     join player_wallet pw on pw.player_id = p.id "
        + "     inner join ext_vw_user_balance ub on ub.user_id = p.platform_id "
        + "   where "
        + "     ap.agent_id = :parentAgentId and p.status <> 'CLOSED' ) a "
        + "   where a.status in (:statuses) "
        + " order by " + orderByClause
        + " offset :skipSize rows fetch next :takeSize rows only";
  }

  public String sumDirectAgentsCreditsAndBalance() {
    return ""
        + " select "
        + "   coalesce(sum(a.balance),0) as balance, "
        + "   coalesce(sum(c.balance),0) as credits "
        + " from "
        + "   agents a "
        + "   join users u on u.id = a.user_id "
        + "   join wallets c on c.id = a.credit_wallet_id "
        + " where "
        + "   ISNULL(a.parent_agent_id, -1) = :parentAgentId "
        + "   and u.status in (:statuses) "
        + "   and u.user_name LIKE CONCAT('%', :search, '%')";
  }

  public String directPlayersCreditsAndBalance() {
    return ""
        + " select "
        + "   pw.balance as balance, "
        + "   p.platform_id as id "
        + " from "
        + "   players p "
        + "   join agents_players ap on ap.players_id = p.id "
        + "   join player_wallet pw on pw.player_id = p.id "
        + " where "
        + "   ap.agent_id = :parentAgentId and p.status in (:statuses)"
        + "   and p.user_name LIKE CONCAT('%', :search, '%')";
  }
  public String findAllAgentsAndPlayersForAutoCompleteAgentWithSearchByUserNameSql() {
    return
            "SELECT " +
                    "    result.user_name AS userName, " +
                    "    result.user_role AS role, " +
                    "    result.balance AS balance," +
                    "    result.credits AS credits, " +
                    "    result.status AS status, " +
                    "    result.id AS id, " +
                    "    result.platform_id AS platformId, " +
                    "    result.parentId AS parentId " +
                    "FROM ( " +
                    "    SELECT " +
                    "        u.user_name, " +
                    "        u.user_role, " +
                    "        a.balance, " +
                    "        u.status AS status, " +
                    "        c.balance AS credits, " +
                    "        a.id AS id, " +
                    "        0 AS platform_id, " +
                    "        a.parent_agent_id AS parentId, " +
                    "        1 AS priority, " +
                    "        uti.operation_date " +
                    "    FROM " +
                    "        user_transaction_interval uti " +
                    "        inner join users u on uti.user_player_id = u.id " +
                    "        inner JOIN agents a ON u.id = a.user_id  " +
                    "        inner JOIN wallets c ON c.id = a.credit_wallet_id " +
                    "    WHERE " +
                    "        CHARINDEX(:parentAgentIdString, a.parent_tree) > 0 " +
                    "        AND u.status IN (:statuses) " +
                    "        AND a.id != :agentUserLoginId " +
                    "        AND uti.operation_date >= DATEADD(DAY, -7, GETDATE()) " +
                    "        AND LOWER(user_name) LIKE LOWER(:search) " +
                    " " +
                    "    UNION " +
                    " " +
                    "    SELECT " +
                    "        p.user_name, " +
                    "        'PLAYER' AS user_role, " +
                    "        pw.balance AS balance, " +
                    "        p.status AS status, " +
                    "        0 AS credits, " +
                    "        p.id AS id, " +
                    "        p.platform_id AS platformId," +
                    "        ap.agent_id AS parentId, " +
                    "        2 AS priority, " +
                    "        uti.operation_date " +
                    "    FROM " +
                    "        user_transaction_interval uti " +
                    "       inner join players p on uti.user_player_id = p.id " +
                    "       inner JOIN agents_players ap ON ap.players_id = p.id " +
                    "       inner JOIN player_wallet pw ON pw.player_id = p.id " +
                    "       inner JOIN agents a ON ap.agent_id = a.id " +
                    "      " +
                    "    WHERE " +
                    "        CHARINDEX(:parentAgentIdString, a.parent_tree) > 0 " +
                    "        AND p.status IN (:statuses) " +
                    "        AND uti.operation_date >= DATEADD(DAY, -7, GETDATE()) " +
                    "        AND LOWER(user_name) LIKE LOWER(:search) " +
                    ") AS result " +
                    "ORDER BY " +
                    "    result.operation_date " +
                    " OFFSET :skipSize ROWS FETCH NEXT :takeSize ROWS ONLY";
  }

  public String findAgentsIdsForDeactivationByInactivitySql() {
    return
            "SELECT u.id, u.user_name as userName, a.id as agentId  FROM users u\n" +
                    "JOIN agents a ON u.id = a.user_id\n" +
                    "WHERE u.id NOT IN (\n" +
                    "\n" +
                    "    -- IDs de agentes ativos\n" +
                    "    SELECT DISTINCT u.id\n" +
                    "    FROM users u\n" +
                    "    JOIN user_transaction ut ON u.id = ut.user_id\n" +
                    "    WHERE ut.operation_date >= DATEADD(MONTH, -6, GETDATE())\n" +
                    "      AND u.status = 'ACTIVE'\n" +
                    "    \n" +
                    "    UNION  \n" +
                    "\n" +
                    "    -- IDs de agentes com comissão\n" +
                    "    SELECT DISTINCT u.id  \n" +
                    "    FROM users u\n" +
                    "    JOIN agents a ON u.id = a.user_id\n" +
                    "    LEFT JOIN (\n" +
                    "        SELECT \n" +
                    "            direct_player_agent_id AS user_id,\n" +
                    "            SUM(amount) AS amount,\n" +
                    "            SUM(bonus) AS bonus\n" +
                    "        FROM dbo.user_transaction trans_deposit\n" +
                    "        WHERE trans_deposit.operation_type = 'DEPOSIT'\n" +
                    "          AND trans_deposit.operation_date >= DATEADD(MONTH, -6, GETDATE())\n" +
                    "        GROUP BY direct_player_agent_id\n" +
                    "    ) deposits ON u.id = deposits.user_id\n" +
                    "    LEFT JOIN (\n" +
                    "        SELECT \n" +
                    "            direct_player_agent_id AS user_id,\n" +
                    "            SUM(amount) AS amount\n" +
                    "        FROM dbo.user_transaction trans_withdrawal\n" +
                    "        WHERE trans_withdrawal.operation_type = 'WITHDRAWAL'\n" +
                    "          AND trans_withdrawal.operation_date >= DATEADD(MONTH, -6, GETDATE())\n" +
                    "        GROUP BY direct_player_agent_id\n" +
                    "    ) withdrawal ON u.id = withdrawal.user_id\n" +
                    "    WHERE u.status = 'ACTIVE'\n" +
                    "    and (deposits.amount > 0 OR withdrawal.amount > 0\n)" +
                    "\n" +
                    "    UNION \n" +
                    "\n" +
                    "    -- IDs de agentes que possuem jogadores com saldo acima de 1000 ARS (não podem ser desativados)\n" +
                    "    SELECT DISTINCT u.id \n" +
                    "    FROM users u \n" +
                    "    JOIN agents a ON u.id = a.user_id \n" +
                    "    WHERE a.id IN (\n" +
                    "        SELECT DISTINCT parent_id \n" +
                    "        FROM ext_vw_user_wallet \n" +
                    "        WHERE credits > 100000\n" +
                    "          AND status = 'ACTIVE'\n" +
                    "    )\n" +
                    "    UNION\n" +
                    "    \n" +
                    "    -- IDs de agentes que possuem jogadores com bonus acima de 100 ARS (não podem ser desativados)\n" +
                    "    SELECT DISTINCT u.id \n" +
                    "    FROM users u \n" +
                    "    JOIN agents a ON u.id = a.user_id \n" +
                    "    WHERE a.id IN (\n" +
                    "        SELECT DISTINCT parent_id \n" +
                    "        FROM ext_vw_user_wallet \n" +
                    "        WHERE bonus_balance > 10000\n" +
                    "          AND status = 'ACTIVE'\n" +
                    "    )" +
                    ") \n" +
                    "AND status <> 'CLOSED'";
  }

  public String findAllSubAgentsByParentIdSql() {
    return
            "  SELECT u.id, u.user_name as userName, p.id as agentId  \n" +
                    "FROM users u\n" +
                    "JOIN agents p ON u.id = p.user_id\n" +
                    "WHERE u.status <> 'CLOSED'\n" +
                    "AND p.parent_agent_id IN (:parentAgentId) ";
  }

  public String findAllPlayersSubAgentsByParentIdSql() {
    return
            "  SELECT p.user_id as id, p.user_name as userName, ap.agent_id as agentId \n" +
                    "FROM agents_players ap\n" +
                    "JOIN players p ON ap.players_id = p.id\n" +
                    "WHERE p.status <> 'CLOSED'\n" +
                    "AND ap.agent_id IN (:parentAgentId) ";
  }

}
