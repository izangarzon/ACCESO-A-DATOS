import java.util.List;

public interface InterfacePago {

    //Guarda un nuevo pago
    void guardar(Pago pago);

    //Devuelve todos los pagos
    List<Pago> obtenerTodos();

    //Busca un pago por su ID
    Pago buscarPorId(int id);
}

