package logic;

public abstract class Barco {
    private Direccion direccion;
    private int casillas;
    public int disparos;

    private int piezasIntactas;
    private int disparosRestantes;

    private String nombre;

    public int id;

    public Barco(Direccion direccion, int casillas, int disparos, String nombre) {
        this.id = 0;
        this.direccion = direccion;

        this.casillas = casillas;
        this.disparos = disparos;

        this.piezasIntactas = casillas;
        this.disparosRestantes = disparos;

        this.nombre = nombre;
    }

    public int getPiezasIntactas() {
        return piezasIntactas;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public int getCasillas() {
        return casillas;
    }

    public int getDisparos() {
        return disparos;
    }

    public int getDisparosRestantes() {
        return disparosRestantes;
    }

    public void setDisparosRestantes(int disparosRestantes) {
        this.disparosRestantes = disparosRestantes;
    }

    public void setPiezasIntactas(int piezasIntactas) {
        this.piezasIntactas = piezasIntactas;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public void setDisparos(int disparos) {
        this.disparos = disparos;
    }

    public String getNombre() {
        return nombre;
    }

    public static int parseId(int x, int y) {
        return Integer.parseInt(Integer.toString(x) + Integer.toString(y));
    }
}
