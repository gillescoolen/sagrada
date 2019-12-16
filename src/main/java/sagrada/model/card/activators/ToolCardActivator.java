package sagrada.model.card.activators;

import sagrada.controller.GameController;
import sagrada.model.ToolCard;

import java.sql.SQLException;

public abstract class ToolCardActivator {
    protected final GameController controller;
    protected final ToolCard toolCard;

    public ToolCardActivator(GameController gameController, ToolCard toolCard) {
        this.controller = gameController;
        this.toolCard = toolCard;
    }

    public abstract boolean activate() throws SQLException;
}
