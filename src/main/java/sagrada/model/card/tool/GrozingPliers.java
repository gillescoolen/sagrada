package sagrada.model.card.tool;

import sagrada.model.*;

public final class GrozingPliers extends ToolCard {
    public GrozingPliers(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Object message) {
        Object[] messages = (Object[]) message;
        Die die = (Die) messages[0];
        Integer newDieValue = (Integer) messages[1];

        die.setValue(newDieValue);

        this.incrementCost();
    }
}