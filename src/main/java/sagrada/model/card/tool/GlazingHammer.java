package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.DieRepository;
import sagrada.database.repositories.FavorTokenRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.List;

public final class GlazingHammer extends ToolCard {
    private FavorTokenRepository favorTokenRepository = new FavorTokenRepository(this.connection);
    private DieRepository dieRepository = new DieRepository(this.connection);

    public GlazingHammer(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public boolean use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Player player, Game game, Object message) throws SQLException {
        if (game.getSelectedDie() != null || player.getIfSecondTurn(game.getPlayers().size())) {
            return false;
        }

        draftPool.throwDice();

        var pool = draftPool.getDice();

        pool.forEach(die -> {
            try {
                dieRepository.updateGameDie(game.getId(), die);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        this.incrementCost();

        FavorToken favorToken = player.getNonAffectedFavorToken();
        favorToken.setToolCard(this);
        game.addDiceInDraftPool(draftPool.getDice());

        this.favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), false, game.getId());

        return true;
    }
}
