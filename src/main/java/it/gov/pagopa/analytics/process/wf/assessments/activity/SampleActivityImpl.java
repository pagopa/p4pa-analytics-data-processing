package it.gov.pagopa.analytics.process.wf.assessments.activity;

import io.temporal.spring.boot.ActivityImpl;
import it.gov.pagopa.analytics.process.service.CommandExecutor;
import it.gov.pagopa.analytics.process.utils.TaskQueueConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ActivityImpl(taskQueues = TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING)
public class SampleActivityImpl implements SampleActivity {

  private final CommandExecutor commandExecutor;

  public SampleActivityImpl(CommandExecutor commandExecutor) {
    this.commandExecutor = commandExecutor;
  }

  @Override
  public String executeSampleActivity() {
    log.info("Executing SAMPLE activity");
    return "Command executed with exitStatus:" + commandExecutor.executeSampleActivity(new String[]{"echo", "Hello World!"});
  }
}
