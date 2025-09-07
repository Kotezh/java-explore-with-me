package ewm.comments.controller;

import ewm.comments.dto.CommentDto;
import ewm.comments.service.CommentService;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentPublicController {
    private final CommentService commentService;

    @GetMapping("/event/{eventId}")
    List<CommentDto> getCommentsByEvent(
            @PathVariable Long eventId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "10") @Positive Integer size) {
        log.info("Получение всех комментариев события с eventId = {} ", eventId);
        return commentService.getCommentsByEventId(eventId, from, size);
    }

    @GetMapping("/{commentId}")
    CommentDto getCommentById(@PathVariable Long commentId) {
        log.info("Получение комментария с commentId = {} ", commentId);
        return commentService.getCommentById(commentId);
    }
}
