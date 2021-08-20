package ar.com.yosuelto.repositories;

import ar.com.yosuelto.model.Publication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, Long> {

    long countByEmail(String email);

}
