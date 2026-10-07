import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface I_AlmacenamientoPago {

    //Guarda un nuevo pago
    void guardarPago(int idCliente, LocalDate fecha, BigDecimal importe, BigDecimal litros, String combustible);

    //Devuelve todos los pagos
    List<Pago> obtenerTodos();
}

