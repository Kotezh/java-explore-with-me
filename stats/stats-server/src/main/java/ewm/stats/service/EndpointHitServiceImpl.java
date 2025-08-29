package ewm.stats.service;

import dto.HitDto;
import dto.StatsDto;
import ewm.exception.BadRequestException;
import ewm.stats.EndpointHit;
import ewm.stats.EndpointHitMapper;
import ewm.stats.EndpointHitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitMapper mapper;
    private final EndpointHitRepository repository;

    @Override
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (start.isAfter(end)) {
            throw new BadRequestException("Некорректное время");
        }
        if (unique) {
            if (uris != null) {
                return repository.getUniqueStatsWithUris(uris, start, end);
            }
            return repository.getUniqueStats(start, end);
        } else {
            if (uris != null) {
                return repository.getStatsWithUris(uris, start, end);
            }
            return repository.getStats(start, end);
        }
    }

    @Override
    public void hit(HitDto hitDto) {
        EndpointHit endpointHit = mapper.mapDtoToModel(hitDto);
        repository.save(endpointHit);
    }
}
