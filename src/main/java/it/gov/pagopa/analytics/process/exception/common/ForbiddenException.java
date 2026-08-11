package it.gov.pagopa.analytics.process.exception.common;

import it.gov.pagopa.analytics.process.exception.NotRetryableActivityException;

@SuppressWarnings("java:S110") // Suppress "Inheritance tree of classes should not be too deep": allowed for exception hierarchy
public class ForbiddenException extends NotRetryableActivityException {
  public ForbiddenException(String code, String message) {
    super(code, message);
  }
}
