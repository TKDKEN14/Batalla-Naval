package logic;

public class Casilla {

    private Boolean fueAtacada;
    private Boolean tieneBarco;
    private Barco barco;

    public Casilla(Boolean fueAtacada, Boolean tieneBarco) {
        this.fueAtacada = fueAtacada;
        this.tieneBarco = tieneBarco;
    }

    public Casilla() {
        this.fueAtacada = false;
        this.tieneBarco = false;
    }

    public Boolean getFueAtacada() {
        return fueAtacada;
    }

    public Boolean getTieneBarco() {
        return tieneBarco;
    }

    public void setFueAtacada(Boolean fueAtacada) {
        this.fueAtacada = fueAtacada;

        if (this.barco != null) {
            this.barco.setPiezasIntactas(this.barco.getPiezasIntactas() - 1);
        }

    }

    public void setBarco(Barco barco) {
        this.barco = barco;
        this.tieneBarco = true;
    }

    public Barco getBarco() {
        return this.barco;
    }
}
