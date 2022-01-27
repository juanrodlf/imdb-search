package co.empathy.academy.search.services.search.exceptions;

public class EmptyQueryException extends Exception {

    private final static String MESSAGE = "The text to be searched cannot be empty.";

    public EmptyQueryException() {
        super(MESSAGE);
    }

}
