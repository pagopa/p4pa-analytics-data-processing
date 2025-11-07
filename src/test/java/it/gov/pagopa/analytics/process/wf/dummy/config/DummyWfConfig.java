package it.gov.pagopa.analytics.process.wf.dummy.config;

import io.temporal.workflow.Workflow;
import it.gov.pagopa.analytics.process.config.temporal.BaseWfConfig;
import it.gov.pagopa.analytics.process.config.temporal.TemporalWFImplementationCustomizer;
import it.gov.pagopa.analytics.process.wf.dummy.activity.DummyActivity;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "workflow.dummy")
public class DummyWfConfig extends BaseWfConfig {

  public DummyActivity buildDummyActivityStub() {
    return Workflow.newActivityStub(DummyActivity.class, TemporalWFImplementationCustomizer.baseWfConfig2ActivityOptions(this));
  }
}
