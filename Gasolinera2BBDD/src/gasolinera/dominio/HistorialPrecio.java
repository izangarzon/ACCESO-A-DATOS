package gasolinera.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public final class HistorialPrecio {

    private final int id;
    private final Estacion estacion;
    private final Producto producto;
    private final BigDecimal precioAnterior;
    private final BigDecimal precioNuevo;
    private final LocalDateTime cambiadoEn;
    private final Empleado empleado;

    public HistorialPrecio(int id, Estacion estacion, Producto producto, BigDecimal precioAnterior,
                           BigDecimal precioNuevo, LocalDateTime cambiadoEn, Empleado empleado) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
        this.producto = Objects.requireNonNull(producto, "El producto no puede ser null.");
        if (precioAnterior != null && precioAnterior.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio anterior no puede ser negativo.");
        }
        this.precioAnterior = precioAnterior;
        this.precioNuevo = Objects.requireNonNull(precioNuevo, "El precio nuevo no puede ser null.");
        if (precioNuevo.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El precio nuevo no puede ser negativo.");
        this.cambiadoEn = Objects.requireNonNull(cambiadoEn, "La fecha de cambio no puede ser null.");
        this.empleado = empleado;
    }

    public int getId() { return id; }
    public Estacion getEstacion() { return estacion; }
    public Producto getProducto() { return producto; }
    public BigDecimal getPrecioAnterior() { return precioAnterior; }
    public BigDecimal getPrecioNuevo() { return precioNuevo; }
    public LocalDateTime getCambiadoEn() { return cambiadoEn; }
    public Empleado getEmpleado() { return empleado; }
}
