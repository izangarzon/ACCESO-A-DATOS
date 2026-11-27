package gasolinera.dominio;

import java.util.Objects;

/** Ficha de configuración de la emisión de tickets de una estación. */
public class ConfiguracionFacturacion {

    private final int id;
    private final String codigo;
    private final Estacion estacion;
    private String serieTickets;
    private int siguienteNumero;
    private String textoPie;

    public ConfiguracionFacturacion(int id, String codigo, Estacion estacion,
                                    String serieTickets, int siguienteNumero, String textoPie) {
        this.id = Validaciones.enteroPositivo(id, "El ID");
        this.codigo = Validaciones.textoObligatorio(codigo, "El código de configuración");
        this.estacion = Objects.requireNonNull(estacion, "La estación no puede ser null.");
        setSerieTickets(serieTickets);
        setSiguienteNumero(siguienteNumero);
        setTextoPie(textoPie);
    }

    /** Devuelve el número que debe usar el siguiente ticket y deja preparado el siguiente. */
    public int reservarNumeroTicket() {
        return siguienteNumero++;
    }

    public int getId() {
        return id;
    }

    public String getCodigo() {
        return codigo;
    }

    public Estacion getEstacion() {
        return estacion;
    }

    public String getSerieTickets() {
        return serieTickets;
    }

    public void setSerieTickets(String serieTickets) {
        this.serieTickets = Validaciones.textoObligatorio(serieTickets, "La serie de tickets");
    }

    public int getSiguienteNumero() {
        return siguienteNumero;
    }

    public void setSiguienteNumero(int siguienteNumero) {
        this.siguienteNumero = Validaciones.enteroPositivo(siguienteNumero, "El siguiente número");
    }

    public String getTextoPie() {
        return textoPie;
    }

    public void setTextoPie(String textoPie) {
        this.textoPie = Validaciones.textoObligatorio(textoPie, "El texto al pie");
    }
}
