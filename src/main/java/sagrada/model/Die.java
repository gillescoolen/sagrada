package sagrada.model;

public class Die {
    private Color color;
    private Integer value = null;
    private ToolCard usedToolCard;

    public Die(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return this.color;
    }

    public Integer getValue() {
        return this.value;
    }

    public void setValue(Integer value) {
        if (value != null) {
            if (!(value < 1) && !(value > 6)) {
                this.value = value;
            }
        }
    }

    public ToolCard getUsedToolCard() {
        return this.usedToolCard;
    }

    public void setUsedToolCard(ToolCard usedToolCard) {
        this.usedToolCard = usedToolCard;
    }
}
