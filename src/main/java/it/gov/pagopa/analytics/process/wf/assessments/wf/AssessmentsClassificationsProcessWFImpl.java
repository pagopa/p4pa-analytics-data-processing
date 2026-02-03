
package it.gov.pagopa.analytics.process.wf.assessments.wf;

import io.temporal.spring.boot.WorkflowImpl;
import it.gov.pagopa.analytics.process.config.temporal.TemporalWFImplementationCustomizer;
import it.gov.pagopa.analytics.process.utils.TaskQueueConstants;
import it.gov.pagopa.analytics.process.wf.assessments.activity.AssessmentsClassificationProcessingActivity;
import it.gov.pagopa.analytics.process.wf.assessments.config.AssessmentsClassificationsProcessWfConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

@Slf4j
@WorkflowImpl(taskQueues = TaskQueueConstants.TASK_QUEUE_DATA_PROCESSING)
public class AssessmentsClassificationsProcessWFImpl implements AssessmentsClassificationsProcessWF, ApplicationContextAware {

  private AssessmentsClassificationProcessingActivity assessmentsClassificationProcessingActivity;


  /**
   * Temporal workflow will not allow to use injection in order to avoid <a href="https://docs.temporal.io/workflows#non-deterministic-change">non-deterministic changes</a> due to dynamic reconfiguration.<BR />
   * Anyway it allows to override ActivityOptions, but actually it's not supporting the override based on the particular workflow.<BR />
   * In {@link TemporalWFImplementationCustomizer} we are already setting defaults to all workflows.<BR />
   * Use this as an example to override based on the particular workflow.
   */

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    AssessmentsClassificationsProcessWfConfig wfConfig = applicationContext.getBean(AssessmentsClassificationsProcessWfConfig.class);
    assessmentsClassificationProcessingActivity = wfConfig.buildSampleActivityStub();

  }

  @Override
  public String processAssessmentsClassifications() {
    log.info("Executing AssessmentsClassifications DataMart process WF");
    String result = assessmentsClassificationProcessingActivity.executeAssessmentsClassificationProcessingActivity();
    log.info("AssessmentsClassifications process WF completed: {}", result);
    return result;
  }
}

