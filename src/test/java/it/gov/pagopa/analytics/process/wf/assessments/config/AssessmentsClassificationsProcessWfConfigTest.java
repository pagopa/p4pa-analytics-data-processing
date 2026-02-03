package it.gov.pagopa.analytics.process.wf.assessments.config;

import it.gov.pagopa.analytics.process.utils.TemporalTestUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

class AssessmentsClassificationsProcessWfConfigTest {

  private final AssessmentsClassificationsProcessWfConfig config = new AssessmentsClassificationsProcessWfConfig();

  private final Map<Class<?>, Class<?>> localActivityInterface2Impl = Map.of();

  @Test
  void testTaskQueueAlignment() throws InvocationTargetException, IllegalAccessException {
    TemporalTestUtils.verifyActivityStubConfiguration(config, localActivityInterface2Impl);
  }
}
