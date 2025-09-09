package ewm.stats.service;

import dto.HitDto;
import dto.StatsDto;
import ewm.stats.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface EndpointHitService {
    EndpointHit hit(HitDto hitDto);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
