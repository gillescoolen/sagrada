package sagrada.model;

import sagrada.database.DatabaseConnection;

public abstract class ToolCard extends ObservableCard<ToolCard>  {
    private final int id;
    private final String description;
    private int cost = 1;
    protected final DatabaseConnection connection;

    public ToolCard(int id, String name, String description, DatabaseConnection databaseConnection) {
        super(name);
        this.id = id;
        this.description = description;
        this.connection = databaseConnection;
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

        this.update(this);
    }
}
