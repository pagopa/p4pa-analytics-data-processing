package it.gov.pagopa.analytics.process.exception.custom;

import it.gov.pagopa.analytics.process.exception.common.BaseBusinessException;

/**
 * A custom exception that represents an internal error related to workflows and extends {@link RuntimeException}.
 *
 */
public class WorkflowInternalErrorException extends BaseBusinessException {

  /**
   * Constructs a new {@code WorkflowInternalErrorException} with the specified detail message.
   *
   * @param message the detail message explaining the cause of the exception.
   */
  public WorkflowInternalErrorException(String message) {
    super("WORKFLOW_INTERNAL_ERROR", message);
  }
}
