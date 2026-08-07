package it.gov.pagopa.analytics.process.exception.common;

import it.gov.pagopa.analytics.process.dto.generated.ErrorFieldDTO;

import java.util.List;

public class ConflictException extends BaseBusinessException {

  public ConflictException(String code, String message) {
    this(code, message, null);
  }

  public ConflictException(String code, String message, List<ErrorFieldDTO> fieldErrors) {
    super(code, message, fieldErrors, null);
  }

}
