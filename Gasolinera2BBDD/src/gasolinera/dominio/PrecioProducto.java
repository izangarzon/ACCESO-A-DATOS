package gasolinera.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class PrecioProducto {

    private final int id;
    private final Estacion estacion;
    private final Producto producto;
    private BigDecimal precio;
    private LocalDateTime actualizadoEn;
    private Empleado actualizadoPor;

    public PrecioProducto(int id, Estacion estacion, Producto producto, BigDecimal precio,
                          LocalDateTime actualizadoEn, Empleado actualizadoPor) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
        this.producto = Objects.requireNonNull(producto, "El producto no puede ser null.");
        setPrecio(precio);
        this.actualizadoEn = Objects.requireNonNull(actualizadoEn, "La fecha de actualización no puede ser null.");
        this.actualizadoPor = actualizadoPor;
    }

    public void setPrecio(BigDecimal precio) {
        Objects.requireNonNull(precio, "El precio no puede ser null.");
        if (precio.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        this.precio = precio;
    }

    public int getId() { return id; }
    public Estacion getEstacion() { return estacion; }
    public Producto getProducto() { return producto; }
    public BigDecimal getPrecio() { return precio; }
    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = Objects.requireNonNull(actualizadoEn); }
    public Empleado getActualizadoPor() { return actualizadoPor; }
    public void setActualizadoPor(Empleado actualizadoPor) { this.actualizadoPor = actualizadoPor; }
}
