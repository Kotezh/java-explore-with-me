package ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(final NotFoundException e) {
        log.error("Искомый объект не найден {}", e.getMessage());
        return new ErrorResponse("Искомый объект не найден", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerErrorException(final InternalServerErrorException e) {
        log.error("Ошибка сервера {}", e.getMessage());
        return new ErrorResponse("Ошибка сервера", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictException(final ConflictException e) {
        log.error("Произошел конфликт {}", e.getMessage());
        return new ErrorResponse("Произошел конфликт", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequestException(final BadRequestException e) {
        log.error("Объект не прошёл валидацию Spring {}", e.getMessage());
        return new ErrorResponse("Объект не прошёл валидацию Spring", e.getMessage());
    }
}


