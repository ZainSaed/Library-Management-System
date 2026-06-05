package exception;

public class FineCalculationException extends Exception {

   private static final long serialVersionUID = 1L;

	
    public FineCalculationException(String message) {
        super(message);
    }

    public FineCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
