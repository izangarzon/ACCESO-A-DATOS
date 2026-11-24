package gasolinera.dominio;

import java.time.LocalDateTime;
import java.util.Objects;

public class Incidencia {

    private final int id;
    private final String codigo;
    private final Estacion estacion;
    private final Empleado empleado;
    private final Surtidor surtidor;
    private final Venta venta;
    private final LocalDateTime fecha;
    private String descripcion;
    private EstadoIncidencia estado;
    private LocalDateTime resueltaEn;

    public Incidencia(int id, String codigo, Estacion estacion, Empleado empleado,
                      Surtidor surtidor, Venta venta, LocalDateTime fecha,
                      String descripcion, EstadoIncidencia estado, LocalDateTime resueltaEn) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.codigo = Producto.validarTexto(codigo, "El código");
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
        if (surtidor != null && surtidor.getEstacion() != estacion) {
            throw new IllegalArgumentException("El surtidor debe pertenecer a la estación de la incidencia.");
        }
        if (venta != null && venta.getEstacion() != estacion) {
            throw new IllegalArgumentException("La venta debe pertenecer a la estación de la incidencia.");
        }
        this.empleado = empleado;
        this.surtidor = surtidor;
        this.venta = venta;
        this.fecha = Objects.requireNonNull(fecha, "La fecha no puede ser null.");
        setDescripcion(descripcion);
        this.estado = Objects.requireNonNull(estado, "El estado no puede ser null.");
        setResueltaEn(resueltaEn);
    }

    public void resolver(LocalDateTime fechaResolucion) {
        this.estado = EstadoIncidencia.RESUELTA;
        setResueltaEn(fechaResolucion);
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public Estacion getEstacion() { return estacion; }
    public Empleado getEmpleado() { return empleado; }
    public Surtidor getSurtidor() { return surtidor; }
    public Venta getVenta() { return venta; }
    public LocalDateTime getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = Producto.validarTexto(descripcion, "La descripción"); }
    public EstadoIncidencia getEstado() { return estado; }
    public void setEstado(EstadoIncidencia estado) { this.estado = Objects.requireNonNull(estado); }
    public LocalDateTime getResueltaEn() { return resueltaEn; }
    public void setResueltaEn(LocalDateTime resueltaEn) {
        if (resueltaEn != null && resueltaEn.isBefore(fecha)) {
            throw new IllegalArgumentException("La resolución no puede ser anterior a la incidencia.");
        }
        this.resueltaEn = resueltaEn;
    }
}
