package sagrada.component;

import javafx.fxml.FXMLLoader;
import sagrada.controller.LobbyController;
import sagrada.database.DatabaseConnection;
import sagrada.model.Account;

public class LobbyScreen extends FXMLLoader {
    public LobbyScreen(DatabaseConnection databaseConnection, Account account) {
        this.setLocation(getClass().getResource("/views/screens/lobby.fxml"));
        this.setController(new LobbyController(databaseConnection, account));
    }
}
