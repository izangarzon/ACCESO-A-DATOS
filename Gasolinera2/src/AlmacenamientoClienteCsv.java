import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class AlmacenamientoClienteCsv implements I_AlmacenamientoCliente {

    private final Path archivo;

    //Constructor
    public AlmacenamientoClienteCsv(String nombreArchivo) {
        archivo = Path.of(nombreArchivo);
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

    //OVERRIDES
    @Override
    public void guardarCliente(String nombre, String telefono, String matricula) {

        // Comprobamos si la matrícula existe
        if (existeMatricula(matricula)) {
            System.out.println("La matrícula ya está registrada.");
            return;
        }

        int nuevoId = 1;

        try {

            // Generamos el nuevo id correspondiente
            if (Files.exists(archivo)) {
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
            }

            // Creamos el nuevo cliente
            matricula = matricula.toUpperCase();
            Cliente cliente = new Cliente(nuevoId, nombre, telefono, matricula);

            // Guardamos en el CSV
            String linea = cliente.getId() + "," + cliente.getNombre() + "," + cliente.getTelefono() + "," + cliente.getMatricula();

            Files.writeString(archivo, linea + System.lineSeparator(), StandardCharsets.UTF_8, StandardOpenOption.APPEND);

            // Mostramos el resultado
            System.out.println("Cliente guardado correctamente.");
            System.out.println("El identificador del cliente es: " + nuevoId);

        } catch (Exception e) {
            System.out.println("Error al guardar el cliente." + e.getMessage());
        }
    }


    @Override
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();

        // Leemos el archivo y mostramos de uno en uno todos los clientes listados
        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    String[] datos = linea.split(",");
                    int id = Integer.parseInt(datos[0]);
                    String nombre = datos[1];
                    String telefono = datos[2];
                    String matricula = datos[3];

                    Cliente cliente = new Cliente(id, nombre, telefono, matricula);
                    clientes.add(cliente);

                }
            }

        } catch (Exception e) {
            System.out.println("Error al leer los clientes" + e.getMessage());
        }

        clientes.sort(Comparator.comparing(Cliente::getNombre, String.CASE_INSENSITIVE_ORDER).thenComparing(Cliente::getId));

        return clientes;
    }

    @Override
    public List<Cliente> buscar(String busqueda) {
        List<Cliente> clientes = new ArrayList<>();

        String minusculas = busqueda.toLowerCase();

        // Recorremos el array buscando coincidencias con la palabra buscada y si se encuentra se muestra ese cliente
        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    String[] datos = linea.split(",");
                    int id = Integer.parseInt(datos[0]);
                    String nombre = datos[1];
                    String telefono = datos[2];
                    String matricula = datos[3];

                    Cliente cliente = new Cliente(id, nombre, telefono, matricula);


                    if (cliente.getNombre().toLowerCase().contains(minusculas)
                            || cliente.getTelefono().toLowerCase().contains(minusculas)
                            || cliente.getMatricula().toLowerCase().contains(minusculas)) {

                        clientes.add(cliente);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar cliente" + e.getMessage());
        }

        clientes.sort(Comparator.comparing(Cliente::getNombre, String.CASE_INSENSITIVE_ORDER).thenComparing(Cliente::getId));

        return clientes;
    }

    @Override
    public boolean existeMatricula(String matricula) {
        List<Cliente> clientes = obtenerTodos();

        for (Cliente cliente : clientes) {
            if (cliente.getMatricula().equalsIgnoreCase(matricula)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Cliente buscarPorId(int idBuscado) {

        // Busca
        try {
            List<String> lineas = Files.readAllLines(archivo, StandardCharsets.UTF_8);

            for (String linea : lineas) {
                if (!linea.isBlank()) {

                    String[] datos = linea.split(",");

                    int id = Integer.parseInt(datos[0]);

                    if (id == idBuscado) {
                        String nombre = datos[1];
                        String telefono = datos[2];
                        String matricula = datos[3];

                        return new Cliente(id, nombre, telefono, matricula);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error al buscar cliente" + e.getMessage());
        }
        return null;
    }
}
