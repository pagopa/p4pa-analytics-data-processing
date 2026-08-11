package it.gov.pagopa.analytics.process.exception;

import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import it.gov.pagopa.analytics.process.exception.common.CommonExceptionHandlerTest;
import it.gov.pagopa.analytics.process.exception.custom.WorkflowInternalErrorException;
import it.gov.pagopa.analytics.process.exception.custom.WorkflowNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

class ControllerExceptionHandlerTest extends CommonExceptionHandlerTest {

  @Test
  void handleWorkflowExecutionAlreadyStarted() throws Exception {
    doThrow(new WorkflowExecutionAlreadyStarted(mock(WorkflowExecution.class), null, null)).when(testControllerSpy).testEndpoint(DATA, BODY);

    performRequest(DATA, MediaType.APPLICATION_JSON)
      .andExpect(MockMvcResultMatchers.status().isConflict())
      .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("CONFLICT"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("CONFLICT"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("workflowId='null', runId='null'"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
  }

  @Test
  void handleWorkflowNotFoundException() throws Exception {
    doThrow(new WorkflowNotFoundException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

    performRequest(DATA, MediaType.APPLICATION_JSON)
      .andExpect(MockMvcResultMatchers.status().isNotFound())
      .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("NOT_FOUND"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("WORKFLOW_NOT_FOUND"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
      .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
  }

  @Test
  void handleTemporalWorkflowNotFoundException() throws Exception {
    WorkflowExecution workflowExecution = mock(WorkflowExecution.class);
    doThrow(new io.temporal.client.WorkflowNotFoundException(workflowExecution, null, null)).when(testControllerSpy).testEndpoint(DATA, BODY);

    performRequest(DATA, MediaType.APPLICATION_JSON)
      .andExpect(MockMvcResultMatchers.status().isNotFound())
      .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("NOT_FOUND"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("NOT_FOUND"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("workflowId='null', runId='null'"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
      .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
  }

  @Test
  void handleWFInternalError() throws Exception {
    doThrow(new WorkflowInternalErrorException("Error")).when(testControllerSpy).testEndpoint(DATA, BODY);

    performRequest(DATA, MediaType.APPLICATION_JSON)
      .andExpect(MockMvcResultMatchers.status().isInternalServerError())
      .andExpect(MockMvcResultMatchers.jsonPath("$.category").value("GENERIC_ERROR"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("WORKFLOW_INTERNAL_ERROR"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Error"))
      .andExpect(MockMvcResultMatchers.jsonPath("$.fields").doesNotExist())
      .andExpect(MockMvcResultMatchers.jsonPath("$.traceId").value(traceId));
  }

}
