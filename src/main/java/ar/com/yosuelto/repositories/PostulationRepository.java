package ar.com.yosuelto.repositories;

import ar.com.yosuelto.model.Postulation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostulationRepository extends JpaRepository<Postulation, Long> {
}
