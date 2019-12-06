package sagrada.model.card.tool;

import sagrada.model.*;

public final class GlazingHammer extends ToolCard {
    public GlazingHammer(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Object message) {
        draftPool.reRollDraft();
    }
}
