package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.FavorTokenRepository;
import sagrada.database.repositories.ToolCardRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.ArrayList;

public final class RunningPliers extends ToolCard {
    private ToolCardRepository toolCardRepository = new ToolCardRepository(this.connection);
    private FavorTokenRepository favorTokenRepository = new FavorTokenRepository(this.connection);

    public RunningPliers(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Player player, Game game, Object message) throws SQLException {
        Die die = (Die) message;

        this.incrementCost();

        ArrayList<Die> dice = new ArrayList<>();
        dice.add(die);

        FavorToken favorToken = player.getNonAffectedFavorToken();
        favorToken.setToolCard(this);

        // TODO: UI??

        favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), true);
    }
}
