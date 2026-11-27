package gasolinera.dominio;

/** Nivel de autorización en un periodo de jefe de estación. */
public final class CondicionJefeEstacion extends CondicionFuncion {

    private int nivelAutorizacion;

    public CondicionJefeEstacion(int id, int nivelAutorizacion) {
        super(id);
        setNivelAutorizacion(nivelAutorizacion);
    }

    @Override
    public TipoFuncion getTipo() {
        return TipoFuncion.JEFE_ESTACION;
    }

    @Override
    public String getResumen() {
        return "Nivel de autorización: " + nivelAutorizacion;
    }

    public int getNivelAutorizacion() {
        return nivelAutorizacion;
    }

    public void setNivelAutorizacion(int nivelAutorizacion) {
        this.nivelAutorizacion = Validaciones.enteroPositivo(nivelAutorizacion, "El nivel de autorización");
    }
}
