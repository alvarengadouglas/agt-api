package com.betmotion.agentsmanagement.rest;

import static com.betmotion.agentsmanagement.utils.Constants.APP_DEFAULT_PAGE_INDEX;
import static com.betmotion.agentsmanagement.utils.Constants.APP_DEFAULT_PAGE_SIZE;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.ResponseEntity.ok;

import com.betmotion.agentsmanagement.annotations.ApiPageable;
import com.betmotion.agentsmanagement.domain.UserRole;
import com.betmotion.agentsmanagement.rest.dto.PageData;
import com.betmotion.agentsmanagement.rest.dto.operator.ChangePasswordDto;
import com.betmotion.agentsmanagement.rest.dto.operator.OperatorAddCreditsDto;
import com.betmotion.agentsmanagement.rest.dto.operator.OperatorDto;
import com.betmotion.agentsmanagement.rest.dto.user.UserInfoDto;
import com.betmotion.agentsmanagement.service.OperatorService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = PRIVATE)
@Slf4j
@RequestMapping("/api/operator")
public class OperatorController {

  OperatorService operatorService;

  @GetMapping()
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT')")
  public ResponseEntity<OperatorDto> get() {
    return ok(operatorService.get());
  }

  @PutMapping(value = "/changePassword")
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT')")
  public ResponseEntity<OperatorDto> changePassword(@RequestBody @Valid ChangePasswordDto password) {
    return ok(operatorService.changePassword(password));
  }

  @GetMapping(value = "/allUsers")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT')")
  public ResponseEntity<PageData<UserInfoDto>> getAllUsers(
      @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
      @SortDefault.SortDefaults({@SortDefault(sort = "user_name", direction = Sort.Direction.ASC)
      }) Pageable pageRequest,
      @RequestParam(name = "role", required = false) UserRole role) {
    PageData<UserInfoDto> result = role == null
        ? operatorService.getAllUsers(pageRequest)
        : operatorService.getAllUsers(pageRequest, role);
    return ok(result);
  }

  @GetMapping(value = "/allUsers/subAgentId/{subAgentId}")
  @ApiPageable
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT')")
  public ResponseEntity<PageData<UserInfoDto>> getAllUsersByUserId(
      @ApiIgnore @PageableDefault(page = APP_DEFAULT_PAGE_INDEX, size = APP_DEFAULT_PAGE_SIZE)
      @SortDefault.SortDefaults({@SortDefault(sort = "user_name", direction = Sort.Direction.ASC)
      }) Pageable pageRequest,
      @PathVariable(name = "subAgentId") Integer userId) {
    return ok(operatorService.getAllUsers(pageRequest, userId));
  }

  @PostMapping("/add-operator-credits")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasAnyAuthority('OPERATOR_DEFAULT')")
  public void addOperatorCredits(@RequestBody @Valid OperatorAddCreditsDto dto) {
    operatorService.addOperatorCredits(dto);
  }
}
