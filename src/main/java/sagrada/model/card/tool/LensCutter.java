package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

public final class LensCutter extends ToolCard {
    public LensCutter(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Object message) {
        Object[] values = (Object[]) message;

        Die draftDie = (Die) values[0];

        int round = (int) values[1];
        Die roundTrackDie = roundTrack.getDieByKey(round);

        roundTrack.updateTrack(round, draftDie);
        draftPool.updateDraft(draftDie, roundTrackDie);

        this.incrementCost();
    }
}