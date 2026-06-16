package dto;

import java.time.LocalTime;

/**
 * Data Transfer Object che rappresenta una fascia oraria prenotabile,
 * delimitata da un'ora di inizio e una di fine.
 * <p>
 * Le fasce non sono entità persistite: sono generate dinamicamente dalla
 * {@code SalaStudio} come slot di un'ora a partire dagli orari di apertura e
 * chiusura, e viaggiano tra i vari livelli come semplice contenitore immutabile
 * di due {@link LocalTime}. L'immutabilità (campi {@code final}) ne consente
 * l'uso sicuro come chiave nelle mappe di disponibilità.
 */
public class FasciaOraria {

    private final LocalTime oraInizio;
    private final LocalTime oraFine;

    public FasciaOraria(LocalTime oraInizio, LocalTime oraFine) {
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
    }

    /** Rappresentazione testuale della fascia nel formato {@code inizio - fine}. */
    @Override
    public String toString() {
        return oraInizio + " - " + oraFine;
    }

    public LocalTime getOraInizio() { return oraInizio; }
    public LocalTime getOraFine() { return oraFine; }
}