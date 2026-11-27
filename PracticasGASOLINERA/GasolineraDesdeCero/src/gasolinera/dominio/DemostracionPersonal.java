package gasolinera.dominio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Demuestra funciones simultáneas, historial, supervisión y reglas rechazadas. */
public final class DemostracionPersonal {

    private DemostracionPersonal() {
    }

    public static void main(String[] args) {
        Estacion norte = new Estacion(1, "EST-001", "Gasolinera Norte", "Calle Mayor 10",
                "910000001", LocalDate.of(2010, 5, 15));
        Estacion sur = new Estacion(2, "EST-002", "Gasolinera Sur", "Avenida del Sur 25",
                "910000002", LocalDate.of(2018, 9, 1));

        Cajero cajero = new Cajero(1, "F-CAJ", "Cajero", "Atención en caja y devoluciones.");
        Expendedor expendedor = new Expendedor(2, "F-EXP", "Expendedor", "Manipulación de combustibles.");
        ResponsableTienda responsable = new ResponsableTienda(3, "F-RTI", "Responsable de tienda", "Compras.");
        JefeEstacion jefe = new JefeEstacion(4, "F-JEF", "Jefe de estación", "Operaciones excepcionales.");

        Empleado ana = new Empleado(1, "EMP-001", "11111111A", "Ana", "600000001", null,
                LocalDate.of(2019, 3, 1), norte);
        Empleado luis = new Empleado(2, "EMP-002", "22222222B", "Luis", null, "luis@correo.com",
                LocalDate.of(2021, 6, 15), norte);
        Empleado marta = new Empleado(3, "EMP-003", "33333333C", "Marta", "600000003", null,
                LocalDate.of(2022, 1, 10), sur);
        Empleado sinFunciones = new Empleado(4, "EMP-004", "44444444D", "Nueva incorporación",
                "600000004", null, LocalDate.of(2026, 1, 5), sur);
        Empleado ultimo = new Empleado(5, "EMP-005", "55555555E", "Último", "600000005", null,
                LocalDate.of(2025, 2, 1), sur);

        luis.setSupervisor(ana);
        marta.setSupervisor(ana);
        ultimo.setSupervisor(marta);

        // Ana tiene dos funciones distintas a la vez.
        ana.asignarFuncion(1, cajero, new CondicionCajero(1, new BigDecimal("150.00")),
                LocalDateTime.of(2022, 1, 1, 0, 0));
        ana.asignarFuncion(2, expendedor,
                new CondicionExpendedor(2, "ACR-123", LocalDate.of(2027, 6, 30)),
                LocalDateTime.of(2023, 1, 1, 0, 0));

        // Luis dejó de ser jefe y recuperó la función después con otro nivel.
        luis.asignarFuncion(3, jefe, new CondicionJefeEstacion(3, 1),
                LocalDateTime.of(2022, 1, 1, 0, 0), LocalDateTime.of(2022, 12, 31, 0, 0));
        luis.asignarFuncion(4, responsable, new CondicionResponsableTienda(4, new BigDecimal("5000.00")),
                LocalDateTime.of(2023, 5, 1, 0, 0));
        luis.asignarFuncion(5, jefe, new CondicionJefeEstacion(5, 3),
                LocalDateTime.of(2025, 1, 1, 0, 0));

        marta.asignarFuncion(6, cajero, new CondicionCajero(6, new BigDecimal("100.00")),
                LocalDateTime.of(2022, 2, 1, 0, 0));

        System.out.println("=== Personal ===");
        imprimirEmpleado(ana);
        imprimirEmpleado(luis);
        imprimirEmpleado(marta);
        imprimirEmpleado(sinFunciones);
        imprimirEmpleado(ultimo);

        System.out.println("\n=== Reglas comprobadas en el modelo Java ===");
        comprobarFallo(
                () -> ana.asignarFuncion(10, cajero, new CondicionCajero(10, new BigDecimal("200.00")),
                        LocalDateTime.of(2024, 1, 1, 0, 0)),
                "solapado");
        comprobarFallo(() -> ana.setSupervisor(ana), "sí mismo");
        comprobarFallo(() -> ana.setSupervisor(ultimo), "ciclo");
        comprobarFallo(
                () -> new Empleado(11, "EMP-011", "66666666F", "Sin contacto", null, null,
                        LocalDate.now(), norte),
                "al menos un medio de contacto");
        comprobarFallo(
                () -> new AsignacionFuncion(12, ana, cajero, new CondicionJefeEstacion(12, 1),
                        LocalDateTime.of(2026, 1, 1, 0, 0), null),
                "no corresponde");

        System.out.println("Se rechazaron correctamente las cinco operaciones.");
        System.out.println("\nDemostración completada correctamente.");
    }

    private static void imprimirEmpleado(Empleado empleado) {
        String supervisor = empleado.getSupervisor() == null
                ? "ninguno" : empleado.getSupervisor().getNombre();
        System.out.println(empleado.getCodigo() + " - " + empleado.getNombre()
                + " | " + empleado.getDni() + " | incorporación: " + empleado.getFechaIncorporacion()
                + " | estación: " + empleado.getEstacion().getNombre()
                + " | supervisor: " + supervisor);
        if (empleado.getAsignacionesVigentes().isEmpty()) {
            System.out.println("  Sin funciones vigentes.");
        }
        for (AsignacionFuncion asignacion : empleado.getAsignacionesVigentes()) {
            System.out.println("  " + asignacion.getFuncion().getNombre() + " desde " + asignacion.getDesde()
                    + " | " + asignacion.getCondicion().getResumen());
        }
        System.out.println("  Total de periodos históricos: " + empleado.getAsignaciones().size());
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
