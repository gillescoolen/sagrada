package sagrada.model;

import java.time.LocalDateTime;

public class ChatLine {
    private Player player;
    private LocalDateTime timestamp;
    private String message;

    public ChatLine(Player player, LocalDateTime timestamp, String message) {
        this.player = player;
        this.timestamp = timestamp;
        this.message = message;
    }

    public Player getPlayer() {
        return this.player;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public String getMessage() {
        return this.message;
    }

}
