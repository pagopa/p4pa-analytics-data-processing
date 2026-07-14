package it.gov.pagopa.analytics.process.wf.assessments.config;

import io.temporal.workflow.Workflow;
import it.gov.pagopa.analytics.process.config.temporal.BaseWfConfig;
import it.gov.pagopa.analytics.process.config.temporal.TemporalWFImplementationCustomizer;
import it.gov.pagopa.analytics.process.wf.assessments.activity.AnalyticsDataProcessingActivity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "workflow.analytics-data-process")
public class AnalyticsDataProcessWfConfig extends BaseWfConfig {

  public AnalyticsDataProcessingActivity buildSampleActivityStub() {
    return Workflow.newActivityStub(AnalyticsDataProcessingActivity.class, TemporalWFImplementationCustomizer.baseWfConfig2ActivityOptions(this));
  }
}
