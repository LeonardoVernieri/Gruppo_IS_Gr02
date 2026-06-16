package entity;

import database.GestorePersistenza;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Information Expert dell'intera collezione delle {@link Prenotazione}.
 * <p>
 * Punto di accesso unico al catalogo globale delle prenotazioni: incapsula la
 * loro registrazione, l'aggiornamento e il recupero filtrato dalla persistenza.
 * Fa coppia con {@link CatalogoSaleStudio}, mantenendo separato l'IE della
 * collezione globale delle prenotazioni da quello delle sale.
 */
public class CatalogoPrenotazioni {

    private final GestorePersistenza gestorePersistenza;

    public CatalogoPrenotazioni(){
        gestorePersistenza = new GestorePersistenza();
    }

    /**
     * Registra una nuova prenotazione, persistendola.
     *
     * @param prenotazione prenotazione da salvare
     */
    public void registraPrenotazione(Prenotazione prenotazione){
        gestorePersistenza.salva(prenotazione);
    }

    /**
     * Propaga sulla persistenza le modifiche di una prenotazione esistente
     * (tipicamente un cambio di stato).
     *
     * @param prenotazione prenotazione da aggiornare
     */
    public void aggiornaPrenotazione(Prenotazione prenotazione) {
        gestorePersistenza.aggiorna(prenotazione);
    }

    /**
     * Restituisce le prenotazioni attive di uno studente per la giornata corrente.
     * <p>
     * Filtra il catalogo per studente, data odierna e stato {@code ATTIVA}:
     * è l'insieme delle prenotazioni su cui lo studente può effettuare il
     * check-in oggi.
     *
     * @param studente studente di cui recuperare le prenotazioni
     * @return le prenotazioni attive dello studente per oggi
     */
    public List<Prenotazione> getPrenotazioniAttiveOggi(Studente studente) {
        return gestorePersistenza.cercaPerCampi(
                Prenotazione.class,
                Map.of(
                        "studente", studente,
                        "data", LocalDate.now(),
                        "stato", StatoPrenotazioneEnum.ATTIVA
                )
        );
    }
}