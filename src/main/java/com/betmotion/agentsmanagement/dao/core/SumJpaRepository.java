package com.betmotion.agentsmanagement.dao.core;

import org.springframework.data.jpa.domain.Specification;

public interface SumJpaRepository<T> {

  //TODO: Fixme - domain class should b received from repository. Now it is code duplication
  <S extends Number> S sum(Specification<T> spec, Class<S> resultType, String fieldName,
      Class<T> domainClass);
}
