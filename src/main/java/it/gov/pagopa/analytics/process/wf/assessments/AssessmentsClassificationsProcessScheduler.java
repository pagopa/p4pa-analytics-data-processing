package it.gov.pagopa.analytics.process.wf.assessments;

import io.temporal.client.schedules.ScheduleHandle;
import it.gov.pagopa.analytics.process.enums.ScheduleEnum;
import it.gov.pagopa.analytics.process.service.temporal.WorkflowScheduleService;
import it.gov.pagopa.analytics.process.utils.TaskQueueConstants;
import it.gov.pagopa.analytics.process.wf.assessments.wf.AssessmentsClassificationsProcessWF;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Getter
public class AssessmentsClassificationsProcessScheduler {

  private final ScheduleHandle schedule;

  public AssessmentsClassificationsProcessScheduler(
    WorkflowScheduleService workflowScheduleService,
    @Value("${schedule.assessments-classifications-process.cron-expression}") String cronExpression
  ) {
    this.schedule = workflowScheduleService.schedule(
      ScheduleEnum.SCHEDULE_ASSESSMENTS_CLASSIFICATIONS_PROCESS_WF,
      AssessmentsClassificationsProcessWF.class,
      TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING,
      cronExpression);
  }

}
