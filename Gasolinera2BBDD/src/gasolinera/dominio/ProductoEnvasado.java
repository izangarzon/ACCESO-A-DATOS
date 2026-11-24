package gasolinera.dominio;

import java.math.BigDecimal;
import java.util.Objects;

public final class ProductoEnvasado extends Producto {

    private final String codigoBarras;
    private final BigDecimal contenidoEnvase;
    private final String formatoEnvase;

    public ProductoEnvasado(int id, String codigo, String nombre, String codigoBarras,
                            BigDecimal contenidoEnvase, UnidadMedida unidadMedida, boolean activo) {
        this(id, codigo, nombre, codigoBarras, contenidoEnvase,
                formatoPorDefecto(contenidoEnvase, unidadMedida),
                unidadMedida, activo);
    }

    public ProductoEnvasado(int id, String codigo, String nombre, String codigoBarras,
                            BigDecimal contenidoEnvase, String formatoEnvase,
                            UnidadMedida unidadMedida, boolean activo) {
        super(id, codigo, nombre, unidadMedida, activo);
        if (unidadMedida != UnidadMedida.UNIDAD) {
            throw new IllegalArgumentException("Un producto envasado se vende por unidades completas.");
        }
        this.codigoBarras = codigoBarras == null || codigoBarras.isBlank() ? null : codigoBarras.trim();
        this.contenidoEnvase = Objects.requireNonNull(contenidoEnvase, "El contenido no puede ser null.");
        if (contenidoEnvase.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El contenido del envase debe ser positivo.");
        }
        this.formatoEnvase = validarTexto(formatoEnvase, "El formato del envase");
    }

    @Override
    public TipoProducto getTipo() {
        return TipoProducto.ENVASADO;
    }

    @Override
    public void validarCantidad(BigDecimal cantidad) {
        Objects.requireNonNull(cantidad, "La cantidad no puede ser null.");
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0 || cantidad.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("La cantidad de productos envasados debe ser un número entero positivo.");
        }
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public BigDecimal getContenidoEnvase() {
        return contenidoEnvase;
    }

    public String getFormatoEnvase() {
        return formatoEnvase;
    }

    private static String formatoPorDefecto(BigDecimal contenidoEnvase, UnidadMedida unidadMedida) {
        Objects.requireNonNull(contenidoEnvase, "El contenido no puede ser null.");
        Objects.requireNonNull(unidadMedida, "La unidad de medida no puede ser null.");
        return contenidoEnvase.stripTrailingZeros().toPlainString() + " " + unidadMedida.name().toLowerCase();
    }
}
