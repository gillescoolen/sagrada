package sagrada.model;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.ToolCardRepository;

import java.sql.SQLException;

public abstract class ToolCard extends ObservableCard<ToolCard> {
    private final int id;
    private final String description;
    private int cost = 1;
    protected final DatabaseConnection connection;

    private boolean canUse = false;

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

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return this.description;
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;

        this.update(this);
    }

    public boolean canUse() {
        return canUse;
    }

    public abstract void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Player player, Game game, Object message) throws SQLException;

    protected void incrementCost() {
        if (this.cost < 2) {
            this.cost = 2;
        }

        this.update(this);
    }

    public boolean isUsed(Game game) throws SQLException {
        var repository = new ToolCardRepository(this.connection);
        return repository.toolCardIsUsed(game.getId(), this.id);
    }
}
