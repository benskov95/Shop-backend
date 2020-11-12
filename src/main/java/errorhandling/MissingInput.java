package errorhandling;

public class MissingInput extends Exception{

    public MissingInput(String message) {
        super(message);
    }
}
