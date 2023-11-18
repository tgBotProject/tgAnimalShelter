package pro.sky.teamproject.tgBot.controller;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pro.sky.teamproject.tgBot.model.Report;
import pro.sky.teamproject.tgBot.service.ReportServiceImpl;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
@WebMvcTest(ReportController.class)
public class ReportControllerMockMvcTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportServiceImpl reportService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    @Test
    void testGetAllReports() throws Exception {
        Report report = createReport();
        List<Report> reports = Collections.singletonList(report);
        given(reportService.findAllReports()).willReturn(reports);

        mockMvc.perform(get("/report"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reports)));

        verify(reportService).findAllReports();
    }
    @Test
    void testGetReportById() throws Exception {
        Report report = createReport();
        given(reportService.findReportById(report.getId())).willReturn(report);

        mockMvc.perform(get("/report/{id}", report.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(report)));

        verify(reportService).findReportById(report.getId());
    }
    @Test
    void testAddReport() throws Exception {
        Report report = createReport();
        given(reportService.addReport(any(Report.class))).willReturn(report);

        mockMvc.perform(post("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(report)));

        verify(reportService).addReport(any(Report.class));
    }
    @Test
    void testUpdateReport() throws Exception {
        Report report = createReport();
        given(reportService.updateReport(any(Report.class))).willReturn(report);

        mockMvc.perform(put("/report")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(report)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(report)));

        verify(reportService).updateReport(any(Report.class));
    }
    @Test
    void testDeleteReport() throws Exception {
        Long reportId = 1L;
        doNothing().when(reportService).deleteReport(reportId);

        mockMvc.perform(delete("/report/{id}", reportId))
                .andExpect(status().isOk());

        verify(reportService).deleteReport(reportId);
    }

    private Report createReport() {
        Report report = new Report();
        report.setId(1L);
        report.setInfo("Test report info");
        report.setPhoto("http://example.com/photo.jpg");
        report.setDatetime(new Timestamp(System.currentTimeMillis()));
        report.setIsReportValid(true);
        return report;
    }
}