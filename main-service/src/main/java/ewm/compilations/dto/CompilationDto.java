package ewm.compilations.dto;

import ewm.events.dto.EventShortDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {
    private long id;
    private String title;
    private List<EventShortDto> events;
    private Boolean pinned;

    public CompilationDto(long id, String title, Boolean pinned) {
        this.id = id;
        this.title = title;
        this.pinned = pinned;
    }
}
