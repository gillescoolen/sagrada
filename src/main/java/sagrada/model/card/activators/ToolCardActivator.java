package sagrada.model.card.activators;

import sagrada.controller.GameController;
import sagrada.model.ToolCard;

public abstract class ToolCardActivator {
    protected final GameController controller;
    protected final ToolCard toolCard;

    public ToolCardActivator(GameController gameController, ToolCard toolCard) {
        this.controller = gameController;
        this.toolCard = toolCard;
    }

    public abstract void activate();
}
