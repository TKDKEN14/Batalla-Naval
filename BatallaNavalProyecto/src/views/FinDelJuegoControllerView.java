package views;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import logic.Juego;

public class FinDelJuegoControllerView implements Initializable {

    @FXML
    private Button jugarDeNuevoBoton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Juego.getInstance().reiniciarJuego();
    }

    @FXML
    public void onJugarDeNuevoButton() throws IOException {
        Stage stageActual = (Stage) jugarDeNuevoBoton.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/tablero.fxml"));

        Scene tableroScene = new Scene(fxmlLoader.load());
        // Configurar la nueva escena en el escenario actual
        stageActual.setScene(tableroScene);
        stageActual.setTitle("Batalla Naval - Tablero");
    }
}
