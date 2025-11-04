package it.gov.pagopa.analytics.process.wf.assessments;

import it.gov.pagopa.analytics.process.dto.generated.WorkflowCreatedDTO;
import it.gov.pagopa.analytics.process.service.temporal.WorkflowClientService;
import it.gov.pagopa.analytics.process.service.temporal.WorkflowService;
import it.gov.pagopa.analytics.process.utils.TaskQueueConstants;
import it.gov.pagopa.analytics.process.utils.TemporalTestUtils;
import it.gov.pagopa.analytics.process.wf.assessments.wf.AssessmentsClassificationsProcessWF;
import it.gov.pagopa.analytics.process.wf.assessments.wf.AssessmentsClassificationsProcessWFImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssessmentsClassificationsProcessWFClientTest {

  @Mock
  private WorkflowService workflowServiceMock;
  @Mock
  private WorkflowClientService workflowClientServiceMock;
  @Mock
  private AssessmentsClassificationsProcessWF wfMock;

  private AssessmentsClassificationsProcessWFClient client;

  @BeforeEach
  void init() {
    client = new AssessmentsClassificationsProcessWFClient(workflowServiceMock, workflowClientServiceMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(workflowServiceMock, workflowClientServiceMock);
  }

  @Test
  void whenProcessAssessmentsClassificationsThenOk() {
    // Given
    String taskQueue = TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING;
    WorkflowCreatedDTO expectedResult = new WorkflowCreatedDTO("AssessmentsClassificationsProcessWF-ON-DEMAND", "RUNID");

    Mockito.when(workflowServiceMock.buildWorkflowStub(AssessmentsClassificationsProcessWF.class, taskQueue, expectedResult.getWorkflowId()))
      .thenReturn(wfMock);

    TemporalTestUtils.configureWorkflowClientServiceMock(workflowClientServiceMock, expectedResult);

    // When
    WorkflowCreatedDTO result = client.processAssessmentsClassifications();

    // Then
    Assertions.assertEquals(expectedResult, result);
    Mockito.verify(wfMock).synchronize();

    TemporalTestUtils.verifyWorkflowTaskQueueConfiguration(taskQueue, AssessmentsClassificationsProcessWFImpl.class);
  }
}
