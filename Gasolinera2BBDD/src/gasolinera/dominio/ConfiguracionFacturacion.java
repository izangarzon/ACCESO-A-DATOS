package gasolinera.dominio;

import java.util.Objects;

public class ConfiguracionFacturacion {

    private final int id;
    private final Estacion estacion;
    private String razonSocial;
    private String nif;
    private String direccionFiscal;
    private String emailFacturacion;
    private String serieFactura;
    private int siguienteNumero;

    public ConfiguracionFacturacion(int id, Estacion estacion, String razonSocial, String nif,
                                    String direccionFiscal, String emailFacturacion,
                                    String serieFactura, int siguienteNumero) {
        if (id <= 0 || siguienteNumero <= 0) {
            throw new IllegalArgumentException("El ID y el siguiente número deben ser positivos.");
        }
        this.id = id;
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
        setRazonSocial(razonSocial);
        setNif(nif);
        setDireccionFiscal(direccionFiscal);
        setEmailFacturacion(emailFacturacion);
        setSerieFactura(serieFactura);
        this.siguienteNumero = siguienteNumero;
    }

    public int reservarNumeroFactura() {
        return siguienteNumero++;
    }

    public int getId() { return id; }
    public Estacion getEstacion() { return estacion; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = Producto.validarTexto(razonSocial, "La razón social"); }
    public String getNif() { return nif; }
    public void setNif(String nif) { this.nif = Producto.validarTexto(nif, "El NIF"); }
    public String getDireccionFiscal() { return direccionFiscal; }
    public void setDireccionFiscal(String direccionFiscal) { this.direccionFiscal = Producto.validarTexto(direccionFiscal, "La dirección fiscal"); }
    public String getEmailFacturacion() { return emailFacturacion; }
    public void setEmailFacturacion(String emailFacturacion) { this.emailFacturacion = emailFacturacion == null || emailFacturacion.isBlank() ? null : emailFacturacion.trim(); }
    public String getSerieFactura() { return serieFactura; }
    public void setSerieFactura(String serieFactura) { this.serieFactura = Producto.validarTexto(serieFactura, "La serie de factura"); }
    public int getSiguienteNumero() { return siguienteNumero; }
    public void setSiguienteNumero(int siguienteNumero) {
        if (siguienteNumero <= 0) throw new IllegalArgumentException("El siguiente número debe ser positivo.");
        this.siguienteNumero = siguienteNumero;
    }
}
