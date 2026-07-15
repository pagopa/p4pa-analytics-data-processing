package it.gov.pagopa.analytics.process.wf.assessments;

import it.gov.pagopa.analytics.process.dto.generated.WorkflowCreatedDTO;
import it.gov.pagopa.analytics.process.service.temporal.WorkflowClientService;
import it.gov.pagopa.analytics.process.service.temporal.WorkflowService;
import it.gov.pagopa.analytics.process.utils.TaskQueueConstants;
import it.gov.pagopa.analytics.process.utils.TemporalTestUtils;
import it.gov.pagopa.analytics.process.wf.assessments.wf.AnalyticsDataProcessWF;
import it.gov.pagopa.analytics.process.wf.assessments.wf.AnalyticsDataProcessWFImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsDataProcessWFClientTest {

  @Mock
  private WorkflowService workflowServiceMock;
  @Mock
  private WorkflowClientService workflowClientServiceMock;
  @Mock
  private AnalyticsDataProcessWF wfMock;

  private AnalyticsDataProcessWFClient client;

  @BeforeEach
  void init() {
    client = new AnalyticsDataProcessWFClient(workflowServiceMock, workflowClientServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(workflowServiceMock, workflowClientServiceMock);
  }

  @Test
  void whenProcessAnalyticsDataThenOk() {
    // Given
    String taskQueue = TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING;
    WorkflowCreatedDTO expectedResult = new WorkflowCreatedDTO("AnalyticsDataProcessWF-ON-DEMAND", "RUNID");

    Mockito.when(workflowServiceMock.buildWorkflowStubToStartNew(AnalyticsDataProcessWF.class, taskQueue, expectedResult.getWorkflowId()))
      .thenReturn(wfMock);

    TemporalTestUtils.configureWorkflowClientServiceMock(workflowClientServiceMock, expectedResult);

    // When
    WorkflowCreatedDTO result = client.processAnalyticsData();

    // Then
    Assertions.assertEquals(expectedResult, result);
    Mockito.verify(wfMock).processAnalyticsData();

    TemporalTestUtils.verifyWorkflowTaskQueueConfiguration(taskQueue, AnalyticsDataProcessWFImpl.class);
  }
}
