package gasolinera.dominio;

/** Combustibles que puede dispensar un surtidor. */
public enum TipoCombustible {

    GASOLINA_95("Gasolina 95"),
    GASOLINA_98("Gasolina 98"),
    DIESEL("Diesel");

    private final String nombre;

    TipoCombustible(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
