package gasolinera.dominio;

import java.util.ArrayList;
import java.util.List;

public class Empleado {

    private final int id;
    private final String codigo;
    private final String dni;
    private String nombre;
    private String telefono;
    private String email;
    private boolean activo;
    private final List<AsignacionFuncion> asignaciones = new ArrayList<>();

    public Empleado(int id, String codigo, String dni, String nombre,
                    String telefono, String email, boolean activo) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.codigo = Producto.validarTexto(codigo, "El código");
        this.dni = Producto.validarTexto(dni, "El DNI");
        setNombre(nombre);
        setTelefono(telefono);
        setEmail(email);
        this.activo = activo;
    }

    void agregarAsignacion(AsignacionFuncion asignacion) {
        if (asignacion.getEmpleado() != this) {
            throw new IllegalArgumentException("La asignación debe pertenecer a este empleado.");
        }
        asignaciones.add(asignacion);
    }

    public List<AsignacionFuncion> getAsignaciones() {
        return List.copyOf(asignaciones);
    }

    public List<AsignacionFuncion> getAsignacionesVigentes() {
        return asignaciones.stream().filter(AsignacionFuncion::isVigente).toList();
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getDni() { return dni; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Producto.validarTexto(nombre, "El nombre"); }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono == null || telefono.isBlank() ? null : telefono.trim(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null || email.isBlank() ? null : email.trim(); }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
