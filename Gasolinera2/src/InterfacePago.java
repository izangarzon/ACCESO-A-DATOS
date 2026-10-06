import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface InterfacePago {

    //Guarda un nuevo pago
    void guardarPago(int idCliente, LocalDate fecha, BigDecimal importe, BigDecimal litros, String combustible);

    //Devuelve todos los pagos
    List<Pago> obtenerTodos();
}

