package ewm.events.controller;

import ewm.events.dto.*;
import ewm.events.service.EventService;
import ewm.requests.dto.EventRequestStatusUpdateRequest;
import ewm.requests.dto.EventRequestStatusUpdateResult;
import ewm.requests.dto.RequestDto;
import ewm.requests.service.RequestService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {
    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public EventDto addEvent(@PathVariable Long userId, @RequestBody @Valid EventCreateDto dto) {
        log.info("Добавление события пользователем userId={}, dto={}", userId, dto);
        return eventService.create(userId, dto);
    }

    @PatchMapping("/{eventId}")
    public EventDto updateEventByOwner(@PathVariable Long userId,
                                       @PathVariable Long eventId,
                                       @RequestBody @Valid EventUserUpdateDto updateEvent) {
        log.info("Изменение события пользователем userId={}, eventId={}, updateEvent={}", userId, eventId, updateEvent);
        return eventService.updateEventByOwner(userId, eventId, updateEvent);
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsStatus(@PathVariable Long userId,
                                                               @PathVariable Long eventId,
                                                               @RequestBody EventRequestStatusUpdateRequest request) {
        log.info("Изменение статуса запроса пользователем userId={}, eventId={}, request={}", userId, eventId, request);
        return requestService.updateRequestsStatus(userId, eventId, request);
    }

    @GetMapping
    List<EventShortDto> getEventsByOwner(@PathVariable Long userId,
                                         @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                         @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Запрос событий пользователя userId={}, from={}, size={}", userId, from, size);
        return eventService.getEventsByOwnerId(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventDto getEventByOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запрос события пользователя userId={}, eventId={}", userId, eventId);
        return eventService.getEventByOwner(userId, eventId);
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByEventOwner(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запросы пользователя userId={}, eventId={}", userId, eventId);
        return requestService.getRequestsByEventOwner(userId, eventId);
    }
}
