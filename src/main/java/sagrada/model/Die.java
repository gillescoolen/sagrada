package sagrada.model;

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

    /*@Override
    public String toString() {
        return this.color.getColor() + " - " + this.value;
    }*/
}
