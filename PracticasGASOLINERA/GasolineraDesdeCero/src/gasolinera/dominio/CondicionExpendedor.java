package gasolinera.dominio;

import java.time.LocalDate;
import java.util.Objects;

/** Acreditación para manipular combustibles en un periodo de expendedor. */
public final class CondicionExpendedor extends CondicionFuncion {

    private String numeroAcreditacion;
    private LocalDate caducidadAcreditacion;

    public CondicionExpendedor(int id, String numeroAcreditacion, LocalDate caducidadAcreditacion) {
        super(id);
        setNumeroAcreditacion(numeroAcreditacion);
        setCaducidadAcreditacion(caducidadAcreditacion);
    }

    @Override
    public TipoFuncion getTipo() {
        return TipoFuncion.EXPENDEDOR;
    }

    @Override
    public String getResumen() {
        return "Acreditación " + numeroAcreditacion + " (caduca el " + caducidadAcreditacion + ")";
    }

    public String getNumeroAcreditacion() {
        return numeroAcreditacion;
    }

    public void setNumeroAcreditacion(String numeroAcreditacion) {
        this.numeroAcreditacion = Validaciones.textoObligatorio(numeroAcreditacion, "El número de acreditación");
    }

    public LocalDate getCaducidadAcreditacion() {
        return caducidadAcreditacion;
    }

    public void setCaducidadAcreditacion(LocalDate caducidadAcreditacion) {
        this.caducidadAcreditacion = Objects.requireNonNull(
                caducidadAcreditacion, "La caducidad de la acreditación no puede ser null.");
    }
}
