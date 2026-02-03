package it.gov.pagopa.analytics.process;

import com.uber.m3.tally.NoopScope;
import io.temporal.api.common.v1.WorkflowExecution;
import io.temporal.api.enums.v1.WorkflowExecutionStatus;
import io.temporal.api.workflow.v1.WorkflowExecutionInfo;
import io.temporal.client.WorkflowClient;
import io.temporal.internal.client.WorkflowClientHelper;
import it.gov.pagopa.analytics.process.dto.generated.WorkflowCreatedDTO;
import it.gov.pagopa.analytics.process.exception.NotRetryableActivityException;
import it.gov.pagopa.analytics.process.wf.assessments.AssessmentsClassificationsProcessScheduler;
import it.gov.pagopa.analytics.process.wf.dummy.DummyWFClient;
import it.gov.pagopa.analytics.process.wf.dummy.activity.DummyActivity;
import it.gov.pagopa.analytics.process.wf.dummy.service.DummyService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import java.util.Set;

import static org.mockito.Mockito.*;

@SpringBootTest(classes = {AnalyticsDataProcessingApplication.class})
@TestPropertySource(properties = {
  "spring.temporal.test-server.enabled: true",

  "workflow.dummy.retry-maximum-attempts: 3",
  "workflow.dummy.retry-initial-interval-in-millis: 100",
  "workflow.dummy.retry-backoff-coefficient: 1",
  "workflow.dummy.start-to-close-timeout-in-seconds: 100"
})
class TemporalSpringBootIntegrationTest {

  /** <a href="https://docs.temporal.io/workflows#status">Closed statuses</a> */
  private final Set<WorkflowExecutionStatus> wfTerminationStatuses = Set.of(
    WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED,
    WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_FAILED,
    WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_TERMINATED,
    WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_CANCELED
  );

  @Autowired
  private WorkflowClient temporalClient;
  @Value("${spring.temporal.namespace}")
  private String temporalNamespace;

  // disabling scheduling due to temporal test server not support
  @MockitoBean
  private AssessmentsClassificationsProcessScheduler assessmentsClassificationsProcessSchedulerMock;


  @MockitoSpyBean
  private DummyActivity dummyActivitySpy;
  // Using real UpdateIngestionFlowStatusActivityImpl which depends on the following
  @MockitoBean
  private DummyService dummyServiceMock;

  @Autowired
  private DummyWFClient workflowClient;

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(
      dummyServiceMock,
      dummyActivitySpy
    );
  }

  @Test
  void givenSuccessfulUseCaseWhenExecuteWfThenInvokeAllActivities() {
    WorkflowCreatedDTO wfExec = workflowClient.execute();

    waitUntilWfCompletion(wfExec);

    verify(dummyActivitySpy).executeDummyActivity();
    verify(dummyServiceMock).doSomething();
  }

  @Test
  void givenNotRetryableExceptionExtensionWhenExecuteWfThenStopExecutionWithoutRetries() {
    when(dummyServiceMock.doSomething())
      .thenThrow(new NotRetryableActivityException("extension"){});

    WorkflowCreatedDTO wfExec = workflowClient.execute();
    waitUntilWfFailed(wfExec);

    verify(dummyActivitySpy).executeDummyActivity();
    verify(dummyServiceMock).doSomething();
  }

  @Test
  void givenRetryableExceptionWhenExecuteWfThenRetrieActivityUntilMax() {
    when(dummyServiceMock.doSomething())
      .thenThrow(new RuntimeException("RetryableActivityException"));

    WorkflowCreatedDTO wfExec = workflowClient.execute();
    waitUntilWfFailed(wfExec);

    verify(dummyActivitySpy, times(3)).executeDummyActivity();
    verify(dummyServiceMock, times(3)).doSomething();
  }

  // PRIVATE METHODS
  private void waitUntilWfCompletion(WorkflowCreatedDTO wfExec) {
    waitUntilWfStatus(wfExec.getWorkflowId(), WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_COMPLETED);
  }

  private void waitUntilWfFailed(WorkflowCreatedDTO wfExec) {
    waitUntilWfStatus(wfExec.getWorkflowId(), WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_FAILED);
  }

  private void waitUntilWfStatus(String workflowId, WorkflowExecutionStatus status) {
    WorkflowExecutionInfo info;
    do {
      info = WorkflowClientHelper.describeWorkflowInstance(temporalClient.getWorkflowServiceStubs(), temporalNamespace, WorkflowExecution.newBuilder().setWorkflowId(workflowId).build(), new NoopScope());
    } while (!wfTerminationStatuses.contains(info.getStatus()));

    Assertions.assertEquals(status, info.getStatus());
  }
}


