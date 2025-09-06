package ewm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final BadRequestException e) {
        log.error("Объект не прошёл валидацию Spring {}", e.getMessage());
        return ErrorResponse.builder()
                .message(e.getMessage())
                .reason("Объект не прошёл валидацию")
                .status("BAD_REQUEST")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final InternalServerErrorException e) {
        log.error("Ошибка сервера {}", e.getMessage());
        return ErrorResponse.builder()
                .message(e.getMessage())
                .reason("Ошибка сервера")
                .status("INTERNAL_SERVER_ERROR")
                .timestamp(LocalDateTime.now())
                .build();
    }
}

