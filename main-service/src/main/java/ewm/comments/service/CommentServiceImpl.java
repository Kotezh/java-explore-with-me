package ewm.comments.service;

import ewm.comments.Comment;
import ewm.comments.CommentMapper;
import ewm.comments.CommentRepository;
import ewm.comments.dto.CommentCreateDto;
import ewm.comments.dto.CommentDto;
import ewm.events.Event;
import ewm.events.EventRepository;
import ewm.exception.BadRequestException;
import ewm.exception.NotFoundException;
import ewm.requests.RequestRepository;
import ewm.user.User;
import ewm.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static ewm.events.enums.State.PUBLISHED;
import static ewm.requests.enums.RequestStatus.CONFIRMED;

@Transactional
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserService userService;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Override
    public CommentDto create(long userId, long eventId, CommentCreateDto dto) {
        User author = userService.getUserById(userId);
        Event event = checkAndReturnEvent(eventId);
        if (event.getState() != PUBLISHED) {
            throw new BadRequestException("Событие должно иметь статус PUBLISHED");
        }
        dto.setCreated(LocalDateTime.now());
        dto.setConfirmedRequests(requestRepository.countByEventIdAndStatus(eventId, CONFIRMED));

        Comment comment = commentMapper.mapCommentCreateDtoToModel(dto, author, event);
        return commentMapper.mapModelToCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto update(long userId, long eventId, long commentId, CommentCreateDto dto) {
        Comment comment = checkAndReturnComment(commentId);

        if (comment.getAuthor().getId() != userId) {
            throw new NotFoundException("Редактировать комментарий может только автор");
        }
        if (comment.getEvent().getId() != eventId) {
            throw new NotFoundException("Событие к комментарию указано не верно");
        }
        comment.setText(dto.getText());
        comment.setEdited(LocalDateTime.now());

        return commentMapper.mapModelToCommentDto(commentRepository.save(comment));
    }

    @Override
    public void deleteCommentByUser(long userId, long commentId) {
        Comment comment = checkAndReturnComment(commentId);
        if (comment.getAuthor().getId() != userId) {
            throw new NotFoundException("Удалять комментарий может только автор.");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteCommentByAdmin(long commentId) {
        checkAndReturnComment(commentId);
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(long commentId) {
        Comment comment = checkAndReturnComment(commentId);
        return commentMapper.mapModelToCommentDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size) {
        checkAndReturnEvent(eventId);
        List<Comment> comments = commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size));
        if (comments.isEmpty()) {
            return List.of();
        }
        return commentMapper.mapListModelToCommentDto(comments);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size) {
        userService.getUserById(userId);
        List<Comment> comments = commentRepository.findAllByAuthorId(userId, PageRequest.of(from / size, size));
        if (comments.isEmpty()) {
            return List.of();
        }
        return commentMapper.mapListModelToCommentDto(comments);
    }

    private Comment checkAndReturnComment(long commentId) {
        return commentRepository.findById(commentId).orElseThrow(() ->
                new NotFoundException("Комментарий с id=" + commentId + " не найден"));
    }

    private Event checkAndReturnEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException("Событие с id=" + eventId + " не найдено"));
    }
}
