package sagrada.model;

public abstract class PublicObjectiveCard extends ObjectiveCard {
    private final int id;
    private final String description;
    private final int points;

    public PublicObjectiveCard(int id, String name, String description, int points) {
        super(name);
        this.id = id;
        this.description = description;
        this.points = points;
    }

    public int getId() {
        return this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public int getPoints() {
        return this.points;
    }
}
