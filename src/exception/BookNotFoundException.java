package exception;

public class BookNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

    public BookNotFoundException(String message) {
        super(message); // Passes message to parent Exception class
    }

    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
