package gasolinera.dominio;

import java.math.BigDecimal;
import java.util.Objects;

public final class ProductoGranel extends Producto {

    private final int precisionPesada;

    public ProductoGranel(int id, String codigo, String nombre, UnidadMedida unidadMedida, boolean activo) {
        this(id, codigo, nombre, unidadMedida, 3, activo);
    }

    public ProductoGranel(int id, String codigo, String nombre, UnidadMedida unidadMedida,
                          int precisionPesada, boolean activo) {
        super(id, codigo, nombre, unidadMedida, activo);
        if (unidadMedida != UnidadMedida.KILOGRAMO) {
            throw new IllegalArgumentException("Los productos a granel se venden por kilogramos.");
        }
        if (precisionPesada < 0 || precisionPesada > 3) {
            throw new IllegalArgumentException("La precisión de pesada debe estar entre 0 y 3 decimales.");
        }
        this.precisionPesada = precisionPesada;
    }

    @Override
    public TipoProducto getTipo() {
        return TipoProducto.GRANEL;
    }

    @Override
    public void validarCantidad(BigDecimal cantidad) {
        Objects.requireNonNull(cantidad, "La cantidad no puede ser null.");
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad en kilogramos debe ser mayor que cero.");
        }
        if (cantidad.stripTrailingZeros().scale() > precisionPesada) {
            throw new IllegalArgumentException(
                    "La cantidad supera la precisión de pesada admitida: máximo "
                            + precisionPesada + " decimales.");
        }
    }

    public int getPrecisionPesada() {
        return precisionPesada;
    }
}
