package pro.sky.teamproject.tgBot.service;

import pro.sky.teamproject.tgBot.model.Report;

import java.util.List;


public interface ReportService {
    Report addReport(Report report);
    Report findReportById(Long id);
    List<Report> findAllReports();
    Report updateReport(Report report);
    void deleteReport(Long id);

    Report validateReport(Long id, boolean isValid);
}
