package gasolinera.dominio;

import java.math.BigDecimal;
import java.util.Objects;

/** Presupuesto autorizado de compras en un periodo de responsable de tienda. */
public final class CondicionResponsableTienda extends CondicionFuncion {

    private BigDecimal presupuestoCompras;

    public CondicionResponsableTienda(int id, BigDecimal presupuestoCompras) {
        super(id);
        setPresupuestoCompras(presupuestoCompras);
    }

    @Override
    public TipoFuncion getTipo() {
        return TipoFuncion.RESPONSABLE_TIENDA;
    }

    @Override
    public String getResumen() {
        return "Presupuesto de compras: " + presupuestoCompras + " €";
    }

    public BigDecimal getPresupuestoCompras() {
        return presupuestoCompras;
    }

    public void setPresupuestoCompras(BigDecimal presupuestoCompras) {
        Objects.requireNonNull(presupuestoCompras, "El presupuesto no puede ser null.");
        if (presupuestoCompras.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El presupuesto no puede ser negativo.");
        }
        this.presupuestoCompras = presupuestoCompras;
    }
}
