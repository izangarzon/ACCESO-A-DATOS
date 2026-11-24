package gasolinera.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class ReembolsoDevolucion {

    private final int id;
    private final MedioPago medioPago;
    private final BigDecimal importe;
    private final LocalDateTime fecha;
    private final String referencia;
    private Devolucion devolucion;

    public ReembolsoDevolucion(int id, MedioPago medioPago, BigDecimal importe,
                               LocalDateTime fecha, String referencia) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.medioPago = Objects.requireNonNull(medioPago, "El medio de pago no puede ser null.");
        this.importe = Objects.requireNonNull(importe, "El importe no puede ser null.");
        if (importe.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("El importe debe ser positivo.");
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser null.");
        this.referencia = referencia == null || referencia.isBlank() ? null : referencia.trim();
    }

    public int getId() { return id; }
    void asignarADevolucion(Devolucion devolucion) {
        if (this.devolucion != null && this.devolucion != devolucion) {
            throw new IllegalArgumentException("El reembolso ya pertenece a otra devolución.");
        }
        this.devolucion = devolucion;
    }
    public Devolucion getDevolucion() { return devolucion; }
    public MedioPago getMedioPago() { return medioPago; }
    public BigDecimal getImporte() { return importe; }
    public LocalDateTime getFecha() { return fecha; }
    public String getReferencia() { return referencia; }
}
