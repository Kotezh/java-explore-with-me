package ewm.comments.service;

import ewm.comments.dto.CommentCreateDto;
import ewm.comments.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto create(long userId, long eventId, CommentCreateDto dto);

    CommentDto update(long userId, long eventId, long commentId, CommentCreateDto dto);

    void deleteCommentByUser(long userId, long commentId);

    void deleteCommentByAdmin(long commentId);

    CommentDto getCommentById(long commentId);

    List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size);

    List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size);
}
