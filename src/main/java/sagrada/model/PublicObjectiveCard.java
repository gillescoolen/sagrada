package sagrada.model;

public class PublicObjectiveCard {
    private int id;
    private String name;
    private String description;
    private int points;

    public PublicObjectiveCard(int id, String name, String description, int points) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.points = points;
    }

    public String getName() {
        return this.name;
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
