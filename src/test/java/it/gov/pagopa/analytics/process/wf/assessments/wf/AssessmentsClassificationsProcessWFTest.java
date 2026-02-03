package it.gov.pagopa.analytics.process.wf.assessments.wf;

import it.gov.pagopa.analytics.process.wf.assessments.activity.AssessmentsClassificationProcessingActivity;
import it.gov.pagopa.analytics.process.wf.assessments.config.AssessmentsClassificationsProcessWfConfig;
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
class AssessmentsClassificationsProcessWFTest {

  @Mock
  private AssessmentsClassificationProcessingActivity assessmentsClassificationProcessingActivityMock;

  private AssessmentsClassificationsProcessWFImpl wf;

  @BeforeEach
  void setUp() {
    AssessmentsClassificationsProcessWfConfig assessmentsClassificationsProcessWfConfigMock = mock(AssessmentsClassificationsProcessWfConfig.class);
    ApplicationContext applicationContextMock = mock(ApplicationContext.class);
    when(assessmentsClassificationsProcessWfConfigMock.buildSampleActivityStub()).thenReturn(assessmentsClassificationProcessingActivityMock);

    when(applicationContextMock.getBean(AssessmentsClassificationsProcessWfConfig.class)).thenReturn(assessmentsClassificationsProcessWfConfigMock);

    wf = new AssessmentsClassificationsProcessWFImpl();
    wf.setApplicationContext(applicationContextMock);
  }

  @AfterEach
  void verifyNoMoreInteractions() {
    Mockito.verifyNoMoreInteractions(
      assessmentsClassificationProcessingActivityMock);
  }
  @Test
  void givenSuccessfulSyncWhenProcessAssessmentsClassificationsThenLogSynchronizedTaxonomies() {
    // Given
    String expectedResult = "OK";
    when(assessmentsClassificationProcessingActivityMock.executeAssessmentsClassificationProcessingActivity()).thenReturn(expectedResult);

    // When
    String result = wf.processAssessmentsClassifications();

    // Then
    Assertions.assertSame(expectedResult, result);
  }
}
