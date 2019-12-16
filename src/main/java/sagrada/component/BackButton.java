package sagrada.component;

import javafx.fxml.FXMLLoader;
import sagrada.controller.BackButtonController;

import java.io.IOException;

public class BackButton extends FXMLLoader {
    public BackButton(Runnable runnable) throws IOException {
        this.setLocation(getClass().getResource("/views/components/backButton.fxml"));
        this.setController(new BackButtonController(runnable));
    }
}
