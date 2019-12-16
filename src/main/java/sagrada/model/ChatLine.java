package sagrada.model;

import java.time.LocalDateTime;

public class ChatLine {
    private final Player player;
    private final String message;

    public ChatLine(Player player,  String message) {
        this.player = player;
        this.message = message;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getMessage() {
        return this.message;
    }

}
