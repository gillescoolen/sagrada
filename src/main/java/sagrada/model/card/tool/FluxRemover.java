package sagrada.model.card.tool;

import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.DieRepository;
import sagrada.database.repositories.FavorTokenRepository;
import sagrada.database.repositories.GameRepository;
import sagrada.model.*;

import java.sql.SQLException;
import java.util.ArrayList;

public final class FluxRemover extends ToolCard {
    private FavorTokenRepository favorTokenRepository = new FavorTokenRepository(this.connection);
    private GameRepository gameRepository = new GameRepository(this.connection);
    private DieRepository dieRepository = new DieRepository(this.connection);

    public FluxRemover(int id, String name, String description, DatabaseConnection connection) {
        super(id, name, description, connection);
    }

    @Override
    public boolean use(DraftPool draftPool, DiceBag diceBag, PatternCard patternCard, RoundTrack roundTrack, Player player, Game game, Object message) throws SQLException {
        Object[] messages = (Object[]) message;
        Die die = (Die) messages[0];
        Integer newValue = (Integer) messages[1];

        this.dieRepository.removeGameDie(game.getId(), die);
        diceBag.put(die);

        Die newDie = diceBag.getRandomDice(1).get(0);
        newDie.setValue(newValue);

        game.updateDraftPool(die, newDie);


        ArrayList<Die> dice = new ArrayList<>();
        dice.add(newDie);

        var round = this.gameRepository.getCurrentRound(game.getId());
        dieRepository.addGameDice(game.getId(), round, dice);

        if (this.getCost() == 1) {
            FavorToken favorToken = player.getNonAffectedFavorToken(this.favorTokenRepository, game);
            favorToken.setToolCard(this);

            this.favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), false, game.getId());
        } else {
            FavorToken favorToken = player.getNonAffectedFavorToken(this.favorTokenRepository, game);
            favorToken.setToolCard(this);

            favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), false, game.getId());

            FavorToken favorToken1 = player.getNonAffectedFavorToken(this.favorTokenRepository, game);
            favorToken1.setToolCard(this);

            favorTokenRepository.updateFavorToken(favorToken1, this.getId(), roundTrack.getCurrent(), false, game.getId());
        }
        this.incrementCost();

        return true;
    }
}
