package it.gov.pagopa.analytics.process.controller.wf;

import io.micrometer.tracing.Tracer;
import it.gov.pagopa.analytics.process.dto.generated.WorkflowCreatedDTO;
import it.gov.pagopa.analytics.process.wf.assessments.AnalyticsDataProcessWFClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsDataProcessControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
class AnalyticsDataProcessControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private JsonMapper jsonMapper;

  @MockitoBean
  private AnalyticsDataProcessWFClient wfClientMock;
  @MockitoBean
  private Tracer tracerMock;

  @Test
  void whenProcessAnalyticsDataThenOk() throws Exception {
    String workflowId = "workflow-1";
    String runId = "runId";
    WorkflowCreatedDTO expected = WorkflowCreatedDTO.builder()
      .workflowId(workflowId)
      .runId(runId)
      .build();

    when(wfClientMock.processAnalyticsData())
      .thenReturn(expected);

    MvcResult result = mockMvc.perform(
        post("/workflow/analytics-data-process"))
      .andExpect(status().isCreated())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    WorkflowCreatedDTO resultResponse =
      jsonMapper.readValue(result.getResponse().getContentAsString(), WorkflowCreatedDTO.class);
    assertEquals(expected, resultResponse);
  }
}
