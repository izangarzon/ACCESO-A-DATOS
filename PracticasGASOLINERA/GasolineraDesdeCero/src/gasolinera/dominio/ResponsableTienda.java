package gasolinera.dominio;

/** Función de responsable de tienda: su condición es el presupuesto de compras. */
public final class ResponsableTienda extends Funcion {

    public ResponsableTienda(int id, String codigo, String nombre, String descripcion) {
        super(id, codigo, nombre, descripcion);
    }

    @Override
    public TipoFuncion getTipo() {
        return TipoFuncion.RESPONSABLE_TIENDA;
    }
}
