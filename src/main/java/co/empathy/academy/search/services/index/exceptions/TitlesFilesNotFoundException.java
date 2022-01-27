package co.empathy.academy.search.services.index.exceptions;

public class TitlesFilesNotFoundException extends Exception {

    private final static String MESSAGE = "The given path for titles file was not found.";

    public TitlesFilesNotFoundException(String path) {
        super(MESSAGE + "(" + path + ")");
    }

    public TitlesFilesNotFoundException() {
        super(MESSAGE);
    }

}
