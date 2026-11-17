import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionBD {


    public static void main(String[] args) {
        String url = System.getenv("GASOLINERA_MYSQL_URL");
        String usuario = System.getenv("GASOLINERA_MYSQL_USER");
        String clave = System.getenv("GASOLINERA_MYSQL_PASSWORD");

        if (url == null || url.isBlank()
                || usuario == null || usuario.isBlank()
                || clave == null) {
            System.err.println("Configura GASOLINERA_MYSQL_URL, GASOLINERA_MYSQL_USER y GASOLINERA_MYSQL_PASSWORD.");
            return;
        }

        try (Connection conexion = DriverManager.getConnection(url, usuario, clave)) {

            DatabaseMetaData datos = conexion.getMetaData();

            System.out.println("Gestor: " + datos.getDatabaseProductName());
            System.out.println("Versión del gestor: " + datos.getDatabaseProductVersion());
            System.out.println("Driver: " + datos.getDriverName());
            System.out.println("Conexión válida: " + conexion.isValid(2));

        } catch (SQLException e) {

            System.err.println("Error al conectar con MySQL: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());

        }
    }
}
