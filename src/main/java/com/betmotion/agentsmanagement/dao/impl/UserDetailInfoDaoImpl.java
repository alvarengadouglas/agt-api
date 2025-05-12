package com.betmotion.agentsmanagement.dao.impl;

import static lombok.AccessLevel.PRIVATE;

import com.betmotion.agentsmanagement.dao.projection.UserDetailInfo;
import com.betmotion.agentsmanagement.rest.dto.user.UserDetailInfoDto;
import com.betmotion.agentsmanagement.service.converter.UserDetailInfoConverter;
import com.betmotion.agentsmanagement.service.exceptions.ServiceException;
import java.util.List;
import javax.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class UserDetailInfoDaoImpl {

  public static final String MAPPING_USER_DETAIL_INFO = "Mapping.userDetailInfo";

  public static final String AGENT_ID = "agentId";

  public UserDetailInfoDto getUserDetailInfo(Query queryUsers) {
    List<UserDetailInfo> userDetailInfoList = queryUsers.getResultList();
    return userDetailInfoList.stream()
        .map(UserDetailInfoConverter::convert)
        .findFirst().orElseThrow(() -> {
          throw new ServiceException("GE00", null);
        });
  }

  public String findUserDetailInfoForAgentSql() {
    return  "" 
        + " select "
        + "   a.id as id, "
        + "   a.comission_type as commissionType, "
        + "   a.commission as commission, "
        + "   a.comission_sports as commissionSports, "
        + "   a.comission_slots as commissionSlots, "
        + "   a.comission_casino as commissionCasino, "
        + "   u.user_name as userName, "
        + "   u.first_name as fullName, "
        + "   u.mail as email, "
        + "   u.phone as phone, "
        + "   u.user_role as role, "
        + "   u.created_on as createdOn, "
        + "   null as hierarchy, "
        + "   (select "
        + "     u2.user_name "
        + "   from "
        + "     agents a2 "
        + "     join users u2 on u2.id = a2.user_id "
        + "   where "
        + "     a2.id = a.parent_agent_id "
        + "   ) as parentUserName, "
        + "   (select count(*) "
        +   "   from agents a3 "
        +   "     join users us on a3.user_id = us.id  "
        +   "   where a3.parent_agent_id = a.id "
        +   "     and us.status <> 'CLOSED' "
        + "   ) as numberOfDirectSubAgents, "
        + "   (select "
        + "     count(*) "
        + "   from "
        + "     agents a4 "
        + "     join agents_players ap on a4.id = ap.agent_id "
        + "     join players p on ap.players_id = p.id "
        + "   where "
        + "     ap.agent_id = a.id and p.status <> 'CLOSED' "
        + "   ) as numberOfDirectPlayers "
        + "   from "
        + "     agents a "
        + "     join users u on a.user_id = u.id "
        + " where a.id = :agentId ";
  }
}
