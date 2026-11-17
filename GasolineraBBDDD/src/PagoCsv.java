import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;


public class PagoCsv implements InterfacePago {

    private final Path archivo;

    private final ClienteCsv clienteCsv;

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
        } catch (Exception e) {
            System.out.println("Error al crear el archivo." + e.getMessage());
        }
    }


    @Override
    public void guardarPago(int idCliente, LocalDate fecha, BigDecimal importe, BigDecimal litros, String combustible) {

        //Buscamos el cliente
        Cliente cliente = clienteCsv.buscarPorId(idCliente);

        //Obtenemos el ID del pago.
        int nuevoId = 1;

        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    String[] datos = linea.split(",");
                    int id = Integer.parseInt(datos[0]);

                    if (id >= nuevoId) {
                        nuevoId = id + 1;
                    }
                }
            }

            Pago pago = new Pago(nuevoId, cliente, fecha, importe, litros, combustible);

            //Guardamos el pago en el CSV
            String linea = pago.getId() + "," + pago.getCliente().getId() + "," + pago.getFecha() + "," + pago.getImporte() + "," + pago.getLitros() + "," + pago.getCombustible();

            Files.writeString(archivo, linea + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);

            System.out.println();
            System.out.println("Pago registrado correctamente.");
            System.out.println("Identificador del pago: " + pago.getId());
            System.out.println("Cliente: " + cliente.getNombre());
            System.out.println("Importe: " + String.format(Locale.US, "%.2f", pago.getImporte()) + "€");
        } catch (Exception e) {
            System.out.println("Error al obtener el ID del pago." + e.getMessage());
        }
    }


    @Override
    public List<Pago> obtenerTodos() {
        List<Pago> pagos = new ArrayList<>();

        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    String[] datos = linea.split(",");
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

        } catch (Exception e) {
            System.out.println("Error al leer los pagos" + e.getMessage());
        }

        pagos.sort(Comparator.comparing(Pago::getFecha).reversed().thenComparing(Comparator.comparing(Pago::getId).reversed()));

        return pagos;
    }

}
