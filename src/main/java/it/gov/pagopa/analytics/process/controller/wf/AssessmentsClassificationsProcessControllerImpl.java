package it.gov.pagopa.analytics.process.controller.wf;

import it.gov.pagopa.analytics.process.controller.generated.AssessmentsClassificationsProcessApi;
import it.gov.pagopa.analytics.process.dto.generated.WorkflowCreatedDTO;
import it.gov.pagopa.analytics.process.wf.assessments.AssessmentsClassificationsProcessWFClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AssessmentsClassificationsProcessControllerImpl implements AssessmentsClassificationsProcessApi {

  private final AssessmentsClassificationsProcessWFClient wfClient;

  public AssessmentsClassificationsProcessControllerImpl(AssessmentsClassificationsProcessWFClient wfClient) {
    this.wfClient = wfClient;
  }

  @Override
  public ResponseEntity<WorkflowCreatedDTO> processAssessmentsClassifications() {
    log.info("Requested execution of AssessmentsClassificationsProcessWF workflow");
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(wfClient.processAssessmentsClassifications());
  }
}
