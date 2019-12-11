package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.FavorTokenRepository;
import sagrada.database.repositories.ToolCardRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class Lathekin extends ToolCard {
    private ToolCardRepository toolCardRepository = new ToolCardRepository(this.connection);
    private FavorTokenRepository favorTokenRepository = new FavorTokenRepository(this.connection);

    public Lathekin(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public void use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Player player, Game game, Object message) throws SQLException {
        @SuppressWarnings("unchecked")
        List<Square[]> messageList = (List<Square[]>) message;

        Square[] squares = messageList.get(0);
        Square[] newSquares = messageList.get(1);

        ArrayList<Die> dice = new ArrayList<>();

        for (int i = 0; i < squares.length; i++) {
            newSquares[i].setDie(squares[i].getDie());
            squares[i].setDie(null);

            patternCard.replaceSquare(squares[i], newSquares[i]);

            dice.add(squares[i].getDie());
        }

        this.incrementCost();

        FavorToken favorToken = player.getNonAffectedFavorToken();
        favorToken.setToolCard(this);

        favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), false);
        toolCardRepository.addAffectedToolCard(this, dice, game.getId());
    }
}
