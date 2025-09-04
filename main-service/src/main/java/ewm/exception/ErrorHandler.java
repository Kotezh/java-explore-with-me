package ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Искомый объект не найден {}", e.getMessage());
        return ErrorResponse.builder()
                .message(e.getMessage())
                .reason("Искомый объект не найден")
                .status("NOT_FOUND")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorException(final InternalServerErrorException e) {
        log.error("Ошибка сервера {}", e.getMessage());
        return ErrorResponse.builder()
                .message(e.getMessage())
                .reason("Ошибка сервера")
                .status("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConflictException e) {
        log.error("Произошел конфликт {}", e.getMessage());
        return ErrorResponse.builder()
                .message(e.getMessage())
                .reason("Произошел конфликт")
                .status("FORBIDDEN")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleСonflictDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error("Ошибка нарушения целостности {}", e.getMessage());
        return ErrorResponse.builder()
                .message(e.getMessage())
                .reason("Ошибка нарушения целостности")
                .status("CONFLICT")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({ValidationException.class, NumberFormatException.class, BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(RuntimeException e) {
        log.error("Некорректный запрос {}", e.getMessage());
        return ErrorResponse.builder()
                .message(e.getMessage())
                .reason("Некорректный запрос")
                .status("BAD_REQUEST")
                .timestamp(LocalDateTime.now())
                .build();
    }
}


