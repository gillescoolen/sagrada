package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.FavorTokenRepository;
import sagrada.database.repositories.ToolCardRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.ArrayList;

public final class EglomiseBrush extends ToolCard {
    private ToolCardRepository toolCardRepository = new ToolCardRepository(this.connection);
    private FavorTokenRepository favorTokenRepository = new FavorTokenRepository(this.connection);

    public EglomiseBrush(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public boolean use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Player player, Game game, Object message) throws SQLException {
        Object[] values = (Object[]) message;
        Square oldSquare = (Square) values[0];
        Square newSquare = (Square) values[1];

        patternCard.moveDie(player, oldSquare, newSquare, connection);


        ArrayList<Die> dice = new ArrayList<>();
        dice.add(oldSquare.getDie());

        for (var i = 0; this.getCost() < i; i++) {
            FavorToken favorToken = player.getNonAffectedFavorToken();
            favorToken.setToolCard(this);

            favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), false, game.getId());
        }

        this.incrementCost();

        toolCardRepository.addAffectedToolCard(this, dice, game.getId());

        return true;
    }
}
