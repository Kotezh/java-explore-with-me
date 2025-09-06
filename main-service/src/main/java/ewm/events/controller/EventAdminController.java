package ewm.events.controller;

import ewm.events.dto.EventAdminUpdateDto;
import ewm.events.dto.EventDto;
import ewm.events.dto.EventViewsDto;
import ewm.events.service.EventService;
import jakarta.validation.Valid;
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
@RequestMapping("/admin/events")
public class EventAdminController {
    private final EventService service;

    @PatchMapping("/{eventId}")
    public EventDto updateEventByAdmin(@PathVariable Long eventId,
                                       @RequestBody @Valid EventAdminUpdateDto eventUpdateAdminDto) {
        log.info("Изменение события админом eventId={}, eventUpdateAdminDto={}", eventId, eventUpdateAdminDto);
        return service.updateEventByAdmin(eventId, eventUpdateAdminDto);
    }

    @GetMapping
    public List<EventViewsDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) LocalDateTime rangeStart,
            @RequestParam(required = false) LocalDateTime rangeEnd,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Запрос событий админом users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        return service.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
