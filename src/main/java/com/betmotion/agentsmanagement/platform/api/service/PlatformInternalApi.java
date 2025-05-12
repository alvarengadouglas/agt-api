package com.betmotion.agentsmanagement.platform.api.service;

import com.betmotion.agentsmanagement.platform.api.config.PlatformApiClientConfiguration;
import com.betmotion.agentsmanagement.rest.dto.reports.CommissionAgent;
import com.betmotion.agentsmanagement.rest.dto.reports.CommissionPayload;
import com.betmotion.agentsmanagement.rest.dto.reports.CommissionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@FeignClient(name = "platformInternalApi", configuration = PlatformApiClientConfiguration.class,
        url = "${platform.api.internal}")
public interface PlatformInternalApi {

    @RequestMapping(value = "/agent/report/commission.do", method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    CommissionAgent getCommissionReport(@RequestBody CommissionPayload payload);

    @RequestMapping(value = "/agent/report/commission.do", method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    CommissionAgent getCommissionReportNew(@RequestBody CommissionPayload payload);

    @RequestMapping(value = "/agent/report/commissionjava.do", method = RequestMethod.POST,
            consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    CommissionAgent getCommissionReportJava(@RequestBody CommissionPayload payload);

}
