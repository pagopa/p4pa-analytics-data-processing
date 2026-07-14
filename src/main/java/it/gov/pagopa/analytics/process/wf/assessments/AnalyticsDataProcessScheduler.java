package it.gov.pagopa.analytics.process.wf.assessments;

import io.temporal.client.schedules.ScheduleHandle;
import it.gov.pagopa.analytics.process.enums.ScheduleEnum;
import it.gov.pagopa.analytics.process.service.temporal.WorkflowScheduleService;
import it.gov.pagopa.analytics.process.utils.TaskQueueConstants;
import it.gov.pagopa.analytics.process.wf.assessments.wf.AnalyticsDataProcessWF;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
public class AnalyticsDataProcessScheduler {

  private final ScheduleHandle schedule;

  public AnalyticsDataProcessScheduler(
    WorkflowScheduleService workflowScheduleService,
    @Value("${schedule.analytics-data-process.cron-expression}") String cronExpression
  ) {
    this.schedule = workflowScheduleService.schedule(
      ScheduleEnum.SCHEDULE_ANALYTICS_DATA_PROCESS_WF,
      AnalyticsDataProcessWF.class,
      TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING,
      cronExpression);
  }

}
