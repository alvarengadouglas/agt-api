package com.betmotion.agentsmanagement.dao.impl;


import com.betmotion.agentsmanagement.rest.dto.reports.IPLoginsDTO;
import com.betmotion.agentsmanagement.rest.dto.reports.IPLoginsResponse;
import com.betmotion.agentsmanagement.utils.IPUtils;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.List;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@Repository
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
public class IpLoginDaoImpl {
    public static final String MAPPING_IP_LOGINS = "Mapping.ipLogins";

    EntityManager em;

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public IPLoginsResponse getAllLogins(String sql) {
        Query query = em.createNativeQuery(sql, MAPPING_IP_LOGINS);
        IPLoginsResponse ipLogins = new IPLoginsResponse();


        List<IPLoginsDTO> ipLoginsDTOList = query.getResultList();

        ipLoginsDTOList.stream().map(ipLoginsDTO -> {
            ipLoginsDTO.setIp(IPUtils.longToIp(Long.parseLong(ipLoginsDTO.getIp())));
            return ipLoginsDTO;
        }).collect(Collectors.toList());

        ipLogins.setLogins(ipLoginsDTOList);
        return ipLogins;

    }
}
