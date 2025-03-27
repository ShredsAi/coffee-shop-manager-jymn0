package ai.shreds.adapter.primary;

public class AdapterInventoryException extends RuntimeException {

    private final String message;

    public AdapterInventoryException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
