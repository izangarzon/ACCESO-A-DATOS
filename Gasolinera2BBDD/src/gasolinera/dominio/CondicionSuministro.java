package gasolinera.dominio;

import java.math.BigDecimal;
import java.util.Objects;

public class CondicionSuministro {

    private final int id;
    private final AcuerdoSuministro acuerdo;
    private final Producto producto;
    private BigDecimal precioAcordado;
    private BigDecimal cantidadMinima;

    public CondicionSuministro(int id, AcuerdoSuministro acuerdo, Producto producto,
                               BigDecimal precioAcordado, BigDecimal cantidadMinima) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.acuerdo = Objects.requireNonNull(acuerdo, "El acuerdo no puede ser null.");
        this.producto = Objects.requireNonNull(producto, "El producto no puede ser null.");
        setPrecioAcordado(precioAcordado);
        setCantidadMinima(cantidadMinima);
    }

    public int getId() { return id; }
    public AcuerdoSuministro getAcuerdo() { return acuerdo; }
    public Producto getProducto() { return producto; }
    public BigDecimal getPrecioAcordado() { return precioAcordado; }
    public void setPrecioAcordado(BigDecimal precioAcordado) {
        Objects.requireNonNull(precioAcordado, "El precio no puede ser null.");
        if (precioAcordado.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        this.precioAcordado = precioAcordado;
    }
    public BigDecimal getCantidadMinima() { return cantidadMinima; }
    public void setCantidadMinima(BigDecimal cantidadMinima) {
        Objects.requireNonNull(cantidadMinima, "La cantidad mínima no puede ser null.");
        if (cantidadMinima.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("La cantidad mínima no puede ser negativa.");
        this.cantidadMinima = cantidadMinima;
    }
}
