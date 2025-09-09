package ewm.comments.controller;

import ewm.comments.dto.CommentCreateDto;
import ewm.comments.dto.CommentDto;
import ewm.comments.service.CommentService;
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
@RequestMapping("/users/{userId}/comments")
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping("/event/{eventId}")
    @ResponseStatus(value = HttpStatus.CREATED)
    public CommentDto addComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Добавление комментария: {}", commentCreateDto);
        return commentService.create(userId, eventId, commentCreateDto);
    }

    @PatchMapping("/event/{eventId}/comment/{commentId}")
    public CommentDto updateComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @RequestBody @Valid CommentCreateDto commentCreateDto) {
        log.info("Обновление комментария пользователем с userId = {}, commentId = {} ", userId, commentId);
        return commentService.update(userId, eventId, commentId, commentCreateDto);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(
            @PathVariable Long userId,
            @PathVariable Long commentId) {
        log.info("Удаление комментария id = {} пользователем userId = {} ", userId, commentId);
        commentService.deleteCommentByUser(userId, commentId);
    }

    @GetMapping
    public List<CommentDto> getCommentsByUser(
            @PathVariable Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Запрос на получение комментариев пользователя с userId = {} ", userId);
        return commentService.getCommentsByUserId(userId, from, size);
    }
}
