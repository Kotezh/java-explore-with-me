package ewm.events.dto;

import ewm.categories.dto.CategoryDto;
import ewm.events.enums.State;
import ewm.locations.dto.LocationDto;
import ewm.user.dto.UserShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventViewsDto {
    private Long id;

    private String title;

    private String annotation;

    private CategoryDto category;

    private boolean paid;

    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Long views;

    private Long confirmedRequests;

    private String description;

    private Integer participantLimit;

    private State state;

    private LocalDateTime createdOn;

    private LocalDateTime publishedOn;

    private LocationDto location;

    private boolean requestModeration;
}
