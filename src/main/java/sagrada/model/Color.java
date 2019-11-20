package sagrada.model;

public enum Color {
    RED("red"),
    GREEN("green"),
    BLUE("blue"),
    YELLOW("yellow"),
    PURPLE("purple");

    private final String color;

    Color(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }
}
