public class MigraCSVToJson {

    public static void convertir() {

        I_AlmacenamientoCliente leerDeCSV = new AlmacenamientoClienteCsv("clientes.csv");
        I_AlmacenamientoCliente escribirJSON = new AlmacenamientoClienteJson("clientes.json");

        for (Cliente c : leerDeCSV.obtenerTodos())
            escribirJSON.guardarCliente(c);

        I_AlmacenamientoPago leerPagosCSV = new AlmacenamientoPagoCsv("pagos.csv", leerDeCSV);
        I_AlmacenamientoPago escribirPagosJSON = new AlmacenamientoPagoJson("pagos.json", escribirJSON);

        for (Pago p : leerPagosCSV.obtenerTodos()) {
            escribirPagosJSON.guardarPago(p);
        }
    }
}
