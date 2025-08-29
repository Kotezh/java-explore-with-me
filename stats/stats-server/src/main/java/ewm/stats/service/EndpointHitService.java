package ewm.stats.service;

import dto.HitDto;
import dto.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitService {
    void hit(HitDto hitDto);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
