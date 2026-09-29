import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;


public class PagoCsv implements InterfacePago {

    private final Path archivo;

    private final ClienteCsv clienteCsv;
    private final Scanner scanner = new Scanner(System.in);

    // Constructor
    public PagoCsv(String nombreArchivo, ClienteCsv clienteCsv) {
        archivo = Path.of(nombreArchivo);
        this.clienteCsv = clienteCsv;
        crearArchivo();
    }

    //Crea el archivo
    private void crearArchivo() {
        try {
            if (!Files.exists(archivo)) {
                Files.createFile(archivo);
            }
        } catch (IOException e) {
            System.out.println("Error al crear el archivo.");
        }
    }


    @Override
    public void guardarPago(int idCliente) {

        //Buscamos el cliente
        Cliente cliente = clienteCsv.buscarPorId(idCliente);

        if (cliente == null) {
            System.out.println("El identificador no corresponde a ningún cliente.");
            return;
        }

        //Pedimos la fecha
        LocalDate fecha = null;

        while (fecha == null) {
            System.out.print("Introduce la fecha (AAAA-MM-DD): ");
            String textoFecha = scanner.nextLine();

            try {
                fecha = LocalDate.parse(textoFecha);
            } catch (DateTimeParseException e) {
                System.out.println("La fecha no es válida.");
            }
        }

        //Pedimos el importe
        BigDecimal importe = null;

        while (importe == null) {
            System.out.print("Introduce el importe: ");
            String textoImporte = scanner.nextLine();

            try {
                importe = new BigDecimal(textoImporte);

                if (importe.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("El importe debe ser mayor que 0.");
                    importe = null;
                }

            } catch (NumberFormatException e) {
                System.out.println("El importe debe ser un número válido.");
            }
        }

        //Pedimos los litros
        BigDecimal litros = null;

        while (litros == null) {
            System.out.print("Introduce los litros: ");
            String textoLitros = scanner.nextLine();

            try {
                litros = new BigDecimal(textoLitros);

                if (litros.compareTo(BigDecimal.ZERO) <= 0) {
                    System.out.println("Los litros deben ser mayores que 0.");
                    litros = null;
                }
            } catch (NumberFormatException e) {

                System.out.println("Los litros deben ser un número válido.");
            }
        }

        //Pedimos el combustible
        String combustible = "";

        while (combustible.isBlank()) {
            System.out.print("Introduce el combustible: ");
            combustible = scanner.nextLine().trim();

            if (combustible.isBlank()) {
                System.out.println("El combustible no puede estar vacío.");
            }
        }

        //Obtenemos el ID del pago.
        int nuevoId = 1;

        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    String[] datos = linea.split(";");
                    int id = Integer.parseInt(datos[0]);

                    if (id >= nuevoId) {
                        nuevoId = id + 1;
                    }
                }
            }

            Pago pago = new Pago(nuevoId, cliente, fecha, importe, litros, combustible);

            //Guardamos el pago en el CSV
            String linea = pago.getId() + ";" + pago.getCliente().getId() + ";" + pago.getFecha() + ";" + pago.getImporte() + ";" + pago.getLitros() + ";" + pago.getCombustible();

            Files.writeString(archivo, linea + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);

            System.out.println();
            System.out.println("Pago registrado correctamente.");
            System.out.println("Identificador del pago: " + pago.getId());
            System.out.println("Cliente: " + cliente.getNombre());
            System.out.println("Importe: " + pago.getImporte());
        } catch (IOException e) {
            System.out.println("Error al obtener el ID del pago.");
        }
    }


    @Override
    public List<Pago> obtenerTodos() {
        List<Pago> pagos = new ArrayList<>();

        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    String[] datos = linea.split(";");
                    int id = Integer.parseInt(datos[0]);
                    int idCliente = Integer.parseInt(datos[1]);
                    LocalDate fecha = LocalDate.parse(datos[2]);
                    BigDecimal importe = new BigDecimal(datos[3]);
                    BigDecimal litros = new BigDecimal(datos[4]);
                    String combustible = datos[5];

                    Cliente cliente = clienteCsv.buscarPorId(idCliente);

                    Pago pago = new Pago(id, cliente, fecha, importe, litros, combustible);
                    pagos.add(pago);

                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer los pagos");
        }
        return pagos;
    }

}
