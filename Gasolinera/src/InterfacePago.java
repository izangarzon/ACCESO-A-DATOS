import java.util.List;

public interface InterfacePago {

    //Guarda un nuevo pago
    void guardarPago(Pago pago);

    //Devuelve todos los pagos
    List<Pago> obtenerTodos();


}

