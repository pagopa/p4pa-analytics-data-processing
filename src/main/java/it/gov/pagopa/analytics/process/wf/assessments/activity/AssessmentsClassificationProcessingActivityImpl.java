package it.gov.pagopa.analytics.process.wf.assessments.activity;

import io.temporal.spring.boot.ActivityImpl;
import it.gov.pagopa.analytics.process.service.CommandExecutor;
import it.gov.pagopa.analytics.process.utils.TaskQueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ActivityImpl(taskQueues = TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING)
public class AssessmentsClassificationProcessingActivityImpl implements AssessmentsClassificationProcessingActivity {

  private final CommandExecutor commandExecutor;

  public AssessmentsClassificationProcessingActivityImpl(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }

  @Override
  public String executeAssessmentsClassificationProcessingActivity() {
    log.info("Executing AssessmentsClassificationProcessing activity");
    return "Command executed with exitStatus:" + commandExecutor.executeCommandActivity(new String[]{"dbt", "run"});
  }
}
