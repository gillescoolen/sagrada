package sagrada.model;

public abstract class ToolCard extends Card {
    private final int id;
    private final String description;
    private int points = 1;

    public ToolCard(int id, String name, String description) {
        super(name);
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public int getPoints() {
        return this.points;
    }

    public String getDescription() {
        return this.description;
    }

    public abstract void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Object message);
}
