package sagrada.model;

import java.util.Random;

public class Die {
    private final Color color;
    private Integer value = null;
    private ToolCard usedToolCard;

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

    public ToolCard getUsedToolCard() {
        return this.usedToolCard;
    }

    public Die(Color color) {
        this.color = color;
    }

    public void flip() {
        if (this.value == null) {
            throw new RuntimeException("Cannot flip a die which has no value");
        }

        switch (this.value) {
            case 1:
                this.setValue(6);
                break;
            case 2:
                this.setValue(5);
                break;
            case 3:
                this.setValue(4);
                break;
            case 4:
                this.setValue(3);
                break;
            case 5:
                this.setValue(2);
                break;
            case 6:
                this.setValue(1);
        }
    }

    public void roll() {
        if (this.value != null) {
            throw new RuntimeException("Cannot roll the die twice");
        }

        Random random = new Random();
        this.setValue(random.nextInt(6) + 1);
    }

    public void setUsedToolCard(ToolCard usedToolCard) {
        this.usedToolCard = usedToolCard;
    }

    @Override
    public String toString() {
        return this.color.getColor() + " - " + this.value;
    }
}
