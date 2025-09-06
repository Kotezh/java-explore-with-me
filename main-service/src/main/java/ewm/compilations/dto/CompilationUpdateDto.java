package ewm.compilations.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationUpdateDto {
    @Size(min = 1, max = 50)
    private String title;

    private Boolean pinned;
    private List<Long> events;
}
