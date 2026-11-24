package gasolinera.dominio;

public class MedioPago {

    private final int id;
    private final String codigo;
    private String nombre;
    private boolean activo;

    public MedioPago(int id, String codigo, String nombre, boolean activo) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.codigo = Producto.validarTexto(codigo, "El código");
        setNombre(nombre);
        this.activo = activo;
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Producto.validarTexto(nombre, "El nombre"); }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
