package ewm.events.dto;

import ewm.locations.dto.LocationDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventCreateDto {
    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @Future
    private LocalDateTime eventDate;

    @NotNull
    @Valid
    private LocationDto location;

    private boolean paid;

    @PositiveOrZero
    private int participantLimit = 0;

    private boolean requestModeration = true;

    @Size(min = 3, max = 120)
    @NotBlank
    private String title;
}
