package gasolinera.dominio;

/** Función de jefe de estación: su condición es el nivel de autorización. */
public final class JefeEstacion extends Funcion {

    public JefeEstacion(int id, String codigo, String nombre, String descripcion) {
        super(id, codigo, nombre, descripcion);
    }

    @Override
    public TipoFuncion getTipo() {
        return TipoFuncion.JEFE_ESTACION;
    }
}
