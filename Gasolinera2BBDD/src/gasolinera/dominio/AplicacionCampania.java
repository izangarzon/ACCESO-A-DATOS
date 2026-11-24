package gasolinera.dominio;

import java.math.BigDecimal;
import java.util.Objects;

public class AplicacionCampania {

    private final int id;
    private final Campania campania;
    private final BigDecimal descuentoAplicado;
    private Venta venta;

    public AplicacionCampania(int id, Campania campania, BigDecimal descuentoAplicado) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.campania = Objects.requireNonNull(campania, "La campaña no puede ser null.");
        this.descuentoAplicado = Objects.requireNonNull(descuentoAplicado, "El descuento no puede ser null.");
        if (descuentoAplicado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El descuento aplicado no puede ser negativo.");
        }
    }

    public int getId() { return id; }
    void asignarAVenta(Venta venta) {
        if (this.venta != null && this.venta != venta) {
            throw new IllegalArgumentException("La campaña ya está aplicada a otra venta.");
        }
        this.venta = venta;
    }
    public Venta getVenta() { return venta; }
    public Campania getCampania() { return campania; }
    public BigDecimal getDescuentoAplicado() { return descuentoAplicado; }
}
