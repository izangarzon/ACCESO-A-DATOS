package gasolinera.dominio;

/** Condiciones concretas de un periodo de función. Cada tipo tiene su propia subclase. */
public abstract class CondicionFuncion {

    private final int id;

    protected CondicionFuncion(int id) {
        this.id = Validaciones.enteroPositivo(id, "El ID de la condición");
    }

    public abstract TipoFuncion getTipo();

    /** Resumen legible para informes o tickets. */
    public abstract String getResumen();

    public int getId() {
        return id;
    }
}
