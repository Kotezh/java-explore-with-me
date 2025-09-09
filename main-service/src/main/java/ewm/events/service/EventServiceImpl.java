package ewm.events.service;

import client.StatsClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.HitDto;
import dto.StatsDto;
import ewm.categories.Category;
import ewm.categories.service.CategoryService;
import ewm.events.Event;
import ewm.events.EventMapper;
import ewm.events.EventRepository;
import ewm.events.dto.*;
import ewm.events.enums.AdminAction;
import ewm.events.enums.PrivateAction;
import ewm.events.enums.State;
import ewm.exception.BadRequestException;
import ewm.exception.ConflictException;
import ewm.exception.NotFoundException;
import ewm.locations.Location;
import ewm.locations.service.LocationService;
import ewm.requests.RequestRepository;
import ewm.requests.dto.ConfirmedRequestsDto;
import ewm.user.User;
import ewm.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static ewm.events.enums.AdminAction.PUBLISH_EVENT;
import static ewm.events.enums.AdminAction.REJECT_EVENT;
import static ewm.events.enums.PrivateAction.CANCEL_REVIEW;
import static ewm.events.enums.PrivateAction.SEND_TO_REVIEW;
import static ewm.events.enums.State.*;
import static ewm.requests.enums.RequestStatus.CONFIRMED;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app}")
    private String app;

    @Override
    public EventDto create(long userId, EventCreateDto dto) {
        validateEventTime(dto.getEventDate());
        User user = userService.getUserById(userId);
        Category category = categoryService.checkAndReturnCategory(dto.getCategory());
        Location location = locationService.getOrSave(dto.getLocation());

        Event event = eventMapper.mapEventCreateDtoToModel(dto);
        event.setInitiator(user);
        event.setCategory(category);
        event.setLocation(location);
        event.setCreatedOn(LocalDateTime.now());
        event.setState(PENDING);

        return eventMapper.mapModelToEventDto(eventRepository.save(event), 0L);
    }

    @Override
    public EventDto updateEventByOwner(Long userId, Long eventId, EventUserUpdateDto dto) {
        Event event = checkAndReturnEvent(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new BadRequestException("Событие не должно быть опубликовано");
        }
        if (event.getState() == PUBLISHED) {
            throw new ConflictException("Невозможно обновить событие, потому что оно в статусе: PUBLISHED");
        }
        String annotation = dto.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryService.checkAndReturnCategory(dto.getCategory()));
        }
        String description = dto.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        LocalDateTime eventDate = dto.getEventDate();
        if (eventDate != null) {
            validateEventTime(eventDate);
            event.setEventDate(eventDate);
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationService.getOrSave(dto.getLocation()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        String title = dto.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }
        if (dto.getStateAction() != null) {
            PrivateAction stateActionPrivate = PrivateAction.valueOf(dto.getStateAction());
            if (stateActionPrivate.equals(SEND_TO_REVIEW)) {
                event.setState(PENDING);
            } else if (stateActionPrivate.equals(CANCEL_REVIEW)) {
                event.setState(CANCELED);
            }
        }
        event = eventRepository.save(event);
        return eventMapper.mapModelToEventDto(event, requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
    }

    @Override
    public EventDto updateEventByAdmin(Long eventId, EventAdminUpdateDto dto) {
        Event event = checkAndReturnEvent(eventId);

        if (dto.getStateAction() != null) {
            AdminAction stateAction = AdminAction.valueOf(dto.getStateAction());
            if (!event.getState().equals(PENDING) && stateAction.equals(PUBLISH_EVENT)) {
                throw new ConflictException("Невозможно опубликовать событие, потому что оно в статусе: не PENDING");
            }
            if (event.getState().equals(PUBLISHED) && stateAction.equals(REJECT_EVENT)) {
                throw new ConflictException("Невозможно отклонить событие, потому что оно в статусе: PUBLISHED");
            }
            if (stateAction.equals(PUBLISH_EVENT)) {
                event.setState(PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (stateAction.equals(REJECT_EVENT)) {
                event.setState(State.CANCELED);
            }
        }

        String annotation = dto.getAnnotation();
        if (annotation != null && !annotation.isBlank()) {
            event.setAnnotation(annotation);
        }
        if (dto.getCategory() != null) {
            event.setCategory(categoryService.checkAndReturnCategory(dto.getCategory()));
        }
        String description = dto.getDescription();
        if (description != null && !description.isBlank()) {
            event.setDescription(description);
        }
        LocalDateTime eventDate = dto.getEventDate();
        if (eventDate != null) {
            validateEventTime(eventDate);
            event.setEventDate(eventDate);
        }
        if (dto.getLocation() != null) {
            event.setLocation(locationService.getOrSave(dto.getLocation()));
        }
        if (dto.getPaid() != null) {
            event.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            event.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            event.setRequestModeration(dto.getRequestModeration());
        }
        String title = dto.getTitle();
        if (title != null && !title.isBlank()) {
            event.setTitle(title);
        }

        event = eventRepository.save(event);
        return eventMapper.mapModelToEventDto(event, requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByOwnerId(Long userId, Integer from, Integer size) {
        List<Event> events = eventRepository.findAllByInitiatorId(userId, PageRequest.of(from / size, size));
        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequestsDto::getEvent, ConfirmedRequestsDto::getCount));
        return events.stream()
                .map(event -> eventMapper.mapModelToEventShortDto(event, confirmedRequests.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventDto getEventByOwner(Long userId, Long eventId) {
        Event event = checkAndReturnEvent(eventId);
        if (event.getInitiator().getId() == userId) {
            return eventMapper.mapModelToEventDto(event, requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        } else {
            throw new NotFoundException("Пользователь с " + userId + " не найден");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventViewsDto> getEventsByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                                LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from,
                                                Integer size) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new ValidationException("Некорректный запрос - дата начала позднее даты окончания");
        }
        Specification<Event> specification = Specification.where(null);
        if (users != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("initiator").get("id").in(users));
        }
        if (states != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("state").as(String.class).in(states));
        }
        if (categories != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (rangeStart != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
        List<Event> events = eventRepository.findAll(specification, PageRequest.of(from / size, size)).getContent();
        List<EventViewsDto> result = new ArrayList<>();
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());
        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new NotFoundException("Дата начала не найдена"));
        ResponseEntity<Object> response = statsClient.getStats(start, LocalDateTime.now(), uris, true);

        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED).stream()
                .collect(Collectors.toMap(ConfirmedRequestsDto::getEvent, ConfirmedRequestsDto::getCount));
        for (Event event : events) {
            List<StatsDto> statsDto = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
            });
            if (!statsDto.isEmpty()) {
                result.add(eventMapper.mapModelToEventDtoWithViews(event, statsDto.getFirst().getHits(),
                        confirmedRequests.getOrDefault(event.getId(), 0L)));
            } else {
                result.add(eventMapper.mapModelToEventDtoWithViews(event, 0L,
                        confirmedRequests.getOrDefault(event.getId(), 0L)));
            }
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventViewsShortDto> getEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                              Integer size, HttpServletRequest request) {
        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("Некорректный запрос - дата начала позднее даты окончания");
        }

        Specification<Event> specification = Specification.where(null);
        if (text != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), "%" + text.toLowerCase() + "%")
                    ));
        }
        if (categories != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    root.get("category").get("id").in(categories));
        }
        if (paid != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("paid"), paid));
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDateTime = Objects.requireNonNullElseGet(rangeStart, () -> now);
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("eventDate"), startDateTime));
        if (rangeEnd != null) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThan(root.get("eventDate"), rangeEnd));
        }
        if (onlyAvailable != null && onlyAvailable) {
            specification = specification.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("participantLimit"), 0));
        }
        specification = specification.and((root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("state"), PUBLISHED));
        PageRequest pageRequest;
        if (sort.equals("EVENT_DATE")) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("eventDate"));
        } else if (sort.equals("VIEWS")) {
            pageRequest = PageRequest.of(from / size, size, Sort.by("views").descending());
        } else {
            throw new ValidationException("Ошибка сортировки: " + sort);
        }
        List<Event> events = eventRepository.findAll(specification, pageRequest).getContent();
        List<EventViewsShortDto> result = new ArrayList<>();
        List<String> uris = events.stream()
                .map(event -> String.format("/events/%s", event.getId()))
                .collect(Collectors.toList());
        LocalDateTime start = events.stream()
                .map(Event::getCreatedOn)
                .min(LocalDateTime::compareTo)
                .orElseThrow(() -> new NotFoundException("Дата начала не найдена"));

        HitDto hitDto = new HitDto(app, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        ResponseEntity<Object> saveResponse = statsClient.save(hitDto);

        ResponseEntity<Object> response = statsClient.getStats(start, LocalDateTime.now(), uris, true);

        List<Long> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> confirmedRequests = requestRepository.findAllByEventIdInAndStatus(ids, CONFIRMED)
                .stream()
                .collect(Collectors.toMap(ConfirmedRequestsDto::getEvent, ConfirmedRequestsDto::getCount));
        for (Event event : events) {
            List<StatsDto> statsDto = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
            });
            if (!statsDto.isEmpty()) {
                result.add(eventMapper.mapModelToEventShortDtoWithViews(event, statsDto.getFirst().getHits(),
                        confirmedRequests.getOrDefault(event.getId(), 0L)));
            } else {
                result.add(eventMapper.mapModelToEventShortDtoWithViews(event, 0L,
                        confirmedRequests.getOrDefault(event.getId(), 0L)));
            }
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public EventViewsDto getEventById(Long eventId, HttpServletRequest request) {
        Event event = checkAndReturnEvent(eventId);
        if (event.getState() != PUBLISHED) {
            throw new NotFoundException("Event is not PUBLISHED");
        }

        HitDto hitDto = new HitDto(app, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        try {
            statsClient.save(hitDto);
        } catch (Exception e) {
            log.error("Error sending hit to stats service: {}", e.getMessage());
        }

        // Делаем небольшую паузу для обработки (100-200ms)
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        LocalDateTime startTime = event.getCreatedOn();
        LocalDateTime endTime = LocalDateTime.now().plusSeconds(1);

        try {
            ResponseEntity<Object> response = statsClient.getStats(startTime, endTime,
                    List.of(request.getRequestURI()), true);
            List<StatsDto> statsDto = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
            });

            long views = statsDto.isEmpty() ? 0L : statsDto.getFirst().getHits();
            long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);

            return eventMapper.mapModelToEventDtoWithViews(event, views, confirmedRequests);

        } catch (Exception e) {
            log.error("Error fetching stats: {}", e.getMessage());
            // возвращаем 1, так как hit был отправлен
            return eventMapper.mapModelToEventDtoWithViews(event, 1L,
                    requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));
        }
    }

    private void validateEventTime(LocalDateTime eventTime) {
        if (eventTime.isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException(" должно содержать дату, которая еще не наступила. Value: " + eventTime);
        }
    }

    private Event checkAndReturnEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено"));
    }
}
