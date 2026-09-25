import java.io.IOException;
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
        } catch (IOException e) {
            System.out.println("Error al crear el archivo.");
        }
    }

    @Override
    public void guardarPago(Pago pago) {
        String linea = pago.getId() + ";" + pago.getCliente().getId() + ";" + pago.getFecha() + ";" + pago.getImporte() + ";" + pago.getLitros() + ";" + pago.getCombustible();
        try {
            Files.writeString(archivo, linea + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println("Error al guardar el cliente");
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
            System.out.println("Error al leer los clientes");
        }
        return pagos;
    }

}
