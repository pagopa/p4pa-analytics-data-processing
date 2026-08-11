package it.gov.pagopa.analytics.process.config.rest;

import it.gov.pagopa.analytics.process.dto.generated.ErrorFieldDTO;

import java.util.List;

public record PuErrorDTO(
  String category,
  String code,
  String message,
  List<ErrorFieldDTO> fields
) {
}
