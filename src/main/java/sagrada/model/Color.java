package sagrada.model;

public enum Color {
    RED("red", "rood"),
    GREEN("green", "groen"),
    BLUE("blue", "blauw"),
    YELLOW("yellow", "geel"),
    PURPLE("purple", "paars");

    private final String color;
    private final String dutchColor;

    Color(String color, String dutchColor) {
        this.color = color;
        this.dutchColor = dutchColor;
    }

    public String getColor() {
        return this.color;
    }

    public String getDutchColorName() {
        return this.dutchColor;
    }
}
