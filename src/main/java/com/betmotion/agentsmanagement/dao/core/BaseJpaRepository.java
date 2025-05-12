package com.betmotion.agentsmanagement.dao.core;

import com.betmotion.agentsmanagement.service.exceptions.ServiceException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

@NoRepositoryBean
public interface BaseJpaRepository<TargetT, IdentifierT> extends
    JpaRepository<TargetT, IdentifierT> {

  @Transactional
  default TargetT getEntity(IdentifierT id) {
    return this.findById(id).orElseThrow(() -> {
      throw new ServiceException("ET00", new String[]{id.toString()});
    });
  }

}
