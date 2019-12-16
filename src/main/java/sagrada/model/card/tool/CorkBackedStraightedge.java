package sagrada.model.card.tool;

import javafx.util.Pair;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.ChatRepository;
import sagrada.database.repositories.FavorTokenRepository;
import sagrada.database.repositories.ToolCardRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.ArrayList;

public final class CorkBackedStraightedge extends ToolCard {
    private ToolCardRepository toolCardRepository = new ToolCardRepository(this.connection);
    private FavorTokenRepository favorTokenRepository = new FavorTokenRepository(this.connection);
    private ChatRepository chatRepository = new ChatRepository(this.connection);

    public CorkBackedStraightedge(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public boolean use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Player player, Game game, Object message) throws SQLException {
        @SuppressWarnings("unchecked")
        var move = (Pair<Die, Square>) message;
        var die = move.getKey();
        var square = move.getValue();

        this.incrementCost();

        ArrayList<Die> dice = new ArrayList<>();
        dice.add(die);

        patternCard.placeDie(player, square, die, connection);

        FavorToken favorToken = player.getNonAffectedFavorToken();
        favorToken.setToolCard(this);

        chatRepository.add(new ChatLine(player, String.format("heeft %s gebruikt met dobbelsteen %s.", this.getName(), die.toString())));
        favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), false, game.getId());
        toolCardRepository.addAffectedToolCard(this, dice, game.getId());

        return true;
    }
}