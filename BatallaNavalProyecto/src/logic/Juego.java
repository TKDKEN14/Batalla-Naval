package logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Juego {

    private static Juego instance = null;

    private Fase fase = Fase.DESPLIGUE;
    private Tablero tablero;
    private Tablero tableroComputadora;
    private Computadora computadora;

    private Map<String, Integer> cantidadesBarcos = new HashMap<>();
    private Queue<Barco> barcosDisponiblesDespliegue = new LinkedList<Barco>();

    private Queue<Barco> barcosDisponiblesAtaque = new LinkedList<Barco>();
    private Queue<Barco> barcosDisponiblesAtaqueComputadora = new LinkedList<Barco>();

    private ArrayList<Barco> barcosJugador = new ArrayList<Barco>();
    private ArrayList<Barco> barcosComputadora = new ArrayList<Barco>();

    public int ataquesSeguidosJugador = 0;
    public int ataquesSeguidosComputadora = 0;

    public int mayorAtaqueJugador = 0;
    public int mayorAtaqueComputadora = 0;

    private Turno turno;

    private Juego() {
        int boardColumns = 10;
        int boardRows = 10;

        this.tablero = new Tablero(boardColumns, boardRows);
        this.tableroComputadora = new Tablero(boardColumns, boardRows);

        this.computadora = new Computadora();
        Barco[][] tableroBarcosComputadora = computadora.getTablero();

        ArrayList<Integer> insertados = new ArrayList<>();

        for (int x = 0; x < boardColumns; x++) {
            for (int y = 0; y < boardRows; y++) {
                Barco barco = tableroBarcosComputadora[x][y];
                if (barco == null) {
                    continue;
                }

                tableroComputadora.getCasilla(x, y).setBarco(barco);

                if (insertados.contains(barco.id)) {
                    continue;
                }

                barcosDisponiblesAtaqueComputadora.add(barco);
                barcosComputadora.add(barco);
                insertados.add(barco.id);

            }
        }

        cantidadesBarcos.put("Portaaviones", 1);
        cantidadesBarcos.put("Acorazado", 1);
        cantidadesBarcos.put("Destructor", 2);
        cantidadesBarcos.put("Fragata", 3);
        cantidadesBarcos.put("Submarino", 5);

        for (Map.Entry<String, Integer> entry : cantidadesBarcos.entrySet()) {
            String shipType = entry.getKey();
            int count = entry.getValue();

            for (int j = 0; j < count; j++) {
                switch (shipType) {
                    case "Portaaviones":
                        barcosDisponiblesDespliegue.add(new Portaaviones(Direccion.HORIZONTAL));
                        break;
                    case "Acorazado":
                        barcosDisponiblesDespliegue.add(new Acorazado(Direccion.HORIZONTAL));
                        break;
                    case "Destructor":
                        barcosDisponiblesDespliegue.add(new Destructor(Direccion.HORIZONTAL));
                        break;
                    case "Fragata":
                        barcosDisponiblesDespliegue.add(new Fragata(Direccion.HORIZONTAL));
                        break;
                    case "Submarino":
                        barcosDisponiblesDespliegue.add(new Submarino(Direccion.HORIZONTAL));
                        break;
                }
            }
        }
    }

    public void colocarBarco(int x, int y, Direccion direccion) {
        if (fase != Fase.DESPLIGUE) {
            return;
        }

        ArrayList<Coord> rectangulosBarco = getPosicionesBarcoActual(x, y, direccion);

        Barco currentBarco = barcosDisponiblesDespliegue.peek();

        if (rectangulosBarco.size() < currentBarco.getCasillas() || hayColision(rectangulosBarco)) {
            return;
        }

        currentBarco.id = Barco.parseId(x, y);

        for (Coord coord : rectangulosBarco) {
            Casilla casilla = this.tablero.getCasilla(coord.x, coord.y);
            casilla.setBarco(currentBarco);
        }

        barcosDisponiblesAtaque.add(currentBarco);
        barcosJugador.add(currentBarco);
        barcosDisponiblesDespliegue.poll();

        if (barcosDisponiblesDespliegue.size() == 0) {
            fase = Fase.ESPERA;
        }
    }

    public boolean hayColision(ArrayList<Coord> posiciones) {
        for (Coord coord : posiciones) {
            Casilla casilla = tablero.getCasilla(coord.x, coord.y);
            if (casilla.getTieneBarco()) {
                return true;
            }
        }

        return false;
    }

    public ArrayList<Coord> getPosicionesBarcoActual(int posX, int posY, Direccion pointerDireccion) {
        ArrayList<Coord> rectangulosBarco = new ArrayList<Coord>();

        Barco currentBarco = barcosDisponiblesDespliegue.peek();

        if (pointerDireccion == Direccion.HORIZONTAL) {
            for (int c = 0; c < currentBarco.getCasillas(); c++) {
                int x = posX + c;

                if (x < 0 || x >= tablero.getAncho()) {
                    continue;
                }

                rectangulosBarco.add(new Coord(x, posY));
            }
        } else if (pointerDireccion == Direccion.VERTICAL) {
            for (int c = 0; c < currentBarco.getCasillas(); c++) {
                int y = posY + c;
                if (y < 0 || y >= tablero.getAlto()) {
                    continue;
                }
                rectangulosBarco.add(new Coord(posX, y));
            }
        }

        return rectangulosBarco;
    }

    public void ataqueEnemigo() {
        this.verificarBarcos();

        if (barcosDisponiblesAtaqueComputadora.size() == 0) {
            return;
        }

        Barco currentBarco = barcosDisponiblesAtaqueComputadora.peek();

        if (currentBarco == null) {
            return;
        }

        if (currentBarco.getDisparosRestantes() == 0 && barcosDisponiblesAtaqueComputadora.size() > 1) {
            barcosDisponiblesAtaqueComputadora.poll();
            currentBarco = barcosDisponiblesAtaqueComputadora.peek();
        }

        Coord coordAtaque = this.computadora.getAtaqueAleatorio();
        Casilla casillaAtacada = tablero.getCasilla(coordAtaque.x, coordAtaque.y);

        if (casillaAtacada.getTieneBarco()) {
            ataquesSeguidosComputadora++;
            if (ataquesSeguidosComputadora > mayorAtaqueComputadora) {
                mayorAtaqueComputadora = ataquesSeguidosComputadora;
            }

        } else {
            ataquesSeguidosComputadora = 0;
        }

        casillaAtacada.setFueAtacada(true);
        currentBarco.setDisparosRestantes(currentBarco.getDisparosRestantes() - 1);

        if (currentBarco.getDisparosRestantes() == 0) {
            barcosDisponiblesAtaqueComputadora.poll();
        }

    }

    public void ataqueJugador(int x, int y) {
        this.verificarBarcos();

        if (barcosDisponiblesAtaque.size() == 0) {
            return;
        }

        Barco currentBarco = barcosDisponiblesAtaque.peek();

        if (currentBarco == null) {
            return;
        }

        if (currentBarco.getDisparosRestantes() == 0 && barcosDisponiblesAtaque.size() > 1) {
            barcosDisponiblesAtaque.poll();
            currentBarco = barcosDisponiblesAtaque.peek();
        }

        Casilla casillaAtacada = tableroComputadora.getCasilla(x, y);

        if (casillaAtacada.getTieneBarco()) {
            ataquesSeguidosJugador++;
            if (ataquesSeguidosJugador > mayorAtaqueJugador) {
                mayorAtaqueJugador = ataquesSeguidosJugador;
            }
        } else {
            ataquesSeguidosJugador = 0;
        }

        casillaAtacada.setFueAtacada(true);
        currentBarco.setDisparosRestantes(currentBarco.getDisparosRestantes() - 1);

        if (currentBarco.getDisparosRestantes() == 0) {
            barcosDisponiblesAtaque.poll();
        }

    }

    public void verificarBarcos() {
        for (Barco barco : barcosDisponiblesAtaque) {
            if (barco.getPiezasIntactas() == 0) {
                barcosDisponiblesAtaque.remove(barco);
            }
        }

        for (Barco barco : barcosDisponiblesAtaqueComputadora) {
            if (barco.getPiezasIntactas() == 0) {
                barcosDisponiblesAtaqueComputadora.remove(barco);
            }
        }
    }

    public ArrayList<Barco> getBarcosComputadora() {
        return barcosComputadora;
    }

    public ArrayList<Barco> getBarcosJugador() {
        return barcosJugador;
    }

    public static Juego getInstance() {
        if (instance == null) {
            instance = new Juego();
        }

        return instance;
    }

    public Map<String, Integer> getCantidadesBarcos() {
        return cantidadesBarcos;
    }

    public Tablero getTablero() {
        return tablero;
    }

    public Tablero getTableroComputadora() {
        return tableroComputadora;
    }

    public Fase getFase() {
        return fase;
    }

    public Turno getTurno() {
        return turno;
    }

    public Queue<Barco> getBarcosDisponiblesAtaque() {
        return barcosDisponiblesAtaque;
    }

    public Queue<Barco> getBarcosDisponiblesAtaqueComputadora() {
        return barcosDisponiblesAtaqueComputadora;
    }

    public void cambiarTurno() {
        if (turno == Turno.COMPUTADORA) {
            turno = Turno.JUGADOR;
        } else {
            turno = Turno.COMPUTADORA;
        }
    }

    public void setFase(Fase nuevaFase) {
        if (fase == Fase.ESPERA && nuevaFase == Fase.ATAQUE) {
            // TURNO ALEATORIO
            turno = Turno.values()[(int) (Math.random() * Turno.values().length)];
        }

        fase = nuevaFase;
    }

    public Queue<Barco> getBarcosDisponibles() {
        return barcosDisponiblesDespliegue;
    }

    public int getMayorAtaqueComputadora() {
        return mayorAtaqueComputadora;
    }

    public int getMayorAtaqueJugador() {
        return mayorAtaqueJugador;
    }

    public void reiniciarJuego() {
        instance = new Juego();
    }
}
