package it.gov.pagopa.analytics.process.wf.assessments.activity;

import io.temporal.spring.boot.ActivityImpl;
import it.gov.pagopa.analytics.process.service.CommandExecutor;
import it.gov.pagopa.analytics.process.utils.TaskQueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ActivityImpl(taskQueues = TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING)
public class AnalyticsDataProcessingActivityImpl implements AnalyticsDataProcessingActivity {

  private final CommandExecutor commandExecutor;

  public AnalyticsDataProcessingActivityImpl(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }

  @Override
  public String executeAnalyticsDataProcessingActivity() {
    log.info("Executing AnalyticsDataProcessing activity");
    String[] dbtCommand = new String[]{"/app/script/dbt_execute.sh"};
    return "Command executed with exitStatus:" + commandExecutor.executeCommandActivity(dbtCommand);
  }
}
