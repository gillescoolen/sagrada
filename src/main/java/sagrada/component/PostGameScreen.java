package sagrada.component;

import javafx.fxml.FXMLLoader;
import sagrada.controller.GameController;
import sagrada.controller.PostGameController;
import sagrada.database.DatabaseConnection;
import sagrada.model.Game;

public class PostGameScreen extends FXMLLoader {
    public PostGameScreen(Game game, GameController gameController, DatabaseConnection databaseConnection) {
        this.setLocation(getClass().getResource("/views/screens/postGame.fxml"));
        this.setController(new PostGameController(game, gameController, databaseConnection));
    }
}
