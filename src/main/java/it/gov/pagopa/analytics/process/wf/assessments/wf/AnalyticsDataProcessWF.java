
package it.gov.pagopa.analytics.process.wf.assessments.wf;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;


/**
 * Workflow to build DataMart AnalyticsData
 */
@WorkflowInterface
public interface AnalyticsDataProcessWF {
  @WorkflowMethod
  String processAnalyticsData();
}

