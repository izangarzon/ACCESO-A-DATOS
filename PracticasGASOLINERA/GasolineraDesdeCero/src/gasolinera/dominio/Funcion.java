package gasolinera.dominio;

/** Catálogo de funciones laborales. Cada subclase representa un tipo distinto. */
public abstract class Funcion {

    private final int id;
    private final String codigo;
    private String nombre;
    private String descripcion;

    protected Funcion(int id, String codigo, String nombre, String descripcion) {
        this.id = Validaciones.enteroPositivo(id, "El ID");
        this.codigo = Validaciones.textoObligatorio(codigo, "El código de función");
        setNombre(nombre);
        setDescripcion(descripcion);
    }

    public abstract TipoFuncion getTipo();

    /** Comprueba que la condición corresponde a este tipo de función. */
    public final boolean admiteCondicion(CondicionFuncion condicion) {
        return condicion != null && condicion.getTipo() == getTipo();
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = Validaciones.textoObligatorio(nombre, "El nombre de la función");
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = Validaciones.textoOpcional(descripcion, "La descripción");
    }
}
