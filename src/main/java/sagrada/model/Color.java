package sagrada.model;

public enum Color {
    RED("rood"),
    GREEN("groen"),
    BLUE("blauw"),
    YELLOW("geel"),
    PURPLE("paars");

    private final String color;

    Color(String color) {
        this.color = color;
    }

    public String getColor() {
        return this.color;
    }
}
