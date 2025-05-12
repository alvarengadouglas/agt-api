package com.betmotion.agentsmanagement.service.specifications;

import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;

@UtilityClass
public class SpecificationUtils {

  public static <T> Specification<T> joinSpecifications(List<Specification<T>> items) {
    Specification<T> result = items.get(0);
    for (int i = 1, n = items.size(); i < n; i++) {
      result = result.and(items.get(i));
    }
    return result;
  }
}
