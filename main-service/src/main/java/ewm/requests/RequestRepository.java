package ewm.requests;

import ewm.requests.dto.ConfirmedRequestsDto;
import ewm.requests.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Request findByIdAndRequesterId(Long requestId, Long userId);

    List<Request> findAllByEventId(Long eventId);

    List<Request> findAllByRequesterId(Long userId);

    List<Request> findAllByEventIdAndIdInAndStatus(Long eventId, List<Long> requestId, RequestStatus status);

    Boolean existsByRequesterIdAndEventId(Long userId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, RequestStatus status);

    @Query("SELECT new ewm.requests.dto.ConfirmedRequestsDto(COUNT(DISTINCT r.id), r.event.id) " +
            "FROM Request AS r " +
            "WHERE r.event.id IN (:ids) AND r.status = :status " +
            "GROUP BY (r.event)")
    List<ConfirmedRequestsDto> findAllByEventIdInAndStatus(@Param("ids") List<Long> ids, @Param("status") RequestStatus status);
}