import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Primera conexión: todo el proceso cabe en un método main. */
public final class ConexionMySQL {
    private ConexionMySQL() {
    }

    public static void main(String[] args) {
        String url = valorConfigurado("GASOLINERA_MYSQL_URL", "jdbc:mysql://localhost:3306/ra2_gasolinera");
        String usuario = valorConfigurado("GASOLINERA_MYSQL_USER", "gasolinera");
        String clave = valorConfigurado("GASOLINERA_MYSQL_PASSWORD", "1234");

        if (args != null && args.length == 3) {
            url = args[0];
            usuario = args[1];
            clave = args[2];
        }

        try (Connection conexion = DriverManager.getConnection(url, usuario, clave)) {
            DatabaseMetaData datos = conexion.getMetaData();
            System.out.println("Conexión MySQL abierta: " + url);
            System.out.println("Gestor: " + datos.getDatabaseProductName());
            System.out.println("Versión del gestor: " + datos.getDatabaseProductVersion());
            System.out.println("Driver: " + datos.getDriverName());
            System.out.println("Conexión válida: " + conexion.isValid(2));
            System.out.println("Auto-commit: " + conexion.getAutoCommit());
        } catch (SQLException e) {
            System.err.println("Error al conectar con MySQL: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
            return;
        }
        System.out.println("La conexión se ha cerrado al salir de try-with-resources.");
    }

    private static String valorConfigurado(String variable, String valorPorDefecto) {
        String valor = System.getenv(variable);
        return valor == null || valor.isBlank() ? valorPorDefecto : valor;
    }
}
