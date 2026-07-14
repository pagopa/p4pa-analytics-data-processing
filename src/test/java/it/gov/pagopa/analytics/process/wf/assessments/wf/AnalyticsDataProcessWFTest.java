package it.gov.pagopa.analytics.process.wf.assessments.wf;

import it.gov.pagopa.analytics.process.wf.assessments.activity.AnalyticsDataProcessingActivity;
import it.gov.pagopa.analytics.process.wf.assessments.config.AnalyticsDataProcessWfConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsDataProcessWFTest {

  @Mock
  private AnalyticsDataProcessingActivity analyticsDataProcessingActivityMock;

  private AnalyticsDataProcessWFImpl wf;

  @BeforeEach
  void setUp() {
    AnalyticsDataProcessWfConfig analyticsDataProcessWfConfigMock = mock(AnalyticsDataProcessWfConfig.class);
    ApplicationContext applicationContextMock = mock(ApplicationContext.class);
    when(analyticsDataProcessWfConfigMock.buildSampleActivityStub()).thenReturn(analyticsDataProcessingActivityMock);

    when(applicationContextMock.getBean(AnalyticsDataProcessWfConfig.class)).thenReturn(analyticsDataProcessWfConfigMock);

    wf = new AnalyticsDataProcessWFImpl();
    wf.setApplicationContext(applicationContextMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      analyticsDataProcessingActivityMock);
  }
  @Test
  void givenSuccessfulSyncWhenProcessAnalyticsDataThenLogSynchronizedTaxonomies() {
    // Given
    String expectedResult = "OK";
    when(analyticsDataProcessingActivityMock.executeAnalyticsDataProcessingActivity()).thenReturn(expectedResult);

    // When
    String result = wf.processAnalyticsData();

    // Then
    Assertions.assertSame(expectedResult, result);
  }
}
