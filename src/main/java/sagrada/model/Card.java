package sagrada.model;

public abstract class Card {
    private final String name;

    public String getName() {
        return this.name;
    }

    public Card(String name) {
        this.name = name;
    }
}
