package gasolinera.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class PagoVenta {

    private final int id;
    private final MedioPago medioPago;
    private final BigDecimal importe;
    private final LocalDateTime fecha;
    private final String referencia;
    private Venta venta;

    public PagoVenta(int id, MedioPago medioPago, BigDecimal importe, LocalDateTime fecha, String referencia) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.medioPago = Objects.requireNonNull(medioPago, "El medio de pago no puede ser null.");
        this.importe = Objects.requireNonNull(importe, "El importe no puede ser null.");
        if (importe.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("El importe debe ser positivo.");
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser null.");
        this.referencia = referencia == null || referencia.isBlank() ? null : referencia.trim();
    }

    public int getId() { return id; }
    void asignarAVenta(Venta venta) {
        if (this.venta != null && this.venta != venta) {
            throw new IllegalArgumentException("El pago ya pertenece a otra venta.");
        }
        this.venta = venta;
    }
    public Venta getVenta() { return venta; }
    public MedioPago getMedioPago() { return medioPago; }
    public BigDecimal getImporte() { return importe; }
    public LocalDateTime getFecha() { return fecha; }
    public String getReferencia() { return referencia; }
}
