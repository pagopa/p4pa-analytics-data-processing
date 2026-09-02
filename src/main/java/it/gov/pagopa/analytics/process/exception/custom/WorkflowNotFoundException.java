package it.gov.pagopa.analytics.process.exception.custom;

import it.gov.pagopa.analytics.process.exception.common.NotFoundException;

/**
 * A custom exception that represents a not found workflow and extends {@link RuntimeException}.
 *
 */
@SuppressWarnings("java:S110") // Suppress "Inheritance tree of classes should not be too deep": allowed for exception hierarchy
public class WorkflowNotFoundException extends NotFoundException {

  /**
   * Constructs a new {@code WorkflowNotFoundException} with the specified detail message.
   *
   * @param message the detail message explaining the cause of the exception.
   */
  public WorkflowNotFoundException(String message) {
    super("WORKFLOW_NOT_FOUND", message);
  }
}
