package gasolinera.dominio;

import java.time.LocalDateTime;
import java.util.Objects;

public class AsignacionFuncion {

    private final int id;
    private final Empleado empleado;
    private final Funcion funcion;
    private final Estacion estacion;
    private final AsignacionFuncion supervisor;
    private final LocalDateTime desde;
    private LocalDateTime hasta;

    public AsignacionFuncion(int id, Empleado empleado, Funcion funcion, Estacion estacion,
                             AsignacionFuncion supervisor, LocalDateTime desde, LocalDateTime hasta) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.empleado = Objects.requireNonNull(empleado, "El empleado no puede ser null.");
        this.funcion = Objects.requireNonNull(funcion, "La función no puede ser null.");
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
        if (supervisor == this || (supervisor != null && supervisor.getEmpleado() == empleado)) {
            throw new IllegalArgumentException("El supervisor debe ser otro empleado.");
        }
        if (supervisor != null && supervisor.getEstacion() != estacion) {
            throw new IllegalArgumentException("El supervisor debe tener una asignación en la misma estación.");
        }
        this.supervisor = supervisor;
        this.desde = Objects.requireNonNull(desde, "La fecha inicial no puede ser null.");
        if (hasta != null && !hasta.isAfter(desde)) {
            throw new IllegalArgumentException("La fecha final debe ser posterior a la inicial.");
        }
        this.hasta = hasta;
        empleado.agregarAsignacion(this);
    }

    public boolean isVigente() {
        return hasta == null;
    }

    public void finalizar(LocalDateTime fechaFin) {
        Objects.requireNonNull(fechaFin, "La fecha final no puede ser null.");
        if (!isVigente() || !fechaFin.isAfter(desde)) {
            throw new IllegalStateException("La asignación no está vigente o la fecha final no es válida.");
        }
        hasta = fechaFin;
    }

    public int getId() { return id; }
    public Empleado getEmpleado() { return empleado; }
    public Funcion getFuncion() { return funcion; }
    public Estacion getEstacion() { return estacion; }
    public AsignacionFuncion getSupervisor() { return supervisor; }
    public LocalDateTime getDesde() { return desde; }
    public LocalDateTime getHasta() { return hasta; }
}
