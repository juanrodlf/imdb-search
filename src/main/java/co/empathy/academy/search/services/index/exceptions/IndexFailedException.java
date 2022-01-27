package co.empathy.academy.search.services.index.exceptions;

public class IndexFailedException extends Exception{

    private final static String MESSAGE = "The bulk process for indexing titles failed.";

    public IndexFailedException(Throwable ex) {
        super(MESSAGE, ex);
    }

    public IndexFailedException() {
        super(MESSAGE);
    }

}
