import java.util.List;

public interface InterfacePago {

    //Guarda un nuevo pago
    void guardarPago(int idCliente);

    //Devuelve todos los pagos
    List<Pago> obtenerTodos();


}

