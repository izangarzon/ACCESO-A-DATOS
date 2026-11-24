package gasolinera.dominio;

import java.util.ArrayList;
import java.util.List;

public class Cliente {

    private final int id;
    private final String codigo;
    private String nombre;
    private String telefono;
    private String email;
    private String matricula;
    private boolean activo;
    private final List<TarjetaFidelizacion> tarjetas = new ArrayList<>();

    public Cliente(int id, String codigo, String nombre, String telefono,
                   String email, String matricula, boolean activo) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.codigo = Producto.validarTexto(codigo, "El código");
        setNombre(nombre);
        setTelefono(telefono);
        setEmail(email);
        setMatricula(matricula);
        this.activo = activo;
    }

    void agregarTarjeta(TarjetaFidelizacion tarjeta) {
        if (tarjeta.getCliente() != this) {
            throw new IllegalArgumentException("La tarjeta no está asignada a este cliente.");
        }
        tarjetas.add(tarjeta);
    }

    void quitarTarjeta(TarjetaFidelizacion tarjeta) {
        tarjetas.remove(tarjeta);
    }

    public List<TarjetaFidelizacion> getTarjetas() {
        return List.copyOf(tarjetas);
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Producto.validarTexto(nombre, "El nombre"); }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono == null || telefono.isBlank() ? null : telefono.trim(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null || email.isBlank() ? null : email.trim(); }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula == null || matricula.isBlank() ? null : matricula.trim(); }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
