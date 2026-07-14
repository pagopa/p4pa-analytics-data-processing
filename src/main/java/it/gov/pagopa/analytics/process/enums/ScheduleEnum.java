package it.gov.pagopa.analytics.process.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleEnum {
  SCHEDULE_ANALYTICS_DATA_PROCESS_WF("AnalyticsDataProcessSchedule");

  private final String value;
}

