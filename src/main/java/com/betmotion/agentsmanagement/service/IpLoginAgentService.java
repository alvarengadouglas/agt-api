package com.betmotion.agentsmanagement.service;

import com.betmotion.agentsmanagement.dao.impl.IpLoginDaoImpl;
import com.betmotion.agentsmanagement.rest.dto.reports.IPLoginsResponse;
import com.betmotion.agentsmanagement.rest.dto.reports.IpLoginReportRequest;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

import static lombok.AccessLevel.PRIVATE;


@Service
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
public class IpLoginAgentService {

    IpLoginDaoImpl ipLoginDao;

    public IPLoginsResponse getAllLogins(IpLoginReportRequest ipLoginReportRequest) {
        String query = getSqlIpLoginsReport(ipLoginReportRequest);

        return ipLoginDao.getAllLogins(query);
    }


    private String getSqlIpLoginsReport(IpLoginReportRequest ipLoginReportRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        String query = "WITH ip_logins_local AS " +
                "(" +
                " SELECT user_id, ip, login_date, device " +
                " FROM ext_ip_logins il  " +
                "), " +
                "ip_logins_union AS  " +
                "( " +
                " SELECT p.user_name,  " +
                "     'PLAYER' AS role,  " +
                "     il.ip,  " +
                "     il.login_date,  " +
                "     il.device " +
                " FROM ip_logins_local il " +
                " INNER JOIN players p ON p.platform_id = il.user_id " +
                " WHERE il.login_date  BETWEEN '" + ipLoginReportRequest.getBeginDate().format(formatter)
                + "' AND '" + ipLoginReportRequest.getEndDate().format(formatter) + "' " +
                " UNION " +
                " SELECT u.user_name,  " +
                "   'AGENT' AS role,  " +
                "   ila.ip,  " +
                "   ila.login_date,  " +
                "   ila.device " +
                " FROM ip_logins_agents ila " +
                " INNER JOIN users u ON ila.agent_id = u.id " +
                " WHERE ila.login_date  BETWEEN '" + ipLoginReportRequest.getBeginDate().format(formatter)
                + "' AND '" + ipLoginReportRequest.getEndDate().format(formatter) + "'), " +
                " ip_login_agg AS ( " +
                " SELECT  ip  " +
                " FROM  ip_logins_union " +
                " GROUP BY ip  " +
                " HAVING COUNT(DISTINCT user_name) > 1 " +
                ") " +
                "SELECT ilu.ip, user_name as username, ilu.role, login_date as loginDate, device " +
                "FROM ip_logins_union ilu ";

        if (ipLoginReportRequest.getOnlyRepeated()) {
            query += " INNER JOIN ip_login_agg ON ip_login_agg.ip = ilu.ip ";
        }

        if (ipLoginReportRequest.getRole() != null) {
            query += " WHERE ilu.[role] = '" + ipLoginReportRequest.getRole() + "' ";
        }

        if (ipLoginReportRequest.getOnlyRepeated()) {
            query += " ORDER BY ilu.ip ASC";
        } else {
            query += " ORDER BY ilu.login_date DESC";
        }

        return query;
    }
} 