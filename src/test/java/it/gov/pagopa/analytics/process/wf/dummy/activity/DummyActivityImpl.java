package it.gov.pagopa.analytics.process.wf.dummy.activity;

import io.temporal.spring.boot.ActivityImpl;
import it.gov.pagopa.analytics.process.utils.TaskQueueConstants;
import it.gov.pagopa.analytics.process.wf.dummy.service.DummyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ActivityImpl(taskQueues = TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING)
public class DummyActivityImpl implements DummyActivity {

  private final DummyService dummyService;

  public DummyActivityImpl(DummyService dummyService) {
    this.dummyService = dummyService;
  }

  @Override
  public String executeDummyActivity() {
    log.info("Executing DUMMY activity");
    return dummyService.doSomething();
  }
}
