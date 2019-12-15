package sagrada.model;

import java.util.Objects;
import java.util.Random;

public class Die {
    private final Integer number;
    private final Color color;
    private Integer value = null;

    public Die(Integer number, Color color) {
        this.number = number;
        this.color = color;
    }

    public Integer getNumber() {
        return this.number;
    }

    public Color getColor() {
        return this.color;
    }

    public Integer getValue() {
        return this.value;
    }

    public void setValue(Integer value) {
        if (value == null) return;

        if (value >= 1 && value <= 6) {
            this.value = value;
        }
    }

    public void flip() {
        if (this.value == null) {
            throw new RuntimeException("Cannot flip a die which has no value");
        }

        this.setValue(7 - this.value);
    }

    public void roll() {
        Random random = new Random();
        this.setValue(random.nextInt(6) + 1);
    }

    @Override
    public String toString() {
        return this.color.getDutchColorName() + " " + this.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || this.getClass() != o.getClass()) return false;

        Die die = (Die) o;
        return Objects.equals(number, die.number) && color == die.color && Objects.equals(value, die.value);
    }
}
