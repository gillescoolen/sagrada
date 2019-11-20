package sagrada.model;

public class ToolCard {
    private final int id;
    private final String description;

    public ToolCard(int id, String description) {
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
