package gasolinera.dominio;

public class Funcion {

    private final int id;
    private final String codigo;
    private String nombre;
    private String descripcion;

    public Funcion(int id, String codigo, String nombre, String descripcion) {
        if (id <= 0) throw new IllegalArgumentException("El ID debe ser positivo.");
        this.id = id;
        this.codigo = Producto.validarTexto(codigo, "El código");
        setNombre(nombre);
        setDescripcion(descripcion);
    }

    public int getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = Producto.validarTexto(nombre, "El nombre"); }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion == null || descripcion.isBlank() ? null : descripcion.trim(); }
}
