import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ClienteCsv clienteCsv = new ClienteCsv("clientes.csv");
        PagoCsv pagoCsv = new PagoCsv("pagos.csv", clienteCsv);

        Gasolinera gasolinera = new Gasolinera(clienteCsv, pagoCsv);

        int opcion = -1;

        // Mostramos el menu y configuramos sus opciones
        while (opcion != 0) {

            System.out.println("\n===== GASOLINERA =====");
            System.out.println("1. Dar de alta cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Buscar clientes");
            System.out.println("4. Procesar pago");
            System.out.println("5. Consultar pagos");
            System.out.println("0. Salir");

            System.out.print("\nElige una opción: ");

            try {
                opcion = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Introduce un número válido.");
                continue;
            }

            switch (opcion) {

                case 1:
                    gasolinera.registrarCliente();
                    break;

                case 2:
                    List<Cliente> clientes = gasolinera.listarClientes();
                    for (Cliente cliente : clientes) {
                        System.out.println(cliente.getId() + ", " + cliente.getNombre() + ", " + cliente.getTelefono() + ", " + cliente.getMatricula());
                    }
                    break;

                case 3:
                    System.out.print("Introduce el texto a buscar: ");
                    String busqueda = sc.nextLine();

                    List<Cliente> clientes2 = gasolinera.buscarClientes(busqueda);
                    for (Cliente cliente : clientes2) {
                        System.out.println(cliente.getId() + ", " + cliente.getNombre() + ", " + cliente.getTelefono() + ", " + cliente.getMatricula());
                    }
                    break;

                case 4:
                    List<Cliente> clientes3 = gasolinera.listarClientes();
                    for (Cliente cliente : clientes3) {
                        System.out.println(cliente.getId() + ", " + cliente.getNombre() + ", " + cliente.getTelefono() + ", " + cliente.getMatricula());
                    }

                    int idCliente;

                    try {
                        System.out.print("Introduce el ID del cliente: ");
                        idCliente = Integer.parseInt(sc.nextLine());
                    } catch (Exception e) {
                        System.out.println("ID no valido.");
                        break;
                    }

                    gasolinera.registrarPago(idCliente);
                    break;

                case 5:
                    List<Pago> pagos = gasolinera.listarPagos();
                    for (Pago pago : pagos) {
                        System.out.println(pago.getId() + ", " + pago.getCliente().getNombre() + ", " + pago.getFecha() + ", " + String.format(Locale.US, "%.2f", pago.getImporte()) + "€, " + String.format(Locale.US, "%.2f", pago.getLitros()) + ", " + pago.getCombustible());
                    }
                    break;

                case 0:
                    System.out.println("Hasta pronto.");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        }
    }
}