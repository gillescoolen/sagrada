package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import sagrada.database.DatabaseConnection;
import sagrada.database.repositories.DieRepository;
import sagrada.database.repositories.GameRepository;
import sagrada.model.DraftPool;
import sagrada.model.Game;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class DraftPoolController implements Consumer<DraftPool> {
    @FXML
    private HBox draftPoolBox;

    private DraftPool draftPool;
    private final Game game;
    private final GameController gameController;
    private ScheduledExecutorService ses;

    public DraftPoolController(DraftPool draftPool, Game game, GameController gameController, DatabaseConnection connection) {
        this.draftPool = draftPool;
        this.game = game;
        this.gameController = gameController;

        this.draftPool.observe(this);

        var dieRepository = new DieRepository(connection);
        var gameRepository = new GameRepository(connection);

        Runnable draftPoolTimer = () -> {
            try {
                var draftedDice = dieRepository.getDraftPoolDice(this.game.getId(), gameRepository.getCurrentRound(this.game.getId()));
                this.game.addDiceInDraftPool(draftedDice);
                this.drawDice();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };

        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        ses.scheduleAtFixedRate(draftPoolTimer, 0, 1000, TimeUnit.MILLISECONDS);
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
                        if (this.draftPool == null) {
                            this.ses.shutdown();
                        }
                        this.draftPoolBox.getChildren().add(loader.load());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
