package it.gov.pagopa.analytics.process.wf.assessments.config;

import it.gov.pagopa.analytics.process.utils.TemporalTestUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

class AnalyticsDataProcessWfConfigTest {

  private final AnalyticsDataProcessWfConfig config = new AnalyticsDataProcessWfConfig();

  private final Map<Class<?>, Class<?>> localActivityInterface2Impl = Map.of();

  @Test
  void testTaskQueueAlignment() throws InvocationTargetException, IllegalAccessException {
    TemporalTestUtils.verifyActivityStubConfiguration(config, localActivityInterface2Impl);
  }
}
