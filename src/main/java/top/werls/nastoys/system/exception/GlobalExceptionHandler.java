package top.werls.nastoys.system.exception;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.werls.nastoys.common.ResultData;

import java.util.HashMap;
import java.util.Map;


/**
 * 全局异常处理
 * @author leejiawei
 * @version 1
 * @since on  2022/2/8
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResultData<String > defaultExceptionHandler(Exception e, HttpServletResponse response) {
        log.error("Exception:{}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return ResultData.systemError(e.getLocalizedMessage());
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResultData<String > defaultExpiredJwtExceptionHandler(ExpiredJwtException e, HttpServletResponse response) {
        log.error("Exception:{}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return ResultData.systemError("JWT token expired");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResultData<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResultData.systemError(errors);
    }

}
