import java.util.List;

public interface InterfaceCliente {


    //Guarda un nuevo cliente
    void guardarCliente(Cliente cliente);

    //Devuelve todos los clientes
    List<Cliente> obtenerTodos();

    //Busca un cliente por su ID
    List<Cliente> buscarPorNombre(String nombre);

    //Comprueba si una matrícula ya está registrada
    boolean existeMatricula(String matricula);

}
