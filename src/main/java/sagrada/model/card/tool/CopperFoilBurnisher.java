package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

public final class CopperFoilBurnisher extends ToolCard {
    public CopperFoilBurnisher(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Object message) {
        Object[] values = (Object[]) message;
        Square oldSquare = (Square)values[0];
        Square newSquare = (Square)values[1];

        newSquare.setDie(oldSquare.getDie());
        oldSquare.setDie(null);

        this.incrementCost();
    }
}