package sagrada.util;

public class ChatLinePair {
    private String username;
    private String message;

    public ChatLinePair(String username, String message) {
        this.username = username;
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}
