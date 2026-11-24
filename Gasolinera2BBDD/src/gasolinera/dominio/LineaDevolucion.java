package gasolinera.dominio;

import java.math.BigDecimal;
import java.util.Objects;

public class LineaDevolucion {

    private final int id;
    private final LineaVenta lineaVenta;
    private final BigDecimal cantidad;
    private final BigDecimal precioUnitarioReembolsado;
    private final BigDecimal importe;
    private Devolucion devolucion;

    public LineaDevolucion(int id, LineaVenta lineaVenta, BigDecimal cantidad,
                           BigDecimal precioUnitarioReembolsado, BigDecimal importe) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.lineaVenta = Objects.requireNonNull(lineaVenta, "La línea de venta no puede ser null.");
        this.cantidad = Objects.requireNonNull(cantidad, "La cantidad no puede ser null.");
        if (cantidad.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("La cantidad debe ser positiva.");
        this.precioUnitarioReembolsado = Objects.requireNonNull(precioUnitarioReembolsado, "El precio no puede ser null.");
        if (precioUnitarioReembolsado.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        this.importe = Objects.requireNonNull(importe, "El importe no puede ser null.");
        if (importe.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El importe no puede ser negativo.");
    }

    public int getId() { return id; }
    void asignarADevolucion(Devolucion devolucion) {
        if (this.devolucion != null && this.devolucion != devolucion) {
            throw new IllegalArgumentException("La línea ya pertenece a otra devolución.");
        }
        this.devolucion = devolucion;
    }
    public Devolucion getDevolucion() { return devolucion; }
    public LineaVenta getLineaVenta() { return lineaVenta; }
    public BigDecimal getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitarioReembolsado() { return precioUnitarioReembolsado; }
    public BigDecimal getImporte() { return importe; }
}
