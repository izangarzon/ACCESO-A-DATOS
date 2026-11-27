package gasolinera.dominio;

import java.time.LocalDateTime;
import java.util.Objects;

/** Periodo en el que un empleado desempeña una función con unas condiciones concretas. */
public class AsignacionFuncion {

    private final int id;
    private final Empleado empleado;
    private final Funcion funcion;
    private final CondicionFuncion condicion;
    private final LocalDateTime desde;
    private LocalDateTime hasta;

    public AsignacionFuncion(int id, Empleado empleado, Funcion funcion,
                             CondicionFuncion condicion, LocalDateTime desde, LocalDateTime hasta) {
        this.id = Validaciones.enteroPositivo(id, "El ID");
        this.empleado = Objects.requireNonNull(empleado, "El empleado no puede ser null.");
        this.funcion = Objects.requireNonNull(funcion, "La función no puede ser null.");
        this.condicion = Objects.requireNonNull(condicion, "La condición no puede ser null.");
        if (!funcion.admiteCondicion(condicion)) {
            throw new IllegalArgumentException("La condición no corresponde a la función asignada.");
        }
        this.desde = Objects.requireNonNull(desde, "La fecha de inicio no puede ser null.");
        if (hasta != null && !hasta.isAfter(desde)) {
            throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio.");
        }
        this.hasta = hasta;
    }

    public boolean isVigente() {
        return hasta == null;
    }

    /** Dos periodos de la misma función se solapan si comparten algún instante. */
    public boolean seSolapaCon(AsignacionFuncion otra) {
        if (otra == null || otra == this) {
            return false;
        }
        if (otra.funcion.getTipo() != funcion.getTipo()) {
            return false;
        }
        LocalDateTime finEste = hasta == null ? LocalDateTime.MAX : hasta;
        LocalDateTime finOtro = otra.hasta == null ? LocalDateTime.MAX : otra.hasta;
        return desde.isBefore(finOtro) && otra.desde.isBefore(finEste);
    }

    /** Cierra el periodo. No se permite modificar los periodos ya cerrados. */
    public void finalizar(LocalDateTime fechaFin) {
        Objects.requireNonNull(fechaFin, "La fecha de fin no puede ser null.");
        if (!isVigente() || !fechaFin.isAfter(desde)) {
            throw new IllegalStateException("El periodo no está vigente o la fecha de fin no es válida.");
        }
        hasta = fechaFin;
    }

    public int getId() {
        return id;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public Funcion getFuncion() {
        return funcion;
    }

    public CondicionFuncion getCondicion() {
        return condicion;
    }

    public LocalDateTime getDesde() {
        return desde;
    }

    public LocalDateTime getHasta() {
        return hasta;
    }
}
