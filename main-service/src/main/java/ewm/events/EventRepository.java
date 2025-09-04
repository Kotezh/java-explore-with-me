package ewm.events;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Page<Event> findAll(Specification<Event> specification, Pageable pageable);

    Set<Event> findAllByIdIn(List<Long> events);

    @Query("SELECT e FROM Event e WHERE e.initiator.id IN :initiatorIds")
    List<Event> findAllByInitiatorIdIn(List<Long> initiatorIds);
}
