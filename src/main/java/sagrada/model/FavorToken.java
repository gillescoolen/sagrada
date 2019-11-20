package sagrada.model;

public class FavorToken {
    private int id;
    private Player player;
    private Integer round = null;

    public FavorToken(int id, Player player) {
        this.id = id;
        this.player = player;
    }

    public int getId() {
        return this.id;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Integer getRound() {
        return this.round;
    }

    public void setRound(Integer round) {
        this.round = round;
    }
}
