import java.util.List;

public class PagoCsv implements InterfacePago {
    @Override
    public void guardar(Pago pago) {

    }

    @Override
    public List<Pago> obtenerTodos() {
        return List.of();
    }

    @Override
    public Pago buscarPorId(int id) {
        return null;
    }
}
