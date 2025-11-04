package it.gov.pagopa.analytics.process.wf.assessments.config;

import io.temporal.workflow.Workflow;
import it.gov.pagopa.analytics.process.config.temporal.BaseWfConfig;
import it.gov.pagopa.analytics.process.config.temporal.TemporalWFImplementationCustomizer;
import it.gov.pagopa.analytics.process.wf.assessments.activity.SampleActivity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "workflow.assessments-classification-process")
public class AssessmentsClassificationsProcessWfConfig extends BaseWfConfig {

  public SampleActivity buildSampleActivityStub() {
    return Workflow.newActivityStub(SampleActivity.class, TemporalWFImplementationCustomizer.baseWfConfig2ActivityOptions(this));
  }
}
