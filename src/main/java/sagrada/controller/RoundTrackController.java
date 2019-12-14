package sagrada.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import sagrada.model.RoundTrack;

import java.io.IOException;
import java.util.function.Consumer;

public class RoundTrackController implements Consumer<RoundTrack> {
    @FXML
    private HBox roundTrackContainer;

    private RoundTrack roundTrack;

    public RoundTrackController(RoundTrack roundTrack) {
        this.roundTrack = roundTrack;
        roundTrack.observe(this);
    }

    @FXML
    protected void initialize() {
        this.fillRoundTrackContainer();
    }

    @Override
    public void accept(RoundTrack roundTrack) {
        this.roundTrack = roundTrack;
        this.fillRoundTrackContainer();
    }

    private void fillRoundTrackContainer() {
        if (this.roundTrackContainer != null) {
            Platform.runLater(() -> {
                this.roundTrackContainer.getChildren().clear();
            });

            for (var dieEntry : this.roundTrack.getTrack().entrySet()) {
                var loader = new FXMLLoader(getClass().getResource("/views/game/roundTrackDie.fxml"));

                if (dieEntry.getValue().getNumber() != null && dieEntry.getValue().getColor() != null) {
                    loader.setController(new RoundTrackDieController(dieEntry.getValue()));
                } else {
                    loader.setController(new RoundTrackDieController(dieEntry.getKey()));
                }

                Platform.runLater(() -> {
                    try {
                        this.roundTrackContainer.getChildren().add(loader.load());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }
}
