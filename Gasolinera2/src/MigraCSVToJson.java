import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MigraCSVToJson {

    public static void convertir() {

        Path clientesCsv = Paths.get("clientes.csv");
        Path pagosCsv = Paths.get("pagos.csv");

        Path clientesJson = Paths.get("clientes.json");
        Path pagosJson = Paths.get("pagos.json");

        try {
            if (!Files.exists(clientesCsv) || !Files.exists(pagosCsv)) {
                System.out.println("No se ha podido realizar la migración, faltan archivos CSV.");
                return;
            }
            if (Files.exists(clientesJson) && Files.size(clientesJson) > 0 || Files.exists(pagosJson) && Files.size(pagosJson) > 0) {
                System.out.println("No se ha podido realizar la migración, los archivos JSON no están vacíos.");
                return;
            }

        } catch (IOException e) {
            System.out.println("Error al comprobar los archivos.");
            return;
        }


        I_AlmacenamientoCliente leerDeCSV = new AlmacenamientoClienteCsv("clientes.csv");
        I_AlmacenamientoCliente escribirJSON = new AlmacenamientoClienteJson("clientes.json");

        for (Cliente c : leerDeCSV.obtenerTodos())
            escribirJSON.guardarCliente(c);

        I_AlmacenamientoPago leerPagosCSV = new AlmacenamientoPagoCsv("pagos.csv", leerDeCSV);
        I_AlmacenamientoPago escribirPagosJSON = new AlmacenamientoPagoJson("pagos.json", escribirJSON);

        for (Pago p : leerPagosCSV.obtenerTodos()) {
            escribirPagosJSON.guardarPago(p);
        }
        System.out.println("\nMIGRACIONES COMPLETADAS");
        System.out.println("Clientes migrados: " + escribirJSON.obtenerTodos().size());
        System.out.println("Pagos migrados: " + escribirPagosJSON.obtenerTodos().size());
    }
}
