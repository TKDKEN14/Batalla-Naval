package views;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import logic.Barco;
import logic.Casilla;
import logic.Coord;
import logic.Direccion;
import logic.Fase;
import logic.Juego;
import logic.Tablero;
import logic.Turno;

public class TableroJugador {
    GridPane gridJugador;

    private int pointerX = -1;
    private int pointerY = -1;
    private Direccion pointerDireccion = Direccion.HORIZONTAL;

    private Juego juego = Juego.getInstance();

    TableroJugador(GridPane gridJugador) {
        this.gridJugador = gridJugador;
        Tablero tablero = juego.getTablero();

        double gridWidth = gridJugador.getPrefWidth();
        double gridHeight = gridJugador.getPrefHeight();

        int boardColumns = tablero.getAncho();
        int boardRows = tablero.getAlto();

        gridJugador.setAlignment(Pos.CENTER);

        for (int x = 0; x < boardColumns; x++) {
            for (int y = 0; y < boardRows; y++) {
                final int finalX = x;
                final int finalY = y;

                Rectangle rectangle = new Rectangle();
                rectangle.setWidth(gridWidth / boardColumns);
                rectangle.setHeight(gridHeight / boardRows);

                rectangle.setFill(javafx.scene.paint.Color.WHITE);
                rectangle.setStroke(javafx.scene.paint.Color.BLACK);

                rectangle.setOnMouseEntered(e -> this.handleMouseEntered(rectangle, finalX, finalY));
                rectangle.setOnMouseExited(e -> this.handleMouseExited(rectangle, finalX, finalY));
                rectangle.setOnMouseClicked(e -> {
                    if (e.getButton() == MouseButton.PRIMARY) {
                        this.handleMouseClickPrimary();
                    } else if (e.getButton() == MouseButton.SECONDARY) {
                        this.handleMouseClickSecondary();
                    }
                });

                gridJugador.add(rectangle, x, y);
            }
        }

        this.initAnimationTimer();
    }

    private void handleMouseEntered(Rectangle rectangulo, int x, int y) {
        this.pointerX = x;
        this.pointerY = y;
    }

    private void handleMouseExited(Rectangle rectangulo, int x, int y) {
        this.pointerX = -1;
        this.pointerY = -1;
    }

    private void handleMouseClickPrimary() {
        juego.colocarBarco(pointerX, pointerY, pointerDireccion);
    }

    private void handleMouseClickSecondary() {
        if (pointerDireccion == Direccion.HORIZONTAL) {
            pointerDireccion = Direccion.VERTICAL;
        } else if (pointerDireccion == Direccion.VERTICAL) {
            pointerDireccion = Direccion.HORIZONTAL;
        }
    }

    private void initAnimationTimer() {
        AnimationTimer animationTimer = new AnimationTimer() {
            public void handle(long now) {

                Fase fase = juego.getFase();
                Turno turno = juego.getTurno();

                if (fase == Fase.ATAQUE && turno == Turno.COMPUTADORA) {
                    juego.ataqueEnemigo();
                    juego.cambiarTurno();
                }

                Tablero tablero = juego.getTablero();

                for (int x = 0; x < tablero.getAncho(); x++) {
                    for (int y = 0; y < tablero.getAlto(); y++) {
                        Casilla casilla = tablero.getCasilla(x, y);
                        Rectangle rectangulo = (Rectangle) gridJugador.getChildren().get(x * tablero.getAlto() + y);

                        if (casilla.getTieneBarco() && casilla.getFueAtacada()) {
                            rectangulo.setFill(javafx.scene.paint.Color.RED);
                        } else if (casilla.getFueAtacada()) {
                            rectangulo.setFill(javafx.scene.paint.Color.YELLOW);
                        } else if (casilla.getTieneBarco()) {
                            rectangulo.setFill(javafx.scene.paint.Color.BLUE);
                        } else {
                            rectangulo.setFill(javafx.scene.paint.Color.WHITE);
                        }

                    }
                }

                if (pointerX != -1 && pointerY != -1 && fase == Fase.DESPLIGUE) {
                    this.dibujarColocacionBarcos();
                }

            }

            public void dibujarColocacionBarcos() {
                ObservableList<Node> gridChildren = gridJugador.getChildren();
                Barco currentBarco = juego.getBarcosDisponibles().peek();
                Color color = Color.LIGHTBLUE;
                ArrayList<Coord> rectangulosBarco = juego.getPosicionesBarcoActual(pointerX, pointerY,
                        pointerDireccion);

                if (rectangulosBarco.size() < currentBarco.getCasillas()) {
                    color = Color.LIGHTCORAL;
                }

                if (juego.hayColision(rectangulosBarco)) {
                    color = Color.LIGHTCORAL;
                }

                for (int i = 0; i < rectangulosBarco.size(); i++) {
                    Coord coord = rectangulosBarco.get(i);
                    Rectangle rectangulo = (Rectangle) gridChildren
                            .get(coord.x * juego.getTablero().getAlto() + coord.y);
                    rectangulo.setFill(color);
                }
            }
        };

        animationTimer.start();
    }
}
