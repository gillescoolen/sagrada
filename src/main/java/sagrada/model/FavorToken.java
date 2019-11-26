package sagrada.model;

public class FavorToken {
    private final int id;
    private Integer round = null;

    public FavorToken(int id) {
        this.id = id;
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
}
