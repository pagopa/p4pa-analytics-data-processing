package it.gov.pagopa.analytics.process.exception.transcoder.handler;

import it.gov.pagopa.analytics.process.dto.generated.ErrorDTO;
import it.gov.pagopa.analytics.process.dto.generated.ErrorFieldDTO;
import it.gov.pagopa.analytics.process.exception.transcoder.ExceptionMessageTranscoded;
import it.gov.pagopa.analytics.process.exception.transcoder.ExceptionMessageTranscoder;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.List;

public class MissingServletRequestParameterExceptionMessageTranscoder implements ExceptionMessageTranscoder<MissingServletRequestParameterException> {

  @Override
  public ExceptionMessageTranscoded transcode(MissingServletRequestParameterException missingServletRequestParameterException) {
    return new ExceptionMessageTranscoded(
      ErrorDTO.CategoryEnum.BAD_REQUEST.name(),
      missingServletRequestParameterException.getMessage(),
      List.of(new ErrorFieldDTO(missingServletRequestParameterException.getParameterName(), "NotNull", missingServletRequestParameterException.getMessage())));
  }
}
