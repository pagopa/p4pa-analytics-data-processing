package it.gov.pagopa.analytics.process.wf.assessments;

import it.gov.pagopa.analytics.process.dto.generated.WorkflowCreatedDTO;
import it.gov.pagopa.analytics.process.service.temporal.WorkflowClientService;
import it.gov.pagopa.analytics.process.service.temporal.WorkflowScheduleServiceImpl;
import it.gov.pagopa.analytics.process.service.temporal.WorkflowService;
import it.gov.pagopa.analytics.process.utils.TaskQueueConstants;
import it.gov.pagopa.analytics.process.wf.assessments.wf.AssessmentsClassificationsProcessWF;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static it.gov.pagopa.analytics.process.utils.Utilities.generateWorkflowId;

@Slf4j
@Service
public class AssessmentsClassificationsProcessWFClient {

  private final WorkflowService workflowService;
  private final WorkflowClientService workflowClientService;

  public AssessmentsClassificationsProcessWFClient(WorkflowService workflowService, WorkflowClientService workflowClientService) {
    this.workflowService = workflowService;
    this.workflowClientService = workflowClientService;
  }

  public WorkflowCreatedDTO processAssessmentsClassifications() {
    log.info("Starting on-demand synchronizeTaxonomy");
    String taskQueue = TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING;
    String workflowId = generateWorkflowId(WorkflowScheduleServiceImpl.ON_DEMAND_SCHEDULE_SUFFIX, AssessmentsClassificationsProcessWF.class);

    AssessmentsClassificationsProcessWF workflow = workflowService.buildWorkflowStubToStartNew(
      AssessmentsClassificationsProcessWF.class,
      taskQueue,
      workflowId);

    return workflowClientService.start(workflow::processAssessmentsClassifications);
  }
}
