package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

public final class RunningPliers extends ToolCard {
    public RunningPliers(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Object message) {
        // TODO: bespreken

        this.incrementCost();
    }
}
