package it.gov.pagopa.analytics.process.wf.assessments;

import io.temporal.client.schedules.ScheduleHandle;
import it.gov.pagopa.analytics.process.enums.ScheduleEnum;
import it.gov.pagopa.analytics.process.service.temporal.WorkflowScheduleService;
import it.gov.pagopa.analytics.process.utils.TaskQueueConstants;
import it.gov.pagopa.analytics.process.utils.TemporalTestUtils;
import it.gov.pagopa.analytics.process.wf.assessments.wf.AnalyticsDataProcessWF;
import it.gov.pagopa.analytics.process.wf.assessments.wf.AnalyticsDataProcessWFImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsDataProcessSchedulerTest {

  @Mock
  private WorkflowScheduleService workflowScheduleServiceMock;

  @AfterEach
  void verifyNoMoreInteractions(){
    Mockito.verifyNoMoreInteractions(workflowScheduleServiceMock);
  }

  @Test
  void givenServiceCreationThenInvokeSchedule(){
    // Given
    String cronExpression = "cron";

    ScheduleHandle expectedResult = Mockito.mock(ScheduleHandle.class);
    String taskQueue = TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING;
    Mockito.when(workflowScheduleServiceMock.schedule(
        ScheduleEnum.SCHEDULE_ANALYTICS_DATA_PROCESS_WF,
        AnalyticsDataProcessWF.class,
        taskQueue,
        cronExpression
      ))
      .thenReturn(expectedResult);

    // When
    AnalyticsDataProcessScheduler scheduler = new AnalyticsDataProcessScheduler(workflowScheduleServiceMock, cronExpression);
    ScheduleHandle result = scheduler.getSchedule();

    // Then
    Assertions.assertSame(expectedResult, result);

    TemporalTestUtils.verifyWorkflowTaskQueueConfiguration(taskQueue, AnalyticsDataProcessWFImpl.class);
  }
}
