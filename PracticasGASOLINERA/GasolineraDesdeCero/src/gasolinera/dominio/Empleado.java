package gasolinera.dominio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Empleado con adscripción actual, supervisor e historial de funciones. */
public class Empleado {

    private final int id;
    private final String codigo;
    private final String dni;
    private String nombre;
    private String telefono;
    private String email;
    private final LocalDate fechaIncorporacion;
    private Estacion estacion;
    private Empleado supervisor;
    private final List<AsignacionFuncion> asignaciones = new ArrayList<>();

    public Empleado(int id, String codigo, String dni, String nombre, String telefono,
                    String email, LocalDate fechaIncorporacion, Estacion estacion) {
        this.id = Validaciones.enteroPositivo(id, "El ID");
        this.codigo = Validaciones.textoObligatorio(codigo, "El código");
        this.dni = Validaciones.textoObligatorio(dni, "El DNI");
        setNombre(nombre);
        setContacto(telefono, email);
        this.fechaIncorporacion = Objects.requireNonNull(
                fechaIncorporacion, "La fecha de incorporación no puede ser null.");
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
    }

    /** Exige al menos un medio de contacto para poder localizar al empleado. */
    public final void setContacto(String telefono, String email) {
        String telefonoLimpio = Validaciones.textoOpcional(telefono, "El teléfono");
        String emailLimpio = Validaciones.textoOpcional(email, "El email");
        if (telefonoLimpio == null && emailLimpio == null) {
            throw new IllegalArgumentException("Debe informarse al menos un medio de contacto.");
        }
        this.telefono = telefonoLimpio;
        this.email = emailLimpio;
    }

    /** Cambia la adscripción actual sin tocar las funciones ni las ventas anteriores. */
    public void cambiarEstacion(Estacion estacion) {
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
    }

    /** Rechaza la auto-supervisión y los ciclos, directos o indirectos. */
    public void setSupervisor(Empleado supervisor) {
        if (supervisor == this) {
            throw new IllegalArgumentException("Un empleado no puede supervisarse a sí mismo.");
        }
        if (supervisor != null && supervisor.tieneEnSuCadena(this)) {
            throw new IllegalArgumentException("La supervisión crearía un ciclo.");
        }
        this.supervisor = supervisor;
    }

    private boolean tieneEnSuCadena(Empleado buscado) {
        Empleado actual = supervisor;
        while (actual != null) {
            if (actual == buscado) {
                return true;
            }
            actual = actual.supervisor;
        }
        return false;
    }

    /** Alta de un periodo de función. No permite solapar dos periodos de la misma función. */
    public AsignacionFuncion asignarFuncion(int id, Funcion funcion, CondicionFuncion condicion,
                                            LocalDateTime desde, LocalDateTime hasta) {
        AsignacionFuncion nueva = new AsignacionFuncion(id, this, funcion, condicion, desde, hasta);
        for (AsignacionFuncion existente : asignaciones) {
            if (existente.seSolapaCon(nueva)) {
                throw new IllegalArgumentException("Ya existe un periodo de esa función solapado.");
            }
        }
        asignaciones.add(nueva);
        return nueva;
    }

    public AsignacionFuncion asignarFuncion(int id, Funcion funcion, CondicionFuncion condicion,
                                            LocalDateTime desde) {
        return asignarFuncion(id, funcion, condicion, desde, null);
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDni() {
        return dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Validaciones.textoObligatorio(nombre, "El nombre");
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getFechaIncorporacion() {
        return fechaIncorporacion;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public Empleado getSupervisor() {
        return supervisor;
    }

    public List<AsignacionFuncion> getAsignaciones() {
        return List.copyOf(asignaciones);
    }

    public List<AsignacionFuncion> getAsignacionesVigentes() {
        return asignaciones.stream().filter(AsignacionFuncion::isVigente).toList();
    }
}
