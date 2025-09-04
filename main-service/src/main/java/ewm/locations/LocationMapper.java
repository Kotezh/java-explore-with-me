package ewm.locations;

import ewm.locations.dto.LocationDto;
import org.springframework.stereotype.Component;

@Component
public class LocationMapper {

    public Location mapLocationDtoToModel(LocationDto dto) {
        return Location.builder()
                .lat(dto.getLat())
                .lon(dto.getLon())
                .build();
    }

    public LocationDto mapModelToDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

}