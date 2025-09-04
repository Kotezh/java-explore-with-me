package ewm.events.service;

import ewm.events.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventDto create(long userId, EventCreateDto dto);

    EventDto updateEventByOwner(Long userId, Long eventId, EventUserUpdateDto dto);

    EventDto updateEventByAdmin(Long eventId, EventAdminUpdateDto dto);

    List<EventShortDto> getEventsByOwnerId(Long userId, Integer from, Integer size);

    EventDto getEventByOwner(Long userId, Long eventId);

    List<EventViewsDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                         LocalDateTime rangeStart, LocalDateTime rangeEnd,
                                         Integer from, Integer size);

    List<EventViewsShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                       LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                       Integer size, HttpServletRequest request);

    EventViewsDto getEventById(Long eventId, HttpServletRequest request);
}
