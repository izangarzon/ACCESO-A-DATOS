package gasolinera.dominio;

import java.math.BigDecimal;

/** Demuestra la venta y validación de productos envasados y a granel. */
public final class DemostracionVentaProductos {

    private DemostracionVentaProductos() {
    }

    public static void main(String[] args) {
        System.out.println("=== Compra de producto envasado ===");
        ProductoEnvasado envasado = new ProductoEnvasado(
                1, "ACEITE-1L", "Aceite motor", "8410000000001",
                new BigDecimal("1.000"), "Botella de 1 litro",
                UnidadMedida.UNIDAD, true);
        BigDecimal unidades = new BigDecimal("3");
        BigDecimal precioUnidad = new BigDecimal("2.345");
        BigDecimal importeEnvasado = envasado.calcularImporte(unidades, precioUnidad);
        System.out.println("Producto: " + envasado.getNombre() + " (" + envasado.getFormatoEnvase() + ")");
        System.out.println("Compra: " + unidades + " unidades x " + precioUnidad);
        System.out.println("Importe: " + importeEnvasado + " €");

        System.out.println("\n=== Compra de producto a granel ===");
        ProductoGranel granel = new ProductoGranel(
                2, "ALMENDRA-KG", "Almendra a granel", UnidadMedida.KILOGRAMO, 2, true);
        BigDecimal kilogramos = new BigDecimal("1.25");
        BigDecimal precioKilogramo = new BigDecimal("4.00");
        BigDecimal importeGranel = granel.calcularImporte(kilogramos, precioKilogramo);
        System.out.println("Producto: " + granel.getNombre()
                + " (precisión admitida: " + granel.getPrecisionPesada() + " decimales)");
        System.out.println("Compra: " + kilogramos + " kg x " + precioKilogramo);
        System.out.println("Importe: " + importeGranel + " €");

        System.out.println("\n=== Pruebas de cantidades no válidas ===");
        comprobarFallo(
                () -> envasado.calcularImporte(new BigDecimal("1.5"), precioUnidad),
                "número entero positivo");
        comprobarFallo(
                () -> new ProductoGranel(
                        3, "HARINA-KG", "Harina a granel", UnidadMedida.KILOGRAMO, 4, true),
                "entre 0 y 3 decimales");

        System.out.println("Se rechazó correctamente la cantidad decimal para el producto envasado.");
        System.out.println("Se rechazó correctamente la precisión de pesada no permitida.");
        System.out.println("\nDemostración completada correctamente.");
    }

    private static void comprobarFallo(Runnable operacion, String textoEsperado) {
        try {
            operacion.run();
        } catch (IllegalArgumentException e) {
            if (!e.getMessage().contains(textoEsperado)) {
                throw new AssertionError("Mensaje de error inesperado: " + e.getMessage());
            }
            return;
        }
        throw new AssertionError("Se esperaba un error que contuviera: " + textoEsperado);
    }
}
