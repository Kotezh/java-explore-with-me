package dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HitDto {
    @NotNull
    private String app;

    @NotNull
    private String uri;

    @NotNull
    private String ip;

    @NotNull
    private LocalDateTime timestamp;
}