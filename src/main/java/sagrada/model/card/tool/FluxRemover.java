package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.model.*;

import java.sql.SQLException;

public final class FluxRemover extends ToolCard {
    public FluxRemover(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Player player, Game game, Object message) throws SQLException {
        Object[] messages = (Object[]) message;
        Die die = (Die) messages[0];
        int newValue = (Integer) messages[1];

        diceBag.put(die);
        Die newDie = diceBag.getRandomDice(1).get(0);
        newDie.setValue(newValue);
        
        draftPool.updateDraft(die, newDie);

        this.incrementCost();

        // TODO: implement database garbage kanker zooi ik wil fucking dood ajfdoiasjdfojajoasjdfojaf allahuakbar
        // inshallah mijn broer haydar
    }
}
