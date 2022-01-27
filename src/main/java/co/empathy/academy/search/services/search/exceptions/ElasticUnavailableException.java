package co.empathy.academy.search.services.search.exceptions;

public class ElasticUnavailableException extends Exception {

    private final static String MESSAGE = "It was not possible to reach any elasticsearch node.";

    public ElasticUnavailableException(Throwable ex) {
        super(MESSAGE, ex);
    }

    public ElasticUnavailableException() {
        super(MESSAGE);
    }

}
