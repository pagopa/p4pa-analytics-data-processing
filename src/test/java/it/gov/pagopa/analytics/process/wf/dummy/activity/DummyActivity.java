package it.gov.pagopa.analytics.process.wf.dummy.activity;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface DummyActivity {

  @ActivityMethod
  String executeDummyActivity();
}
