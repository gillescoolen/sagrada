package sagrada.model;

public class FavorToken {
    private final int id;
    private Integer playerId = null;
    private Integer round = null;
    private ToolCard toolCard = null;

    public FavorToken(int id, int round, ToolCard toolCard, Integer playerId) {
        this.id = id;
        this.round = round;
        this.toolCard = toolCard;
        this.playerId = playerId;
    }

    public int getId() {
        return this.id;
    }

    public Integer getRound() {
        return this.round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }

    public ToolCard getToolCard() {
        return this.toolCard;
    }

    public void setToolCard(ToolCard toolCard) {
        this.toolCard = toolCard;
    }

    public Integer getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }
}
