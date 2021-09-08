package ar.com.yosuelto.repositories;

import ar.com.yosuelto.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByQuery(String query);
}
