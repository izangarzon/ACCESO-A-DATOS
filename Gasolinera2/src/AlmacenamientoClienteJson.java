import java.io.BufferedReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class AlmacenamientoClienteJson implements I_AlmacenamientoCliente {

    private final Path archivo;

    //Constructor
    public AlmacenamientoClienteJson(String nombreArchivo) {
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

        for (Cliente cliente : obtenerTodos()) {
            if (cliente.getId() >= nuevoId) {
                nuevoId = cliente.getId() + 1;
            }
        }

        Cliente cliente = new Cliente(nuevoId, nombre, telefono, matricula.toUpperCase());

        String linea = "{\"Id\": \"" + cliente.getId() + "\"," + "\"nombre\": \"" + cliente.getNombre() + "\"," + "\"telefono\": \"" + cliente.getTelefono() + "\"," + "\"matricula\": \"" + cliente.getMatricula() + "\"}";

        try {
            String contenido = Files.readString(archivo, StandardCharsets.UTF_8).trim();

            if (contenido.isEmpty() || contenido.equals("[]")) {
                contenido = "{" + System.lineSeparator() + "  \"clientes\": [" + System.lineSeparator() + linea + System.lineSeparator() + "  ]" + System.lineSeparator() + "}";
            } else {
                int posicion = contenido.lastIndexOf("]");

                if (posicion == -1) {
                    System.out.println("El archivo JSON no tiene un formato válido.");
                    return;
                }

                String clientes = contenido.substring(0, posicion).trim();

                if (!clientes.endsWith("[")) {
                    clientes += ",";
                }

                contenido = clientes + System.lineSeparator() + linea + System.lineSeparator() + "  ]" + System.lineSeparator() + "}";
            }

            Files.writeString(archivo, contenido, StandardCharsets.UTF_8);

            System.out.println("Cliente guardado correctamente.");
            System.out.println("El identificador del cliente es: " + nuevoId);

        } catch (Exception e) {
            System.out.println("Error al guardar el cliente: " + e.getMessage());
        }
    }

    @Override
    public void guardarCliente(Cliente c) {
        this.guardarCliente(c.getNombre(), c.getTelefono(), c.getMatricula());

    }


    @Override
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();

        // Leemos el archivo y mostramos de uno en uno todos los clientes listados
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.startsWith("{\"Id\"")) {
                    String[] datos = linea.split("\"");

                    int id = Integer.parseInt(datos[3].trim());
                    String nombre = datos[7].trim();
                    String telefono = datos[11].trim();
                    String matricula = datos[15].trim();

                    Cliente cliente = new Cliente(id, nombre, telefono, matricula);
                    clientes.add(cliente);

                }
            }

        } catch (Exception e) {
            System.out.println("Error al leer los clientes" + e.getMessage());
        }

        clientes.sort((o1, o2) -> {
            if (o1.getNombre().compareToIgnoreCase(o2.getNombre()) == 0) {
                return Integer.compare(o1.getId(), o2.getId());
            }

            return o1.getNombre().compareToIgnoreCase(o2.getNombre());
        });

        return clientes;
    }

    @Override
    public List<Cliente> buscar(String busqueda) {
        List<Cliente> clientes = new ArrayList<>();

        String minusculas = busqueda.toLowerCase();

        // Recorremos el array buscando coincidencias con la palabra buscada y si se encuentra se muestra ese cliente
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.startsWith("{\"Id\"")) {
                    String[] datos = linea.split("\"");

                    int id = Integer.parseInt(datos[3].trim());
                    String nombre = datos[7].trim();
                    String telefono = datos[11].trim();
                    String matricula = datos[15].trim();

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

        clientes.sort((o1, o2) -> {
            if (o1.getNombre().compareToIgnoreCase(o2.getNombre()) == 0) {
                return Integer.compare(o1.getId(), o2.getId());
            }

            return o1.getNombre().compareToIgnoreCase(o2.getNombre());
        });

        return clientes;
    }

    @Override
    public boolean existeMatricula(String matricula) {
        List<Cliente> clientes = obtenerTodos();

        for (Cliente cliente : clientes) {
            if (cliente.getMatricula().equalsIgnoreCase(matricula.trim())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Cliente buscarPorId(int idBuscado) {

        // Busca
        try (BufferedReader lector = Files.newBufferedReader(archivo, StandardCharsets.UTF_8)) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (linea.startsWith("{\"Id\"")) {
                    String[] datos = linea.split("\"");

                    int id = Integer.parseInt(datos[3].trim());

                    if (id == idBuscado) {
                        String nombre = datos[7].trim();
                        String telefono = datos[11].trim();
                        String matricula = datos[15].trim();

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
