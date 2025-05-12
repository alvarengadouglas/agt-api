package com.betmotion.agentsmanagement.platform.api.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
public class UserTransactionFilterDTO implements Serializable {

	private static final long serialVersionUID = 2123372311244420456L;
	
	private Long id;
    private String gameName;
    private Date startDate;
    private Date endDate;
    private String type;
    private String mode;
    private Long min;
    private Long max;
    private String order;
	@NotNull
    private Long page;
	@NotNull
    private Long pageSize;
    private Integer userId;
}
