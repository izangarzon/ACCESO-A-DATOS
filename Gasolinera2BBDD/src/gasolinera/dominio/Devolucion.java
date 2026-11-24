package gasolinera.dominio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Devolucion {

    private final int id;
    private final Venta venta;
    private final Empleado empleado;
    private final String codigo;
    private final LocalDateTime fecha;
    private final String motivo;
    private final List<LineaDevolucion> lineas = new ArrayList<>();
    private final List<ReembolsoDevolucion> reembolsos = new ArrayList<>();

    public Devolucion(int id, Venta venta, Empleado empleado, String codigo,
                      LocalDateTime fecha, String motivo) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.venta = Objects.requireNonNull(venta, "La venta original no puede ser null.");
        this.empleado = Objects.requireNonNull(empleado, "El empleado no puede ser null.");
        this.codigo = Producto.validarTexto(codigo, "El código");
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser null.");
        this.motivo = motivo == null || motivo.isBlank() ? null : motivo.trim();
        venta.agregarDevolucion(this);
    }

    public void agregarLinea(LineaDevolucion linea) {
        Objects.requireNonNull(linea, "La línea devuelta no puede ser null.");
        if (lineas.stream().anyMatch(actual -> actual.getLineaVenta() == linea.getLineaVenta())) {
            throw new IllegalArgumentException("La línea de venta ya está incluida en esta devolución.");
        }
        if (!venta.getLineas().contains(linea.getLineaVenta())) {
            throw new IllegalArgumentException("La línea devuelta no pertenece a la venta original.");
        }
        BigDecimal acumulado = linea.getLineaVenta().getCantidadDevuelta().add(linea.getCantidad());
        if (acumulado.compareTo(linea.getLineaVenta().getCantidad()) > 0) {
            throw new IllegalArgumentException("La cantidad devuelta supera la cantidad vendida.");
        }
        lineas.add(linea);
        linea.asignarADevolucion(this);
        linea.getLineaVenta().agregarDevolucion(linea);
    }

    public void agregarReembolso(ReembolsoDevolucion reembolso) {
        Objects.requireNonNull(reembolso, "El reembolso no puede ser null.");
        reembolso.asignarADevolucion(this);
        reembolsos.add(reembolso);
    }

    public BigDecimal getTotal() {
        return lineas.stream().map(LineaDevolucion::getImporte)
                .reduce(BigDecimal.ZERO, BigDecimal::add).setScale(2);
    }

    public int getId() { return id; }
    public Venta getVenta() { return venta; }
    public Empleado getEmpleado() { return empleado; }
    public String getCodigo() { return codigo; }
    public LocalDateTime getFecha() { return fecha; }
    public String getMotivo() { return motivo; }
    public List<LineaDevolucion> getLineas() { return List.copyOf(lineas); }
    public List<ReembolsoDevolucion> getReembolsos() { return List.copyOf(reembolsos); }
}
