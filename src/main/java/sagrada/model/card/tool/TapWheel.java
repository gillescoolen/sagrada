package sagrada.model.card.tool;

import javafx.util.Pair;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.FavorTokenRepository;
import sagrada.database.repositories.ToolCardRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.List;

public final class TapWheel extends ToolCard {
    private ToolCardRepository toolCardRepository = new ToolCardRepository(this.connection);
    private FavorTokenRepository favorTokenRepository = new FavorTokenRepository(this.connection);

    public TapWheel(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public boolean use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Player player, Game game, Object message) throws SQLException {
        @SuppressWarnings("unchecked")
        List<Pair<Square, Square>> movePair = (List<Pair<Square, Square>>) message;

        for (var pair : movePair) {
            Square newSquare = pair.getKey();
            Square oldSquare = pair.getValue();

            patternCard.moveDie(player, newSquare, oldSquare, connection);
        }

        this.incrementCost();

        FavorToken favorToken = player.getNonAffectedFavorToken();
        favorToken.setToolCard(this);

        favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), false, game.getId());

        return true;
    }
}
