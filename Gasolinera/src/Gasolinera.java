import java.util.List;

public class Gasolinera {

    private final ClienteCsv clienteCsv;
    private final PagoCsv pagoCsv;


    // Constructor
    public Gasolinera(ClienteCsv clienteCsv, PagoCsv pagoCsv) {
        this.clienteCsv = clienteCsv;
        this.pagoCsv = pagoCsv;
    }


    // Registrar un cliente
    public void registrarCliente(String nombre, String telefono, String matricula) {

        clienteCsv.guardarCliente(nombre, telefono, matricula);
    }


    // Buscar clientes por nombre
    public List<Cliente> buscarClientes(String nombre) {

        return clienteCsv.buscar(nombre);
    }


    // Listar todos los clientes
    public List<Cliente> listarClientes() {

        return clienteCsv.obtenerTodos();
    }


    // Registrar un pago
    public void registrarPago(Pago pago) {

        pagoCsv.guardarPago(pago);
    }


    // Listar todos los pagos
    public List<Pago> listarPagos() {

        return pagoCsv.obtenerTodos();
    }
}
