package gasolinera.dominio;

import java.time.LocalDate;

/** Demuestra la creación de estaciones, su ficha de facturación y sus surtidores. */
public final class DemostracionEstaciones {

    private DemostracionEstaciones() {
    }

    public static void main(String[] args) {
        Estacion norte = new Estacion(1, "EST-001", "Gasolinera Norte",
                "Calle Mayor 10", "910000001", LocalDate.of(2010, 5, 15));
        norte.configurarFacturacion(new ConfiguracionFacturacion(
                1, "CFG-001", norte, "TN", 1, "Gracias por su visita"));
        norte.agregarSurtidor(new Surtidor(1, norte, "EQ-001", 1,
                TipoCombustible.GASOLINA_95, EstadoServicio.OPERATIVO));
        norte.agregarSurtidor(new Surtidor(2, norte, "EQ-002", 2,
                TipoCombustible.DIESEL, EstadoServicio.AVERIADO));

        Estacion sur = new Estacion(2, "EST-002", "Gasolinera Sur",
                "Avenida del Sur 25", "910000002", LocalDate.of(2018, 9, 1));
        sur.configurarFacturacion(new ConfiguracionFacturacion(
                2, "CFG-002", sur, "TS", 1, "Atención al cliente: 910000002"));
        sur.agregarSurtidor(new Surtidor(3, sur, "EQ-003", 1,
                TipoCombustible.GASOLINA_98, EstadoServicio.OPERATIVO));

        System.out.println("=== Estaciones dadas de alta ===");
        imprimirEstacion(norte);
        imprimirEstacion(sur);

        System.out.println("\n=== Uso de la numeración de tickets ===");
        System.out.println("Primer ticket: " + norte.getConfiguracionFacturacion().reservarNumeroTicket());
        System.out.println("Siguiente ticket: " + norte.getConfiguracionFacturacion().getSiguienteNumero());

        System.out.println("\n=== Reglas comprobadas en el modelo Java ===");
        comprobarFallo(
                () -> norte.agregarSurtidor(new Surtidor(4, norte, "EQ-004", 1,
                        TipoCombustible.DIESEL, EstadoServicio.OPERATIVO)),
                "número visible");
        comprobarFallo(
                () -> sur.quitarSurtidor(sur.getSurtidores().get(0)),
                "al menos un surtidor");
        comprobarFallo(
                () -> norte.configurarFacturacion(new ConfiguracionFacturacion(
                        3, "CFG-003", norte, "TN", 1, "Otro pie")),
                "ya tiene una configuración");

        System.out.println("Se rechazaron correctamente las tres operaciones.");
        System.out.println("\nDemostración completada correctamente.");
    }

    private static void imprimirEstacion(Estacion estacion) {
        System.out.println(estacion.getCodigo() + " - " + estacion.getNombre()
                + " | " + estacion.getDireccion() + " | " + estacion.getTelefono()
                + " | apertura: " + estacion.getFechaApertura());
        ConfiguracionFacturacion configuracion = estacion.getConfiguracionFacturacion();
        System.out.println("  Ficha " + configuracion.getCodigo() + ": serie " + configuracion.getSerieTickets()
                + ", siguiente número " + configuracion.getSiguienteNumero()
                + ", pie \"" + configuracion.getTextoPie() + "\"");
        for (Surtidor surtidor : estacion.getSurtidores()) {
            System.out.println("  Surtidor " + surtidor.getCodigoEquipo() + ": número " + surtidor.getNumeroVisible()
                    + ", " + surtidor.getTipoCombustible().getNombre()
                    + ", " + surtidor.getEstadoServicio());
        }
    }

    private static void comprobarFallo(Runnable operacion, String textoEsperado) {
        try {
            operacion.run();
        } catch (IllegalArgumentException | IllegalStateException e) {
            if (!e.getMessage().contains(textoEsperado)) {
                throw new AssertionError("Mensaje de error inesperado: " + e.getMessage());
            }
            return;
        }
        throw new AssertionError("Se esperaba un error que contuviera: " + textoEsperado);
    }
}
