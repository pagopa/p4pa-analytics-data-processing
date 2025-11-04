
package it.gov.pagopa.analytics.process.wf.dummy.wf;

import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;


/**
 * Workflow to build DataMart AssessmentsClassifications
 */
@WorkflowInterface
public interface DummyWF {
  @WorkflowMethod
  String process();
}

