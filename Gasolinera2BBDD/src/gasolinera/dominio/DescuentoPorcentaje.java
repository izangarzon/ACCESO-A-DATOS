package gasolinera.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class DescuentoPorcentaje implements Descuento {

    private final BigDecimal porcentaje;

    public DescuentoPorcentaje(BigDecimal porcentaje) {
        Objects.requireNonNull(porcentaje, "El porcentaje no puede ser null.");
        if (porcentaje.compareTo(BigDecimal.ZERO) <= 0
                || porcentaje.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("El porcentaje debe ser mayor que 0 y como máximo 100.");
        }
        this.porcentaje = porcentaje;
    }

    public BigDecimal getPorcentaje() {
        return porcentaje;
    }

    @Override
    public TipoDescuento getTipo() {
        return TipoDescuento.PORCENTAJE;
    }

    @Override
    public BigDecimal calcular(BigDecimal base) {
        validarBase(base);
        return base.multiply(porcentaje)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    private static void validarBase(BigDecimal base) {
        Objects.requireNonNull(base, "La base no puede ser null.");
        if (base.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La base no puede ser negativa.");
        }
    }
}
