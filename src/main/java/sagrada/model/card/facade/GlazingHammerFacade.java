package sagrada.model.card.facade;

import sagrada.controller.GameController;
import sagrada.model.Game;
import sagrada.model.Player;

public class GlazingHammerFacade implements ToolCardFacade {
    @Override
    public void use(GameController controller) {
        Player player = controller.getPlayer();
        Game game = controller.getGame();
        game.getToolCards().stream()
                .filter(c -> c.getName().equals("Loodhamer"))
                .findFirst()
                .ifPresent(card -> card.use(game.getDraftPool(), player.getDiceBag(), player.getPatternCard(), game.getRoundTrack(), null));
    }
}
