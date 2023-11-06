package pro.sky.teamproject.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.teamproject.tgBot.model.Report;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findReportsByAnimalIdAndDatetimeAfter(Long animalId, LocalDateTime datetime);
}
