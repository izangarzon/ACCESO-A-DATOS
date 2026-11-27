package gasolinera.dominio;

/** Función de cajero: su condición es el límite autorizado de devoluciones. */
public final class Cajero extends Funcion {

    public Cajero(int id, String codigo, String nombre, String descripcion) {
        super(id, codigo, nombre, descripcion);
    }

    @Override
    public TipoFuncion getTipo() {
        return TipoFuncion.CAJERO;
    }
}
