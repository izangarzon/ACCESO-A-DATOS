package gasolinera.dominio;

import java.math.BigDecimal;
import java.util.Objects;

public final class DescuentoImporte implements Descuento {

    private final BigDecimal importe;

    public DescuentoImporte(BigDecimal importe) {
        Objects.requireNonNull(importe, "El importe no puede ser null.");
        if (importe.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El importe del descuento debe ser positivo.");
        }
        this.importe = importe;
    }

    public BigDecimal getImporte() {
        return importe;
    }

    @Override
    public TipoDescuento getTipo() {
        return TipoDescuento.IMPORTE;
    }

    @Override
    public BigDecimal calcular(BigDecimal base) {
        Objects.requireNonNull(base, "La base no puede ser null.");
        if (base.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La base no puede ser negativa.");
        }
        return importe.min(base);
    }
}
