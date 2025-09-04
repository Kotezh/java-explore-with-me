package ewm.locations.service;

import ewm.locations.Location;
import ewm.locations.dto.LocationDto;

public interface LocationService {
    Location getOrSave(LocationDto dto);
}
