package logic;

public class Tablero {

    private int ancho;
    private int alto;
    private Casilla[][] casillas;

    public Tablero(int ancho, int alto) {
        this.ancho = ancho;
        this.alto = alto;
        this.casillas = new Casilla[ancho][alto];

        for (int x = 0; x < ancho; x++) {
            for (int y = 0; y < ancho; y++) {
                this.casillas[x][y] = new Casilla();
            }
        }
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public Casilla getCasilla(int x, int y) {
        return casillas[x][y];
    }
}
