import java.util.List;

public class ClienteCsv implements InterfaceCliente {
    @Override
    public void guardar(Cliente cliente) {

    }

    @Override
    public List<Cliente> obtenerTodos() {
        return List.of();
    }

    @Override
    public Cliente buscarPorId(int id) {
        return null;
    }

    @Override
    public boolean existeMatricula(String matricula) {
        return false;
    }
}
