package co.empathy.academy.search.services.index.exceptions;

public class IndexAlreadyExistsException extends Exception {

    private final static String MESSAGE = "The index already exists.";

    public IndexAlreadyExistsException(Throwable ex) {
        super(MESSAGE, ex);
    }

    public IndexAlreadyExistsException() {
        super(MESSAGE);
    }
}
