package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.DieRepository;
import sagrada.database.repositories.FavorTokenRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.ArrayList;

public final class FluxBrush extends ToolCard {
    private FavorTokenRepository favorTokenRepository = new FavorTokenRepository(this.connection);
    private DieRepository dieRepository = new DieRepository(this.connection);

    public FluxBrush(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public boolean use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Player player, Game game, Object message) throws SQLException {
        var die = (Die) message;
        var newDie = new Die(die.getNumber(), die.getColor());

        newDie.setValue(die.getValue());

        newDie.roll();

        game.updateDraftPool(die, newDie);


        ArrayList<Die> dice = new ArrayList<>();
        dice.add(newDie);

        dieRepository.updateGameDie(game.getId(), newDie);

        if (this.getCost() == 1) {
            FavorToken favorToken = player.getNonAffectedFavorToken(this.favorTokenRepository, game);
            favorToken.setToolCard(this);

            this.favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), false, game.getId());
        } else {
            FavorToken favorToken = player.getNonAffectedFavorToken(this.favorTokenRepository, game);
            favorToken.setToolCard(this);

            this.favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), false, game.getId());

            FavorToken favorToken1 = player.getNonAffectedFavorToken(this.favorTokenRepository, game);
            favorToken1.setToolCard(this);

            this.favorTokenRepository.updateFavorToken(favorToken1, this.getId(), roundTrack.getCurrent(), false, game.getId());
        }

        this.incrementCost();

        return true;
    }
}
