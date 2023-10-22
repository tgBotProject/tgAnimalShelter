package pro.sky.teamproject.tgBot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pro.sky.teamproject.tgBot.model.Report;

public interface ReportRepository extends JpaRepository<Report, Long> {

}
