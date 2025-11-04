package it.gov.pagopa.analytics.process.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleEnum {
  SCHEDULE_ASSESSMENTS_CLASSIFICATIONS_PROCESS_WF("AssessmentsClassificationsProcessSchedule");

  private final String value;
}

