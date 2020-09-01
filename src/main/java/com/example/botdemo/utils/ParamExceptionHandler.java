package com.example.botdemo.utils;

import com.example.botdemo.ResponsePage;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.util.Set;

/**
 * @author lin945
 * @date 2020/6/16 21:04
 */
//错误捕获
@RestControllerAdvice
public class ParamExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponsePage handle(ValidationException exception) {
        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException exs = (ConstraintViolationException) exception;
            Set<ConstraintViolation<?>> violations = exs.getConstraintViolations();
            for (ConstraintViolation<?> item : violations) {
                //打印验证不通过的信息
                return new ResponsePage().failed(item.getMessage());
            }
        }
        return new ResponsePage().failed(exception.getMessage());
    }
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponsePage handle2(Exception exception) {
        return new ResponsePage().failed(exception.getMessage());
    }
}



