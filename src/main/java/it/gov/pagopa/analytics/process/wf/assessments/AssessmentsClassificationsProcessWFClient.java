package it.gov.pagopa.analytics.process.wf.assessments;

import it.gov.pagopa.analytics.process.dto.generated.WorkflowCreatedDTO;
import it.gov.pagopa.analytics.process.service.temporal.WorkflowClientService;
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
  private static final String ON_DEMAND = "ON-DEMAND";

  public AssessmentsClassificationsProcessWFClient(WorkflowService workflowService, WorkflowClientService workflowClientService) {
    this.workflowService = workflowService;
    this.workflowClientService = workflowClientService;
  }

  public WorkflowCreatedDTO processAssessmentsClassifications() {
    log.info("Starting synchronizeTaxonomy {}", ON_DEMAND);
    String taskQueue = TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING;
    String workflowId = generateWorkflowId(ON_DEMAND, AssessmentsClassificationsProcessWF.class);

    AssessmentsClassificationsProcessWF workflow = workflowService.buildWorkflowStub(
      AssessmentsClassificationsProcessWF.class,
      taskQueue,
      workflowId);

    return workflowClientService.start(workflow::synchronize);
  }
}
