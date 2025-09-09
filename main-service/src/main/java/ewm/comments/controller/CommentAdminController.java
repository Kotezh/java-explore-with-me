package ewm.comments.controller;

import ewm.comments.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
public class CommentAdminController {
    private final CommentService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info("Запрос на удаление комментария с id = {} ", commentId);
        commentService.deleteCommentByAdmin(commentId);
    }
}
