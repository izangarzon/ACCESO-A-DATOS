public class Cliente {

    // Atributos
    private final int id;
    private String nombre;
    private String telefono;
    private String matricula;


    //Constructor
    public Cliente(int id, String nombre, String telefono, String matricula) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.matricula = matricula;
    }

    //Getters
    public int getId() {
        return id;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getNombre() {
        return nombre;
    }

    //Setters
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}