package gasolinera.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Venta {

    private final int id;
    private final Estacion estacion;
    private final Empleado empleado;
    private final Cliente cliente;
    private final String codigo;
    private final LocalDateTime fecha;
    private final TipoDocumento tipoDocumento;
    private final String serieFactura;
    private final Integer numeroFactura;
    private BigDecimal subtotal = BigDecimal.ZERO.setScale(2);
    private BigDecimal descuento = BigDecimal.ZERO.setScale(2);
    private BigDecimal total = BigDecimal.ZERO.setScale(2);
    private final List<LineaVenta> lineas = new ArrayList<>();
    private final List<PagoVenta> pagos = new ArrayList<>();
    private final List<AplicacionCampania> campanias = new ArrayList<>();
    private final List<Devolucion> devoluciones = new ArrayList<>();

    public Venta(int id, Estacion estacion, Empleado empleado, Cliente cliente, String codigo,
                 LocalDateTime fecha, TipoDocumento tipoDocumento, String serieFactura,
                 Integer numeroFactura) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
        this.empleado = Objects.requireNonNull(empleado, "El empleado no puede ser null.");
        this.cliente = cliente;
        this.codigo = Producto.validarTexto(codigo, "El código");
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser null.");
        this.tipoDocumento = Objects.requireNonNull(tipoDocumento, "El tipo de documento no puede ser null.");
        if ((serieFactura == null) != (numeroFactura == null)) {
            throw new IllegalArgumentException("La serie y el número de factura deben indicarse juntos.");
        }
        if (serieFactura != null && serieFactura.isBlank()) throw new IllegalArgumentException("La serie no puede estar vacía.");
        if (numeroFactura != null && numeroFactura <= 0) throw new IllegalArgumentException("El número de factura debe ser positivo.");
        if (tipoDocumento == TipoDocumento.FACTURA && serieFactura == null) {
            throw new IllegalArgumentException("Una factura debe tener serie y número.");
        }
        this.serieFactura = serieFactura;
        this.numeroFactura = numeroFactura;
    }

    public void agregarLinea(LineaVenta linea) {
        Objects.requireNonNull(linea, "La línea no puede ser null.");
        if (lineas.stream().anyMatch(actual -> actual.getNumeroLinea() == linea.getNumeroLinea())) {
            throw new IllegalArgumentException("El número de línea ya existe en esta venta.");
        }
        if (linea.getSurtidor() != null && linea.getSurtidor().getEstacion() != estacion) {
            throw new IllegalArgumentException("El surtidor debe pertenecer a la estación de la venta.");
        }
        linea.asignarAVenta(this);
        lineas.add(linea);
        recalcularTotales();
    }

    public void agregarPago(PagoVenta pago) {
        Objects.requireNonNull(pago, "El pago no puede ser null.");
        pago.asignarAVenta(this);
        pagos.add(pago);
    }

    public void agregarCampania(AplicacionCampania aplicacion) {
        Objects.requireNonNull(aplicacion, "La aplicación de campaña no puede ser null.");
        if (campanias.stream().anyMatch(actual -> actual.getCampania().getId() == aplicacion.getCampania().getId())) {
            throw new IllegalArgumentException("La campaña ya está aplicada a esta venta.");
        }
        aplicacion.asignarAVenta(this);
        campanias.add(aplicacion);
    }

    void agregarDevolucion(Devolucion devolucion) {
        devoluciones.add(devolucion);
    }

    private void recalcularTotales() {
        subtotal = lineas.stream()
                .map(linea -> linea.getTotalLinea().add(linea.getDescuento()))
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2);
        descuento = lineas.stream().map(LineaVenta::getDescuento)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2);
        total = subtotal.subtract(descuento).setScale(2);
    }

    public int getId() { return id; }
    public Estacion getEstacion() { return estacion; }
    public Empleado getEmpleado() { return empleado; }
    public Cliente getCliente() { return cliente; }
    public String getCodigo() { return codigo; }
    public LocalDateTime getFecha() { return fecha; }
    public TipoDocumento getTipoDocumento() { return tipoDocumento; }
    public String getSerieFactura() { return serieFactura; }
    public Integer getNumeroFactura() { return numeroFactura; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDescuento() { return descuento; }
    public BigDecimal getTotal() { return total; }
    public List<LineaVenta> getLineas() { return List.copyOf(lineas); }
    public List<PagoVenta> getPagos() { return List.copyOf(pagos); }
    public List<AplicacionCampania> getCampanias() { return List.copyOf(campanias); }
    public List<Devolucion> getDevoluciones() { return List.copyOf(devoluciones); }
}
