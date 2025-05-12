package com.betmotion.agentsmanagement.dao.core;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class SumJpaRepositoryImpl<T> implements SumJpaRepository<T> {

  @Autowired
  private EntityManager entityManager;

  @Override
  public <S extends Number> S sum(Specification<T> spec, Class<S> resultType, String fieldName,
      Class<T> domainClass) {
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    CriteriaQuery<S> query = builder.createQuery(resultType);
    Root<T> root = applySpecificationToCriteria(spec, query, domainClass);
    query.select(builder.sum(root.get(fieldName).as(resultType)));
    TypedQuery<S> typedQuery = entityManager.createQuery(query);
    return typedQuery.getSingleResult();
  }


  protected <S> Root<T> applySpecificationToCriteria(Specification<T> spec, CriteriaQuery<S> query,
      Class<T> domainClass) {
    Root<T> root = query.from(domainClass);
    if (spec == null) {
      return root;
    }
    CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    Predicate predicate = spec.toPredicate(root, query, builder);
    if (predicate != null) {
      query.where(predicate);
    }
    return root;
  }
}
