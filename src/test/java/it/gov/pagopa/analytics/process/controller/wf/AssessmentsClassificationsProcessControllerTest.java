package it.gov.pagopa.analytics.process.controller.wf;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.gov.pagopa.analytics.process.dto.generated.WorkflowCreatedDTO;
import it.gov.pagopa.analytics.process.wf.assessments.AssessmentsClassificationsProcessWFClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AssessmentsClassificationsProcessControllerImpl.class)
@AutoConfigureMockMvc(addFilters = false)
class AssessmentsClassificationsProcessControllerTest {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private AssessmentsClassificationsProcessWFClient wfClientMock;

  @Test
  void whenProcessAssessmentsClassificationsThenOk() throws Exception {
    String workflowId = "workflow-1";
    String runId = "runId";
    WorkflowCreatedDTO expected = WorkflowCreatedDTO.builder()
      .workflowId(workflowId)
      .runId(runId)
      .build();

    Mockito.when(wfClientMock.processAssessmentsClassifications())
      .thenReturn(expected);

    MvcResult result = mockMvc.perform(
        post("/workflow/assessments-classifications-process"))
      .andExpect(status().isCreated())
      .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
      .andReturn();

    WorkflowCreatedDTO resultResponse =
      objectMapper.readValue(result.getResponse().getContentAsString(), WorkflowCreatedDTO.class);
    assertEquals(expected, resultResponse);
  }
}
