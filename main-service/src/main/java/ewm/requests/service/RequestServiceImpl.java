package ewm.requests.service;

import ewm.events.Event;
import ewm.events.EventRepository;
import ewm.events.enums.State;
import ewm.exception.ConflictException;
import ewm.exception.NotFoundException;
import ewm.requests.Request;
import ewm.requests.RequestMapper;
import ewm.requests.RequestRepository;
import ewm.requests.dto.EventRequestStatusUpdateRequest;
import ewm.requests.dto.EventRequestStatusUpdateResult;
import ewm.requests.dto.RequestDto;
import ewm.requests.enums.RequestStatus;
import ewm.user.User;
import ewm.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.ValidationException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ewm.requests.enums.RequestStatus.*;

@Service
@Transactional
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserService userService;
    private final RequestMapper requestMapper;

    @Override
    public RequestDto addRequest(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
        User user = userService.getUserById(userId);
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new ConflictException("Запрос уже существует");
        }
        if (userId.equals(event.getInitiator().getId())) {
            throw new ConflictException("Владелец не может отправить запрос на участие в своем событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Участвовать можно только в опубликованном событии");
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <=
                requestRepository.countByEventIdAndStatus(eventId, CONFIRMED)) {
            throw new ConflictException("Превышен лимит участников");
        }
        Request request = new Request();
        request.setCreated(LocalDateTime.now());
        request.setEvent(event);
        request.setRequester(user);

        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(PENDING);
        } else {
            request.setStatus(CONFIRMED);
        }
        return requestMapper.mapModelToDto(requestRepository.save(request));
    }

    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest statusUpdateRequest) {
        User initiator = userService.getUserById(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено"));
        if (!event.getInitiator().equals(initiator)) {
            throw new ValidationException("Пользователь не является инициатором");
        }
        long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, CONFIRMED);
        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() <= confirmedRequests) {
            throw new ConflictException("Превышен лимит участников");
        }
        List<RequestDto> confirmed = new ArrayList<>();
        List<RequestDto> rejected = new ArrayList<>();
        List<Request> requests = requestRepository.findAllByEventIdAndIdInAndStatus(eventId,
                statusUpdateRequest.getRequestIds(), PENDING);
        for (int i = 0; i < requests.size(); i++) {
            Request request = requests.get(i);
            if (statusUpdateRequest.getStatus() == REJECTED) {
                request.setStatus(REJECTED);
                rejected.add(requestMapper.mapModelToDto(request));
            }
            if (statusUpdateRequest.getStatus() == CONFIRMED && event.getParticipantLimit() > 0 &&
                    (confirmedRequests + i) < event.getParticipantLimit()) {
                request.setStatus(CONFIRMED);
                confirmed.add(requestMapper.mapModelToDto(request));
            } else {
                request.setStatus(REJECTED);
                rejected.add(requestMapper.mapModelToDto(request));
            }
        }
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Override
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequesterId(requestId, userId);
        request.setStatus(RequestStatus.CANCELED);

        return requestMapper.mapModelToDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByEventOwner(Long userId, Long eventId) {
        userService.getUserById(userId);
        eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(() ->
                new NotFoundException("Событие с id " + eventId + " не найдено"));
        return requestRepository.findAllByEventId(eventId).stream()
                .map(requestMapper::mapModelToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getRequestsByUser(Long userId) {
        userService.getUserById(userId);
        return requestRepository.findAllByRequesterId(userId).stream()
                .map(requestMapper::mapModelToDto).toList();
    }
}
