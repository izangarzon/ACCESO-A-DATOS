package gasolinera.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public abstract class Producto {

    private final int id;
    private final String codigo;
    private String nombre;
    private final UnidadMedida unidadMedida;
    private boolean activo;

    protected Producto(int id, String codigo, String nombre, UnidadMedida unidadMedida, boolean activo) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser positivo.");
        }
        this.id = id;
        this.codigo = validarTexto(codigo, "El código");
        this.nombre = validarTexto(nombre, "El nombre");
        this.unidadMedida = Objects.requireNonNull(unidadMedida, "La unidad de medida no puede ser null.");
        this.activo = activo;
    }

    public abstract TipoProducto getTipo();

    public abstract void validarCantidad(BigDecimal cantidad);

    public final BigDecimal calcularImporte(BigDecimal cantidad, BigDecimal precioUnitario) {
        validarCantidad(cantidad);
        Objects.requireNonNull(precioUnitario, "El precio de venta no puede ser null.");
        if (precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor que cero.");
        }
        return cantidad.multiply(precioUnitario).setScale(2, RoundingMode.HALF_UP);
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = validarTexto(nombre, "El nombre");
    }

    public UnidadMedida getUnidadMedida() {
        return unidadMedida;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    protected static String validarTexto(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser null.");
        String limpio = valor.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return limpio;
    }
}
