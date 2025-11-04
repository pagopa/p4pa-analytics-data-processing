
package it.gov.pagopa.analytics.process.wf.assessments.wf;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;


/**
 * Workflow to build DataMart AssessmentsClassifications
 */
@WorkflowInterface
public interface AssessmentsClassificationsProcessWF {
  @WorkflowMethod
  String synchronize();
}

