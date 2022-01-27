package co.empathy.academy.search.controllers;

import co.empathy.academy.search.controllers.cluster.ClusterNotFoundException;
import co.empathy.academy.search.services.index.exceptions.IndexAlreadyExistsException;
import co.empathy.academy.search.services.index.exceptions.IndexFailedException;
import co.empathy.academy.search.services.index.exceptions.TitlesFilesNotFoundException;
import co.empathy.academy.search.services.search.exceptions.ElasticUnavailableException;
import co.empathy.academy.search.services.search.exceptions.EmptyQueryException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ControllerAdvisor {

    @ResponseBody
    @ExceptionHandler(ClusterNotFoundException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String clusterNotFoundHandler(ClusterNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(IndexAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String indexAlreadyExistsHandler(IndexAlreadyExistsException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(IndexFailedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String indexFailedHandler(IndexFailedException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(TitlesFilesNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String titlesFilesNotFoundHandler(TitlesFilesNotFoundException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(EmptyQueryException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    String emptyQueryHandler(EmptyQueryException ex) {
        return ex.getMessage();
    }

    @ResponseBody
    @ExceptionHandler(ElasticUnavailableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    String elasticUnavailableHandler(ElasticUnavailableException ex) {
        return ex.getMessage();
    }
}
