package it.gov.pagopa.analytics.process.wf.assessments.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SampleActivity {

  @ActivityMethod
  String executeSampleActivity();
}
