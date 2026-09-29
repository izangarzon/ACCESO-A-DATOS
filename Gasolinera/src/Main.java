import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ClienteCsv clienteCsv = new ClienteCsv("clientes.csv");
        PagoCsv pagoCsv = new PagoCsv("pagos.csv", clienteCsv);

        Gasolinera gasolinera = new Gasolinera(clienteCsv, pagoCsv);

        int opcion = -1;

        while (opcion != 0) {

            System.out.println("===== GASOLINERA =====");
            System.out.println("1. Dar de alta cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Buscar clientes");
            System.out.println("4. Procesar pago");
            System.out.println("5. Consultar pagos");
            System.out.println("0. Salir");

            System.out.print("Elige una opción: ");
            opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {

                case 1:
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine();

                    System.out.print("Teléfono: ");
                    String telefono = sc.nextLine();

                    System.out.print("Matrícula: ");
                    String matricula = sc.nextLine();

                    gasolinera.registrarCliente(nombre, telefono, matricula);
                    break;

                case 2:
                    List<Cliente> clientes = gasolinera.listarClientes();
                    for (Cliente cliente : clientes) {
                        System.out.println(cliente.getId() + "; " + cliente.getNombre() + "; " + cliente.getTelefono() + "; " + cliente.getMatricula());
                    }
                    break;

                case 3:
                    System.out.print("Introduce el texto a buscar: ");
                    String busqueda = sc.nextLine();

                    List<Cliente> clientes2 = gasolinera.listarClientes();
                    for (Cliente cliente : clientes2) {
                        System.out.println(cliente.getId() + "; " + cliente.getNombre() + "; " + cliente.getTelefono() + "; " + cliente.getMatricula());
                    }
                    break;

                case 4:
                    System.out.print("Introduce el ID del cliente: ");
                    int idCliente = Integer.parseInt(sc.nextLine());

                    gasolinera.registrarPago(idCliente);
                    break;

                case 5:
                    List<Pago> pagos = gasolinera.listarPagos();
                    for (Pago pago : pagos){
                        System.out.println(pago.getId() + "; " + pago.getCliente().getNombre() + "; " + pago.getFecha() + "; " + pago.getImporte() + "; " + pago.getLitros() + "; " + pago.getCombustible());
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