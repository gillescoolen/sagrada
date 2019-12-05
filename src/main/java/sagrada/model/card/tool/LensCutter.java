package sagrada.model.card.tool;

import sagrada.model.*;

public final class LensCutter extends ToolCard {
    public LensCutter(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Object message) {
        Object[] values = (Object[]) message;

        Die draftDie = (Die) values[0];

        int round = (int) values[1];
        Die roundTrackDie = roundTrack.getDieByKey(round);

        roundTrack.updateTrack(round, draftDie);
        draftPool.updateDraft(draftDie, roundTrackDie);
    }
}