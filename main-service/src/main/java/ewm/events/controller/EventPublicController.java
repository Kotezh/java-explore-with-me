package ewm.events.controller;

import ewm.events.dto.EventViewsDto;
import ewm.events.dto.EventViewsShortDto;
import ewm.events.service.EventService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {
    private final EventService service;

    @GetMapping
    public List<EventViewsShortDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(defaultValue = "EVENT_DATE") String sort,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size,
            HttpServletRequest request) {
        log.info("Запрос событий с фильтрацией и сортировкой text={}, paid={}, categories={}, rangeStart={}, rangeEnd={}, " +
                        "onlyAvailable={}, sort={}, from={}, size={}, request={}",
                        text, paid, categories, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
        return service.getEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request);
    }

    @GetMapping("/{eventId}")
    public EventViewsDto getEventById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("Получение события по eventId={}, request={}", eventId, request);
        return service.getEventById(eventId, request);
    }
}
