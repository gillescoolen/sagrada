package sagrada.model;

public class PatternCardField {
    private final Position position;
    private Color color;
    private Integer value;

    public PatternCardField(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return this.position;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Integer getValue() {
        return this.value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
