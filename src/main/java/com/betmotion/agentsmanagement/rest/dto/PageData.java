package com.betmotion.agentsmanagement.rest.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public class PageData<T> {

  List<T> data;

  int totalPages;

  long totalElements;
}
