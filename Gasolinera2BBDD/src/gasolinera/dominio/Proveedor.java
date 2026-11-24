package gasolinera.dominio;

public class Proveedor {

    private final int id;
    private final String codigo;
    private final String nif;
    private String nombre;
    private String telefono;
    private String email;
    private boolean activo;

    public Proveedor(int id, String codigo, String nif, String nombre,
                     String telefono, String email, boolean activo) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.codigo = Producto.validarTexto(codigo, "El código");
        this.nif = Producto.validarTexto(nif, "El NIF");
        setNombre(nombre);
        setTelefono(telefono);
        setEmail(email);
        this.activo = activo;
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNif() { return nif; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Producto.validarTexto(nombre, "El nombre"); }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono == null || telefono.isBlank() ? null : telefono.trim(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null || email.isBlank() ? null : email.trim(); }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
