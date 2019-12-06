package sagrada.model;

public abstract class ToolCard extends Card {
    private final int id;
    private final String description;
    private int cost = 1;

    public ToolCard(int id, String name, String description) {
        super(name);
        this.id = id;
        this.description = description;
    }

    public int getId() {
        return this.id;
    }

    public int getCost() {
        return this.cost;
    }

    public String getDescription() {
        return this.description;
    }

    public abstract void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Object message);

    protected void incrementCost() {
        if (this.cost < 2) {
            this.cost = 2;
        }
    }
}
