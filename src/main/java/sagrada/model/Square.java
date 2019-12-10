package sagrada.model;

/**
 * This is *patterncardfield* in the database.
 */
public class Square {
    private Position position;
    private Color color;
    private Integer value;
    private Die die;

    public void setPosition(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return this.position;
    }

    public Color getColor() {
        return this.die == null ? this.color : this.die.getColor();
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Integer getValue() {
        return this.die == null ? this.value : this.die.getValue();
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Die getDie() {
        return this.die;
    }

    public void setDie(Die die) {
        this.die = die;
    }
}
