package ar.com.yosuelto.repositories;

import ar.com.yosuelto.model.Publication;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

    List<Publication> findByReportsLessThan(int reports);

    List<Publication> findByReportsLessThan(int reports, Sort sort);

    List<Publication> findByLocationCountryAndReportsLessThan(String country, int report, Sort sort);

    long countByEmail(String email);

}
