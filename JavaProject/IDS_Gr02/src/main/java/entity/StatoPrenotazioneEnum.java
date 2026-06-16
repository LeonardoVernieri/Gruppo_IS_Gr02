package entity;

/**
 * Stati persistibili di una {@link Prenotazione}.
 * <p>
 * Rappresenta la vista persistente dello stato: viene salvato su database come
 * stringa ({@code @Enumerated(EnumType.STRING)}) ed è la sorgente da cui, al
 * caricamento, viene ricostruito l'oggetto-stato comportamentale
 * ({@link StatoPrenotazione}).
 */
public enum StatoPrenotazioneEnum {

    /** Prenotazione effettuata e valida, in attesa di check-in. */
    ATTIVA,

    /** Check-in non effettuato entro l'intervallo previsto: postazione liberata. */
    SCADUTA,

    /** Prenotazione annullata dallo studente entro il limite consentito. */
    ANNULLATA,

    /** Presenza confermata tramite check-in. */
    CONFERMATA
}