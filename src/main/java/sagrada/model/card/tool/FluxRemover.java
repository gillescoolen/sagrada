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

        dieRepository.removeGameDie(game.getId(), die);
        diceBag.put(die);

        Die newDie = diceBag.getRandomDice(1).get(0);
        newDie.setValue(newValue);

        game.updateDraftPool(die, newDie);

        this.incrementCost();

        ArrayList<Die> dice = new ArrayList<>();
        dice.add(newDie);

        var round = gameRepository.getCurrentRound(game.getId());
        dieRepository.addGameDice(game.getId(), round, dice);

        FavorToken favorToken = player.getNonAffectedFavorToken();
        favorToken.setToolCard(this);

        favorTokenRepository.updateFavorToken(favorToken, this.getId(), roundTrack.getCurrent(), false, game.getId());

        return true;
    }
}
