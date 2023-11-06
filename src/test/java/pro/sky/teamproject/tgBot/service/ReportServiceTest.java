package pro.sky.teamproject.tgBot.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import jakarta.persistence.EntityNotFoundException;
import pro.sky.teamproject.tgBot.model.Report;
import pro.sky.teamproject.tgBot.repository.ReportRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ReportServiceTest {
    @InjectMocks
    private ReportServiceImpl reportService;
    @Mock
    private Report report;
    private final Long reportId = 1L;

    @Mock
    private ReportRepository reportRepository;



    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

    }

    @Test
    void addReportTest() {
        Report report = new Report();
        when(reportRepository.save(report)).thenReturn(report);

        Report savedReport = reportService.addReport(report);

        assertNotNull(savedReport.getDatetime());
        verify(reportRepository).save(report);
    }

    @Test
    void findAllReportsTest() {

        when(reportService.findAllReports()).thenReturn(List.of(report));
        List<Report> reports = reportService.findAllReports();
        assertFalse(reports.isEmpty());
        assertEquals(1, reports.size());

        verify(reportRepository, times(1)).findAll();
    }

    @Test
    void findReportByIdTest() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        Report foundReport = reportService.findReportById(reportId);

        assertNotNull(foundReport);
        assertEquals(reportId, foundReport.getId());
        verify(reportRepository).findById(reportId);
    }

    @Test
    void findReportByIdNotFoundTest() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            reportService.findReportById(reportId);
        });

        assertNotNull(thrown);
        verify(reportRepository).findById(reportId);
    }

    @Test
    void updateReportTest() {
        Report updatedReport = new Report();
        updatedReport.setId(reportId);
        updatedReport.setInfo("Updated info");

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportRepository.save(report)).thenReturn(updatedReport);

        Report result = reportService.updateReport(updatedReport);

        assertEquals("Updated info", result.getInfo());
        verify(reportRepository).save(report);
    }

    @Test
    void deleteReportTest() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        reportService.deleteReport(reportId);

        verify(reportRepository).deleteById(reportId);
    }

    @Test
    void deleteReportNotFoundTest() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class, () -> {
            reportService.deleteReport(reportId);
        });

        assertNotNull(thrown);
        verify(reportRepository).findById(reportId);
    }

    @Test
    void validateReportTest() {
        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(reportRepository.save(any(Report.class))).thenReturn(report);

        Report result = reportService.validateReport(reportId, true);

        assertTrue(result.getIsReportValid());
        verify(reportRepository).save(report);
    }
}
