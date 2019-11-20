package sagrada.model;

public class ToolCard {
    private int id;
    private String description;

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
