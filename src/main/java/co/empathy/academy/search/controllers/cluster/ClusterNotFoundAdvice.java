package co.empathy.academy.search.controllers.cluster;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice

public class ClusterNotFoundAdvice {
    @ResponseBody
    @ExceptionHandler(ClusterNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String clusterNotFoundHandler(ClusterNotFoundException exception) {
        return exception.getMessage();
    }
}
