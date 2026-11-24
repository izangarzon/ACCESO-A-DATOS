package gasolinera.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LineaVenta {

    private final int id;
    private final int numeroLinea;
    private final Producto producto;
    private final Surtidor surtidor;
    private final String codigoProductoAplicado;
    private final String nombreProductoAplicado;
    private Venta venta;
    private final BigDecimal cantidad;
    private final BigDecimal precioUnitarioAplicado;
    private final BigDecimal descuento;
    private final BigDecimal totalLinea;
    private final List<LineaDevolucion> devoluciones = new ArrayList<>();

    public LineaVenta(int id, int numeroLinea, Producto producto, Surtidor surtidor,
                      String codigoProductoAplicado, String nombreProductoAplicado,
                      BigDecimal cantidad, BigDecimal precioUnitarioAplicado, BigDecimal descuento) {
        if (id <= 0 || numeroLinea <= 0) throw new IllegalArgumentException("ID y número de línea deben ser positivos.");
        this.id = id;
        this.numeroLinea = numeroLinea;
        this.producto = Objects.requireNonNull(producto, "El producto no puede ser null.");
        this.cantidad = cantidad;
        this.precioUnitarioAplicado = validarPrecioPositivo(precioUnitarioAplicado);
        this.descuento = validarNoNegativo(descuento, "El descuento");
        BigDecimal bruto = producto.calcularImporte(cantidad, precioUnitarioAplicado);
        if (descuento.compareTo(bruto) > 0) throw new IllegalArgumentException("El descuento supera el importe bruto.");
        this.totalLinea = bruto.subtract(descuento).setScale(2, RoundingMode.HALF_UP);
        this.codigoProductoAplicado = Producto.validarTexto(codigoProductoAplicado, "El código aplicado");
        this.nombreProductoAplicado = Producto.validarTexto(nombreProductoAplicado, "El nombre aplicado");
        if (surtidor != null) {
            if (!(producto instanceof ProductoGranel) || surtidor.getProducto() != producto) {
                throw new IllegalArgumentException("El surtidor solo puede asociarse al producto a granel que dispensa.");
            }
        }
        this.surtidor = surtidor;
    }

    void agregarDevolucion(LineaDevolucion devolucion) {
        devoluciones.add(devolucion);
    }

    void asignarAVenta(Venta venta) {
        if (this.venta != null && this.venta != venta) {
            throw new IllegalArgumentException("La línea ya pertenece a otra venta.");
        }
        this.venta = venta;
    }

    public BigDecimal getCantidadDevuelta() {
        return devoluciones.stream()
                .map(LineaDevolucion::getCantidad)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getId() { return id; }
    public int getNumeroLinea() { return numeroLinea; }
    public Producto getProducto() { return producto; }
    public Surtidor getSurtidor() { return surtidor; }
    public String getCodigoProductoAplicado() { return codigoProductoAplicado; }
    public String getNombreProductoAplicado() { return nombreProductoAplicado; }
    public Venta getVenta() { return venta; }
    public BigDecimal getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitarioAplicado() { return precioUnitarioAplicado; }
    public BigDecimal getDescuento() { return descuento; }
    public BigDecimal getTotalLinea() { return totalLinea; }
    public List<LineaDevolucion> getDevoluciones() { return List.copyOf(devoluciones); }

    private static BigDecimal validarNoNegativo(BigDecimal valor, String nombre) {
        Objects.requireNonNull(valor, nombre + " no puede ser null.");
        if (valor.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException(nombre + " no puede ser negativo.");
        return valor;
    }

    private static BigDecimal validarPrecioPositivo(BigDecimal precio) {
        Objects.requireNonNull(precio, "El precio de venta no puede ser null.");
        if (precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor que cero.");
        }
        return precio;
    }
}
