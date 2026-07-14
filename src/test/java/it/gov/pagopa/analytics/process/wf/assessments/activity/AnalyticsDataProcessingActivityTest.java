package it.gov.pagopa.analytics.process.wf.assessments.activity;

import it.gov.pagopa.analytics.process.service.CommandExecutor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsDataProcessingActivityTest {

  @Mock
  private CommandExecutor commandExecutor;

  private AnalyticsDataProcessingActivity activity;

  @BeforeEach
  void init() {
    activity = new AnalyticsDataProcessingActivityImpl(commandExecutor);
  }

  @Test
  void whenExecuteAssessmentsClassificationProcessingActivityThenReturnExpectedString() {
    final int mockExitStatus = 0;
    when(commandExecutor.executeCommandActivity(any(String[].class)))
      .thenReturn(mockExitStatus);

    // When
    activity.executeAnalyticsDataProcessingActivity();

    // Then
    assertDoesNotThrow(() -> {
      activity.executeAnalyticsDataProcessingActivity();
    });
  }
}
