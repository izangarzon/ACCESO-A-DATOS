import java.util.List;

public interface InterfaceCliente {


    //Guarda un nuevo cliente
    void guardarCliente(String nombre, String telefono, String matricula);

    //Devuelve todos los clientes
    List<Cliente> obtenerTodos();

    //Busca un cliente por su Nombre, Telefono o Matricula
    List<Cliente> buscar(String nombre);

    //Comprueba si una matrícula ya está registrada
    boolean existeMatricula(String matricula);

}
