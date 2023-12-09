package views;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import logic.*;

enum Ganador {
    JUGADOR,
    COMPUTADORA,
    EMPATE
}

public class TableroControllerView implements Initializable {

    @FXML
    private GridPane gridJugador;
    @FXML
    private GridPane gridEnemigo;
    @FXML
    private Label labelPortaavion;
    @FXML
    private Label labelAcorazado;
    @FXML
    private Label labelDestructor;
    @FXML
    private Label labelFragata;
    @FXML
    private Label labelSubmarino;

    @FXML
    private Label barcosJugadorRestantes;

    @FXML
    private Label barcosEnemigoRestantes;

    private Juego juego = Juego.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Map<String, Integer> cantidadesBarcos = juego.getCantidadesBarcos();

        labelPortaavion.setText(cantidadesBarcos.get("Portaaviones").toString());
        labelAcorazado.setText(cantidadesBarcos.get("Acorazado").toString());
        labelDestructor.setText(cantidadesBarcos.get("Destructor").toString());
        labelFragata.setText(cantidadesBarcos.get("Fragata").toString());
        labelSubmarino.setText(cantidadesBarcos.get("Submarino").toString());

        new TableroJugador(gridJugador);
        new TableroEnemigo(gridEnemigo);

        this.initGameTimer();
    }

    public void initGameTimer() {

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {

                if (juego.getFase() == Fase.ATAQUE) {
                    Ganador ganador = this.verificaGanador();

                    int barcosRestantesJugador = 0;
                    int barcosRestantesComputadora = 0;

                    for (Barco barco : juego.getBarcosJugador()) {
                        if (barco.getPiezasIntactas() > 0) {
                            barcosRestantesJugador++;
                        }

                    }

                    for (Barco barco : juego.getBarcosComputadora()) {
                        if (barco.getPiezasIntactas() > 0) {
                            barcosRestantesComputadora++;
                        }
                    }

                    barcosJugadorRestantes.setText(barcosRestantesJugador + "");
                    barcosEnemigoRestantes.setText(barcosRestantesComputadora + "");

                    if (ganador != null) {

                        this.stop();

                        String ventana;
                        if (ganador == Ganador.COMPUTADORA) {
                            ventana = "perdiste.fxml";
                        } else if (ganador == Ganador.JUGADOR) {
                            ventana = "ganaste.fxml";
                        } else {
                            ventana = "empate.fxml";
                        }

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + ventana));
                            Scene scene = new Scene(loader.load());

                            Stage stageActual = (Stage) labelPortaavion.getScene().getWindow();
                            stageActual.setScene(scene);
                            stageActual.setTitle("Fin Del Juego");

                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(TableroControllerView.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        } catch (IOException ex) {
                            Logger.getLogger(TableroControllerView.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                }
            }

            public Ganador verificaGanador() {
                int barcosDisponiblesJugador = juego.getBarcosDisponiblesAtaque().size();
                int barcosDisponiblesComputadora = juego.getBarcosDisponiblesAtaqueComputadora().size();

                Ganador porHundimiento = verificarGanadorPorHundimiento();

                if (porHundimiento != null && porHundimiento != Ganador.EMPATE) {
                    return porHundimiento;
                }

                if (barcosDisponiblesJugador > 0 || barcosDisponiblesComputadora > 0) {
                    return null;
                }

                Ganador porCeldas = verificarGanadorPorCeldas();
                Ganador porBarcosVivos = verificarGanadorPorBarcosVivos();
                Ganador porAtaquesSeguidos = verificaGanadorPorAtaquesSeguidos();

                if (porCeldas != Ganador.EMPATE) {
                    return porCeldas;
                } else if (porBarcosVivos != Ganador.EMPATE) {
                    return porBarcosVivos;
                } else if (porAtaquesSeguidos != Ganador.EMPATE) {
                    return porAtaquesSeguidos;
                } else {
                    return Ganador.EMPATE;
                }
            }

            public Ganador verificarGanadorPorHundimiento() {
                boolean userWin = true;
                boolean computerWin = true;

                for (Barco barco : juego.getBarcosComputadora()) {
                    if (barco.getPiezasIntactas() > 0) {
                        userWin = false;
                    }
                }

                for (Barco barco : juego.getBarcosJugador()) {
                    if (barco.getPiezasIntactas() > 0) {
                        computerWin = false;
                    }
                }

                if (userWin) {
                    return Ganador.JUGADOR;
                } else if (computerWin) {
                    return Ganador.COMPUTADORA;
                } else if (!userWin && !computerWin) {
                    return null;
                } else {
                    return Ganador.EMPATE;
                }
            }

            public Ganador verificarGanadorPorCeldas() {

                int celdasJugador = 0;
                int celdasComputadora = 0;

                for (Barco barco : juego.getBarcosJugador()) {
                    celdasJugador += barco.getPiezasIntactas();
                }

                for (Barco barco : juego.getBarcosComputadora()) {
                    celdasComputadora += barco.getPiezasIntactas();
                }

                if (celdasJugador > celdasComputadora) {
                    return Ganador.JUGADOR;
                } else if (celdasComputadora > celdasJugador) {
                    return Ganador.COMPUTADORA;
                } else {
                    return Ganador.EMPATE;
                }
            }

            public Ganador verificarGanadorPorBarcosVivos() {

                int vivosJugador = 0;
                int vivosComputadora = 0;

                for (Barco barco : juego.getBarcosJugador()) {
                    if (barco.getPiezasIntactas() > 0) {
                        vivosJugador++;
                    }
                }

                for (Barco barco : juego.getBarcosComputadora()) {
                    if (barco.getPiezasIntactas() > 0) {
                        vivosComputadora++;
                    }
                }

                if (vivosJugador > vivosComputadora) {
                    return Ganador.JUGADOR;
                } else if (vivosComputadora > vivosJugador) {
                    return Ganador.COMPUTADORA;
                } else {
                    return Ganador.EMPATE;
                }
            }

            public Ganador verificaGanadorPorAtaquesSeguidos() {
                int seguidosJugador = juego.getMayorAtaqueJugador();
                int seguidosComputadora = juego.getMayorAtaqueComputadora();

                if (seguidosJugador > seguidosComputadora) {
                    return Ganador.JUGADOR;
                } else if (seguidosComputadora > seguidosJugador) {
                    return Ganador.COMPUTADORA;
                } else {
                    return Ganador.EMPATE;
                }
            }
        };

        timer.start();
    }

    @FXML
    public void onAtacarButtonClick() {
        Fase fase = juego.getFase();
        if (fase == Fase.ESPERA) {
            juego.setFase(Fase.ATAQUE);
        }
    }

    @FXML
    public void onAbandonarButtonClick() {

        Stage stage = (Stage) gridJugador.getScene().getWindow();
        stage.close();

    }

}
