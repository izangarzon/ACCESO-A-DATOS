import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.time.format.DateTimeFormatter;

public class Gasolinera {

    private final Scanner scanner = new Scanner(System.in);

    private final ClienteJson clienteJson;
    private final PagoJson pagoJson;


    // Constructor
    public Gasolinera(ClienteJson clienteJson, PagoJson pagoJson) {
        this.clienteJson = clienteJson;
        this.pagoJson = pagoJson;
    }


    // Registrar un cliente
    public void registrarCliente() {

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        while (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            System.out.println("El nombre solo puede contener letras.");
            System.out.print("Introduce de nuevo el nombre: ");
            nombre = scanner.nextLine().trim();
        }

        System.out.print("Teléfono: ");
        String telefono = scanner.nextLine();

        while (!telefono.matches("[0-9 ]{9}")) {
            System.out.println("El telefono solo puede contener 9 numeros.");
            System.out.print("Introduce de nuevo el telefono: ");
            telefono = scanner.nextLine().trim();
        }

        System.out.print("Matrícula: ");
        String matricula = scanner.nextLine();

        while (!matricula.matches("[0-9]{4}[a-zA-Z]{3}")) {
            System.out.println("La matrícula debe tener 4 números y 3 letras.");
            System.out.print("Introduce de nuevo la matrícula: ");
            matricula = scanner.nextLine().trim();
        }

        clienteJson.guardarCliente(nombre, telefono, matricula);
    }


    // Buscar clientes por nombre
    public List<Cliente> buscarClientes(String nombre) {

        return clienteJson.buscar(nombre);
    }


    // Listar todos los clientes
    public List<Cliente> listarClientes() {

        return clienteJson.obtenerTodos();
    }


    // Registrar un pago
    public void registrarPago(int idCliente) {

        Cliente cliente = clienteJson.buscarPorId(idCliente);

        if (cliente == null) {
            System.out.println("El identificador no corresponde a ningún cliente.");
            return;
        }

        //Pedimos la fecha
        LocalDate fecha = null;

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (fecha == null) {

            System.out.print("Fecha (dd/MM/aaaa) vacío --> hoy: ");
            String textoFecha = scanner.nextLine().trim();

            if (textoFecha.isBlank()) {
                fecha = LocalDate.now();
            } else {
                try {
                    fecha = LocalDate.parse(textoFecha, formato);
                } catch (DateTimeParseException e) {
                    System.out.println("La fecha no es válida.");
                }
            }
        }

        //Pedimos el importe
        BigDecimal importe = null;

        while (importe == null) {

            System.out.print("Introduce el importe: ");
            String texto = scanner.nextLine().trim();

            // Cambiamos la coma por un punto
            texto = texto.replace(",", ".");

            try {
                BigDecimal numero = new BigDecimal(texto);

                if (numero.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("El valor debe ser mayor que 0.");
                } else if (numero.scale() > 2) {
                    System.out.println("Solo se permiten dos decimales como máximo.");
                } else {
                    importe = numero;
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número válido.");
            }
        }

        //Pedimos los litros
        BigDecimal litros = null;

        while (litros == null) {

            System.out.print("Introduce los litros: ");
            String texto = scanner.nextLine().trim();

            // Cambiamos la coma por un punto
            texto = texto.replace(",", ".");

            try {
                BigDecimal numero = new BigDecimal(texto);

                if (numero.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("El valor debe ser mayor que 0.");
                } else if (numero.scale() > 2) {
                    System.out.println("Solo se permiten dos decimales como máximo.");
                } else {
                    litros = numero;
                }
            } catch (NumberFormatException e) {
                System.out.println("Introduce un número válido.");
            }
        }

        //Pedimos el combustible
        String combustible = "";

        while (combustible.isBlank()) {
            System.out.print("Introduce el combustible: ");
            combustible = scanner.nextLine().trim();

            if (!combustible.equalsIgnoreCase("Gasolina 95") && !combustible.equalsIgnoreCase("Gasolina 98") && !combustible.equalsIgnoreCase("Diesel")) {
                System.out.println("Debes introducir uno de los combustibles disponibles:");
                System.out.println("1. Gasolina 95");
                System.out.println("2. Gasolina 98");
                System.out.println("3. Diesel");
                combustible = "";
            }
        }


        pagoJson.guardarPago(idCliente, fecha, importe, litros, combustible);
    }


    // Listar todos los pagos
    public List<Pago> listarPagos() {

        return pagoJson.obtenerTodos();
    }
}
