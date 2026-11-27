package gasolinera.dominio;

import java.math.BigDecimal;
import java.util.Objects;

/** Límite autorizado de devoluciones en un periodo de cajero. */
public final class CondicionCajero extends CondicionFuncion {

    private BigDecimal limiteDevoluciones;

    public CondicionCajero(int id, BigDecimal limiteDevoluciones) {
        super(id);
        setLimiteDevoluciones(limiteDevoluciones);
    }

    @Override
    public TipoFuncion getTipo() {
        return TipoFuncion.CAJERO;
    }

    @Override
    public String getResumen() {
        return "Límite de devoluciones: " + limiteDevoluciones + " €";
    }

    public BigDecimal getLimiteDevoluciones() {
        return limiteDevoluciones;
    }

    public void setLimiteDevoluciones(BigDecimal limiteDevoluciones) {
        Objects.requireNonNull(limiteDevoluciones, "El límite de devoluciones no puede ser null.");
        if (limiteDevoluciones.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El límite de devoluciones debe ser mayor que cero.");
        }
        this.limiteDevoluciones = limiteDevoluciones;
    }
}
