package sagrada.model;

import java.util.Objects;

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

    @Override
    public String toString() {
        if (this.die == null && this.position == null) {
            return "[ ]: X: 0 Y: 0";
        } else if (this.position != null && this.die == null) {
            return "[ " + this.position.toString() + " ]: X: 0 Y: 0";
        } else if (this.position != null) {
            return "[ " + this.position.toString() + " ]: " + this.die.toString();
        } else {
            return "";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || this.getClass() != o.getClass()) return false;

        Square square = (Square) o;
        return Objects.equals(this.position, square.position) && this.color == square.color && Objects.equals(this.value, square.value) && Objects.equals(this.die, square.die);
    }
}
