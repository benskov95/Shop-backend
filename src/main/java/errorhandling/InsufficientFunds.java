package errorhandling;

public class InsufficientFunds extends Exception{

    public InsufficientFunds(String message) {
        super(message);
    }
}
