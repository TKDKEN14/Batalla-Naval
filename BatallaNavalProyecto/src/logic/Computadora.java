package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Computadora {

    private List<Coord> ataquesCoordenadas = new ArrayList<>();
    private Barco[][] tablero = new Barco[10][10];

    private int portaaviones = 0;
    private int acorazado = 0;
    private int destructores = 0;
    private int fragatas = 0;
    private int submarinos = 0;

    public Computadora() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                tablero[i][j] = null;
            }
        }

        propagarTablero();
    }

    public Barco[][] getTablero() {
        return tablero;
    }

    public Coord atacarAleatoriamente() {

        Coord coord = generarCoordenadas();

        while (verificarCoordenadaDeAtaque(coord.x, coord.y)) {
            coord.x = (int) (Math.random() * 10);
            coord.y = (int) (Math.random() * 10);
        }

        ataquesCoordenadas.add(coord);
        return coord;
    }

    private void propagarTablero() {

        int id = 0;

        while (portaaviones == 0) {
            Portaaviones barco = new Portaaviones(obtenerDireccionAleatoria());
            Coord coords_iniciales = generarCoordenadas();
            boolean coordsFinalesValidacion = verificarPosicionBarcoFinal(barco, coords_iniciales);

            if (coordsFinalesValidacion) {
                portaaviones++;
                barco.id = id;
                id++;
                agregarBarco(barco, coords_iniciales.x, coords_iniciales.y);
            }
        }

        while (acorazado == 0) {
            Acorazado barco = new Acorazado(obtenerDireccionAleatoria());
            Coord coords_iniciales = generarCoordenadas();
            boolean coordsFinalesValidacion = verificarPosicionBarcoFinal(barco, coords_iniciales);

            if (coordsFinalesValidacion) {
                acorazado++;
                barco.id = id;
                id++;
                agregarBarco(barco, coords_iniciales.x, coords_iniciales.y);
            }
        }

        while (destructores < 2) {
            Destructor barco = new Destructor(obtenerDireccionAleatoria());
            Coord coords_iniciales = generarCoordenadas();
            boolean coordsFinalesValidacion = verificarPosicionBarcoFinal(barco, coords_iniciales);

            if (coordsFinalesValidacion) {
                destructores++;
                barco.id = id;
                id++;
                agregarBarco(barco, coords_iniciales.x, coords_iniciales.y);
            }
        }

        while (fragatas < 3) {
            Fragata barco = new Fragata(obtenerDireccionAleatoria());
            Coord coords_iniciales = generarCoordenadas();
            boolean coordsFinalesValidacion = verificarPosicionBarcoFinal(barco, coords_iniciales);

            if (coordsFinalesValidacion) {
                fragatas++;
                barco.id = id;
                id++;
                agregarBarco(barco, coords_iniciales.x, coords_iniciales.y);
            }
        }

        while (submarinos < 5) {
            Submarino barco = new Submarino(obtenerDireccionAleatoria());
            Coord coords_iniciales = generarCoordenadas();
            boolean coordsFinalesValidacion = verificarPosicionBarcoFinal(barco, coords_iniciales);

            if (coordsFinalesValidacion) {
                submarinos++;
                barco.id = id;
                id++;
                agregarBarco(barco, coords_iniciales.x, coords_iniciales.y);
            }
        }

    }

    public boolean verificarPosicionBarcoFinal(Barco barco, Coord coords_propuestas) {

        int fila = coords_propuestas.x;
        int columna = coords_propuestas.y;

        if (barco.getDireccion() == Direccion.HORIZONTAL) {
            int columna_final = columna + barco.getCasillas();

            for (int columna_index = columna; columna_index < columna_final; columna_index++) {
                if (!posicionValidaParaBarcos(fila, columna_index)) {
                    return false;
                }
            }

        } else {
            int fila_final = fila + barco.getCasillas();

            for (int fila_index = fila; fila_index < fila_final; fila_index++) {
                if (!posicionValidaParaBarcos(fila_index, columna)) {
                    return false;
                }
            }
        }

        return true;

    }

    public boolean posicionValidaParaBarcos(int fila, int columna) {

        if (fila >= 10 || columna >= 10) {
            return false;

        } else {
            return tablero[fila][columna] == null;
        }
    }

    public void agregarBarco(Barco barco, int fila, int columna) {

        if (barco.getDireccion() == Direccion.HORIZONTAL) {
            int columna_final = columna + barco.getCasillas();

            for (int columna_index = columna; columna_index < columna_final; columna_index++) {
                tablero[fila][columna_index] = barco;
            }

        } else { // Vertical
            int fila_final = fila + barco.getCasillas();

            for (int fila_index = fila; fila_index < fila_final; fila_index++) {
                tablero[fila_index][columna] = barco;
            }
        }
    }

    Direccion obtenerDireccionAleatoria() {
        Direccion[] direcciones = Direccion.values();
        Random random = new Random();
        int index = random.nextInt(direcciones.length);
        return direcciones[index];
    }

    public Coord getAtaqueAleatorio() {

        Coord coord = generarCoordenadas();

        while (verificarCoordenadaDeAtaque(coord.x, coord.y)) {
            coord.x = (int) (Math.random() * 10);
            coord.y = (int) (Math.random() * 10);
        }

        ataquesCoordenadas.add(coord);
        return coord;
    }

    private Coord generarCoordenadas() {

        int fila = (int) (Math.random() * 10);
        int columna = (int) (Math.random() * 10);

        return new Coord(fila, columna);
    }

    private boolean verificarCoordenadaDeAtaque(int fila, int columna) {
        if (ataquesCoordenadas.size() >= 10 * 10) {
            return false;
        }

        for (Coord coordenada : ataquesCoordenadas) {
            if (coordenada.x == fila && coordenada.y == columna) {
                return true;
            }
        }
        return false;
    }

}
