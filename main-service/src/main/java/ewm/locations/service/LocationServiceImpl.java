package ewm.locations.service;

import ewm.locations.Location;
import ewm.locations.LocationMapper;
import ewm.locations.LocationRepository;
import ewm.locations.dto.LocationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Transactional
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationMapper mapper;
    private final LocationRepository repository;

    @Override
    public Location getOrSave(LocationDto dto) {
        Location location = repository.findByLatAndLon(dto.getLat(), dto.getLon());
        return Objects.requireNonNullElseGet(location, () -> repository.save(mapper.mapLocationDtoToModel(dto)));
    }
}
