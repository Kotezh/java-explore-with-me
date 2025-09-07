package ewm.stats;

import dto.HitDto;
import dto.StatsDto;
import ewm.stats.service.EndpointHitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class EndpointHitController {
    private final EndpointHitService service;

    @GetMapping("/stats")
    public List<StatsDto> getStats(@RequestParam LocalDateTime start,
                                   @RequestParam LocalDateTime end,
                                   @RequestParam(required = false) List<String> uris,
                                   @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Получение статистики start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        return service.getStats(start, end, uris, unique);
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void hit(@RequestBody @Valid HitDto hitDto) {
        log.info("Добавление просмотра события в статистику hitDto={}", hitDto);
        service.hit(hitDto);
    }
}
