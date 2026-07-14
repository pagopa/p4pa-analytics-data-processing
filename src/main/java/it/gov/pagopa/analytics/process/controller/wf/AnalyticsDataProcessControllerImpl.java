package it.gov.pagopa.analytics.process.controller.wf;

import it.gov.pagopa.analytics.process.controller.generated.AnalyticsDataProcessApi;
import it.gov.pagopa.analytics.process.dto.generated.WorkflowCreatedDTO;
import it.gov.pagopa.analytics.process.wf.assessments.AnalyticsDataProcessWFClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class AnalyticsDataProcessControllerImpl implements AnalyticsDataProcessApi {

  private final AnalyticsDataProcessWFClient wfClient;

  public AnalyticsDataProcessControllerImpl(AnalyticsDataProcessWFClient wfClient) {
    this.wfClient = wfClient;
  }

  @Override
  public ResponseEntity<WorkflowCreatedDTO> processAnalyticsData() {
    log.info("Requested execution of AnalyticsDataProcessWF workflow");
    return ResponseEntity.status(HttpStatus.CREATED)
      .body(wfClient.processAnalyticsData());
  }
}
