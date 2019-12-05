package sagrada.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.lang.reflect.Method;
import java.util.function.Function;

public class BackButtonController {
    @FXML
    private Button btnBack;

    private final Method method;

    public BackButtonController(Method method) {
        this.method = method;
    }

    @FXML
    protected void initialize() {

    }

    public void btnClick() {

    }

}
