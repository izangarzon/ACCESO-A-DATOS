import java.util.List;

public interface InterfaceCliente {


    //Guarda un nuevo cliente
    void guardar(Cliente cliente);

    //Devuelve todos los clientes
    List<Cliente> obtenerTodos();

    //Busca un cliente por su ID
    Cliente buscarPorId(int id);

    //Comprueba si una matrícula ya está registrada
    boolean existeMatricula(String matricula);

}
