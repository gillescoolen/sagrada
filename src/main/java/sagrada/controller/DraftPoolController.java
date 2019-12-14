package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import sagrada.model.DraftPool;
import sagrada.model.Game;

import java.io.IOException;
import java.util.function.Consumer;

public class DraftPoolController implements Consumer<DraftPool> {
    @FXML
    private HBox draftPoolBox;

    private DraftPool draftPool;
    private final Game game;
    private final GameController gameController;

    public DraftPoolController(DraftPool draftPool, Game game, GameController gameController) {
        this.draftPool = draftPool;
        this.game = game;
        this.gameController = gameController;
    }

    @FXML
    protected void initialize() {
        this.drawDice();
    }

    @Override
    public void accept(DraftPool draftPool) {
        this.draftPool = draftPool;
        this.drawDice();
    }

    private void drawDice() {
        if (this.draftPoolBox != null && this.draftPool != null) {
            Platform.runLater(() -> this.draftPoolBox.getChildren().clear());

            var diceCount = this.game.getDiceCount();

            for (int i = 0; i < diceCount; ++i) {
                var loader = new FXMLLoader(getClass().getResource("/views/game/draftPoolDie.fxml"));

                if (i < this.draftPool.getDice().size()) {
                    var die = this.draftPool.getDice().get(i);
                    loader.setController(new DraftPoolDieController(die, this.gameController));
                }

                Platform.runLater(() -> {
                    try {
                        this.draftPoolBox.getChildren().add(loader.load());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
