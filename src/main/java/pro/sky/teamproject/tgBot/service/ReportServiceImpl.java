package pro.sky.teamproject.tgBot.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pro.sky.teamproject.tgBot.model.Report;
import pro.sky.teamproject.tgBot.repository.ReportRepository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for Report domain
 *
 * @author Artem beshik7
 */
@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository repository;

    @Override
    public Report addReport(Report report) {
        report.setDatetime(Timestamp.valueOf(LocalDateTime.now()));
        return repository.save(report);
    }
    @Override
    public List<Report> findAllReports() {
        return repository.findAll();
    }
    @Override
    public Report findReportById(Long id) {
        return repository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Report not found [reportId=%s]", id)));
    }
    @Override
    public Report updateReport(Report report) {
        Report foundReport = findReportById(report.getId());

        foundReport.setUser(report.getUser());
        foundReport.setPhoto(report.getPhoto());
        foundReport.setInfo(report.getInfo());
        foundReport.setReportValid(report.getReportValid());

        return repository.save(foundReport);
    }
    @Override
    public void deleteReport(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new EntityNotFoundException(String.format("Report not found [Id=%s]", id));
        }
        repository.deleteById(id);
    }
    @Override
    public Report validateReport(Long id, boolean isValid) {
        Report report = findReportById(id);
        report.setReportValid(isValid);
        return repository.save(report);
    }
}
