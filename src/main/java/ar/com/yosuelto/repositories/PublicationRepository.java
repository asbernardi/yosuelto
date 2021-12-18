package ar.com.yosuelto.repositories;

import ar.com.yosuelto.model.Publication;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

    List<Publication> findByLocationCountry(String country, Sort sort);

    long countByEmail(String email);

}
