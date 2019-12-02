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
}
