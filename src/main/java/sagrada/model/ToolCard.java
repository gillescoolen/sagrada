package sagrada.model;

public class ToolCard extends Card {
    private final int id;
    private final String description;

    public ToolCard(int id, String name, String description) {
        super(name);
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }
}
