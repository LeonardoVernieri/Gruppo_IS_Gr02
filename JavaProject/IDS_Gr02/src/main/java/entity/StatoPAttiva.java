package entity;

import java.time.LocalTime;

/**
 * Stato concreto {@code ATTIVA} nel pattern State (vedi {@link StatoPrenotazione}).
 * <p>
 * È l'unico stato dal quale la conferma tramite check-in è ammessa: una
 * prenotazione attiva può transire a {@code CONFERMATA}.
 */
public class StatoPAttiva implements StatoPrenotazione {

    /**
     * Conferma la prenotazione: la porta nello stato {@code CONFERMATA} e
     * registra l'orario di check-in al momento corrente.
     *
     * @param prenotazione prenotazione attiva da confermare
     */
    @Override
    public void conferma(Prenotazione prenotazione) {
        prenotazione.setStato(StatoPrenotazioneEnum.CONFERMATA);
        prenotazione.setDataCheckIn(LocalTime.now());
    }
}