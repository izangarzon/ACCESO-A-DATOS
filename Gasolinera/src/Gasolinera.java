import java.util.*;

public class Gasolinera {

    private final Scanner scanner = new Scanner(System.in);

    private final ClienteCsv clienteCsv;
    private final PagoCsv pagoCsv;


    // Constructor
    public Gasolinera(ClienteCsv clienteCsv, PagoCsv pagoCsv) {
        this.clienteCsv = clienteCsv;
        this.pagoCsv = pagoCsv;
    }


    // Registrar un cliente
    public void registrarCliente(String nombre, String telefono, String matricula) {

        while (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            System.out.println("El nombre solo puede contener letras.");
            System.out.print("Introduce de nuevo el nombre: ");

            nombre = scanner.nextLine().trim();
        }

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
    public void registrarPago(int idCliente) {

        pagoCsv.guardarPago(idCliente);
    }


    // Listar todos los pagos
    public List<Pago> listarPagos() {

        return pagoCsv.obtenerTodos();
    }
}
