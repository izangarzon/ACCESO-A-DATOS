package gasolinera.dominio;

/** Función de expendedor: su condición es la acreditación para manipular combustibles. */
public final class Expendedor extends Funcion {

    public Expendedor(int id, String codigo, String nombre, String descripcion) {
        super(id, codigo, nombre, descripcion);
    }

    @Override
    public TipoFuncion getTipo() {
        return TipoFuncion.EXPENDEDOR;
    }
}
