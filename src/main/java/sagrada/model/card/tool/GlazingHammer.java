package sagrada.model.card.tool;

import sagrada.model.DiceBag;
import sagrada.model.Die;
import sagrada.model.PatternCard;
import sagrada.model.ToolCard;

import java.util.List;

public final class GlazingHammer extends ToolCard {
    public GlazingHammer(int id, String name, String description) {
        super(id, name, description);
    }

    @Override
    public void use(List<Die> dice, DiceBag diceBag, PatternCard patternCard) {
    }
}
