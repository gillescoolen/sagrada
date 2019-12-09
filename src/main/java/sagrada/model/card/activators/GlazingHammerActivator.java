package sagrada.model.card.activators;

import sagrada.controller.GameController;
import sagrada.model.Game;
import sagrada.model.Player;
import sagrada.model.ToolCard;

public final class GlazingHammerActivator extends ToolCardActivator {
    GlazingHammerActivator(GameController gameController, ToolCard toolCard) {
        super(gameController, toolCard);
    }

    @Override
    public void activate() {
        Player player = this.controller.getPlayer();
        Game game = this.controller.getGame();
        this.toolCard.use(game.getDraftPool(), player.getDiceBag(), player.getPatternCard(), game.getRoundTrack(), null);
    }
}
