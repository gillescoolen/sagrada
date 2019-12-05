package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.lang.reflect.Method;
import java.util.function.Function;

public class BackButtonController {
    @FXML
    private Button btnBack;

    private final Runnable method;

    public BackButtonController(Runnable method) {
        this.method = method;
    }

    @FXML
    protected void initialize() {
        this.btnBack.setOnMouseClicked(c -> btnClick());
    }

    public void btnClick() {
        this.method.run();
    }
}
