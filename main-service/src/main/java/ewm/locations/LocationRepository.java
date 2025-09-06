package ewm.locations;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Long> {

    Location findByLatAndLon(Float lat, Float lon);

}
