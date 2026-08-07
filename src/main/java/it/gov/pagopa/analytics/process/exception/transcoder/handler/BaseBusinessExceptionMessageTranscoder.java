package it.gov.pagopa.analytics.process.exception.transcoder.handler;

import it.gov.pagopa.analytics.process.exception.common.BaseBusinessException;
import it.gov.pagopa.analytics.process.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.analytics.process.exception.transcoder.ExceptionMessageTranscoder;

public class BaseBusinessExceptionMessageTranscoder implements ExceptionMessageTranscoder<BaseBusinessException> {
  @Override
  public ExceptionMessageTranscoded transcode(BaseBusinessException businessException) {
    return new ExceptionMessageTranscoded(businessException.getCode(), businessException.getMessage(), businessException.getFields());
  }
}
