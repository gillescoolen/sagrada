package sagrada.model.card.activators;

import sagrada.controller.GameController;
import sagrada.model.Square;
import sagrada.model.ToolCard;

import java.sql.SQLException;
import java.util.List;

public abstract class ToolCardActivator {
    protected final GameController controller;
    protected final ToolCard toolCard;

    public ToolCardActivator(GameController gameController, ToolCard toolCard) {
        this.controller = gameController;
        this.toolCard = toolCard;
    }

    public abstract boolean activate() throws SQLException;
}
