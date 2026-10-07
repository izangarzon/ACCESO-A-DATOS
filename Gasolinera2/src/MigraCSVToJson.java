import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class MigraCSVToJson {

    public static void convertir() {

        Path clientesCsv = Path.of("clientes.csv");
        Path clientesJson = Path.of("clientes.json");
        Path pagosCsv = Path.of("pagos.csv");
        Path pagosJson = Path.of("pagos.json");

        try {
            if (Files.exists(clientesCsv) && Files.size(clientesJson) > 0) {
                System.out.println("Error: el archivo JSON ya contiene datos.");
                System.out.println("No se han trasladado los datos.");
                return;
            }

            List<String> lineas = Files.readAllLines(clientesCsv, StandardCharsets.UTF_8);


            for (String linea : lineas) {

                if (!linea.isBlank()) {

                    String[] datos = linea.split(",");

                    String cliente = "{\"Id\": \"" + datos[0] + "\"," + "\"nombre\": \"" + datos[1] + "\"," + "\"telefono\": \"" + datos[2] + "\"," + "\"matricula\": \"" + datos[3]+"\"},";

                    Files.writeString(clientesJson, cliente + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
                }
            }
            if (Files.exists(pagosCsv) && Files.size(pagosJson) > 0) {
                System.out.println("Error: el archivo JSON ya contiene datos.");
                System.out.println("No se han trasladado los datos.");
                return;
            }

            List<String> lineas2 = Files.readAllLines(pagosCsv, StandardCharsets.UTF_8);


            for (String linea : lineas2) {

                if (!linea.isBlank()) {

                    String[] datos = linea.split(",");

                    String pago = "{\"Id\": \"" + datos[0] + "\"," + "\"Idcliente\": \"" + datos[1] + "\"," + "\"fecha\": \"" + datos[2] + "\"," + "\"importe\": \"" + datos[3] + "\"," + "\"Litros\": \"" + datos[4] + "\"," + "\"Combustible\": \"" + datos[5]+"\"},";

                    Files.writeString(pagosJson, pago + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);
                }
            }

            System.out.println("Datos trasladados correctamente.");


        } catch (Exception e) {
            System.out.println("Error al trasladar los datos: " + e.getMessage());
        }
    }
}
