package ewm.requests.controller;

import ewm.requests.dto.RequestDto;
import ewm.requests.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Добавление запроса пользователя на участие в событии userId={}, eventId={}", userId, eventId);
        return service.addRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Отмена запроса на участие пользователя userId={}, requestId={}", userId, requestId);
        return service.cancelRequest(userId, requestId);
    }

    @GetMapping
    public List<RequestDto> getRequestsByUser(@PathVariable Long userId) {
        log.info("Получение информации о заявках пользователя на участие в событиях userId={}", userId);
        return service.getRequestsByUser(userId);
    }
}