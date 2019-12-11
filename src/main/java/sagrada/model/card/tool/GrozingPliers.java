package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.FavorTokenRepository;
import sagrada.database.repositories.ToolCardRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.ArrayList;

public final class GrozingPliers extends ToolCard {
    private ToolCardRepository toolCardRepository = new ToolCardRepository(this.connection);
    private FavorTokenRepository favorTokenRepository = new FavorTokenRepository(this.connection);

    public GrozingPliers(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Player player, Game game, Object message) throws SQLException {
        Object[] messages = (Object[]) message;
        Die die = (Die) messages[0];
        Integer newDieValue = (Integer) messages[1];

        die.setValue(newDieValue);

        game.updateDraftPool(die, die);

        this.incrementCost();

        ArrayList<Die> dice = new ArrayList<>();
        dice.add(die);

        FavorToken favorToken = player.getNonAffectedFavorToken();
        favorToken.setToolCard(this);

        favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), false);
        toolCardRepository.addAffectedToolCard(this, dice, game.getId());
    }
}