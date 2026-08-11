package it.gov.pagopa.analytics.process.exception.common;

import it.gov.pagopa.analytics.process.exception.NotRetryableActivityException;

@SuppressWarnings("java:S110") // Suppress "Inheritance tree of classes should not be too deep": allowed for exception hierarchy
public class NotFoundException extends NotRetryableActivityException {
  public NotFoundException(String code, String message) {
    super(code, message);
  }
}

