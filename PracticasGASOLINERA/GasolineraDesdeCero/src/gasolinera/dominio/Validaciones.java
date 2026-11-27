package gasolinera.dominio;

import java.util.Objects;

/** Comprobaciones sencillas compartidas por las clases del dominio. */
final class Validaciones {

    private Validaciones() {
    }

    static String textoObligatorio(String valor, String campo) {
        Objects.requireNonNull(valor, campo + " no puede ser null.");
        String limpio = valor.trim();
        if (limpio.isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede estar vacío.");
        }
        return limpio;
    }

    static int enteroPositivo(int valor, String campo) {
        if (valor <= 0) {
            throw new IllegalArgumentException(campo + " debe ser positivo.");
        }
        return valor;
    }

    static String textoOpcional(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }
}
