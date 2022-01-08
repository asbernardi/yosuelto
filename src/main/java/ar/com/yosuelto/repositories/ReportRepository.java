package ar.com.yosuelto.repositories;

import ar.com.yosuelto.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

}
