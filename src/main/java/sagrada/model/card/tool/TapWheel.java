package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

public final class TapWheel extends ToolCard {
    public TapWheel(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Object message) {
        this.incrementCost();
    }
}
