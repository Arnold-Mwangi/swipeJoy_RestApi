package blog.posts.p1.authentication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class RestResponseEntityExceptionHandler {
    @ExceptionHandler(InvalidInputProblem.class)
    protected ResponseEntity<Map<String, Object>> handleInvalidInput(
            InvalidInputProblem ex, WebRequest request
    ){
        Map<String, Object> exResponse = new HashMap<>();
        exResponse.put("violations", ex.getViolations());
        exResponse.put("details", String.format("%s.%s.%s", ex.getRoot(), ex.getScope(), ex.getReason()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exResponse);
    }


}