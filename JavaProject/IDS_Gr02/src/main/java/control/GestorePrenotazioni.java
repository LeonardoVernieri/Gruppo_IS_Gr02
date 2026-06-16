package control;

import dto.FasciaOraria;
import entity.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Use Case Controller responsabile del ciclo di vita delle prenotazioni.
 * <p>
 * Orchestra l'effettuazione di una prenotazione, il check-in e la consultazione
 * delle prenotazioni attive, coordinando {@link CatalogoSaleStudio} (per il
 * reperimento di sale e postazioni libere) e {@link CatalogoPrenotazioni} (IE
 * della collezione delle prenotazioni).
 */
public class GestorePrenotazioni {

    private CatalogoSaleStudio catalogoSaleStudio;
    private CatalogoPrenotazioni catalogoPrenotazioni;

    public GestorePrenotazioni() {
        catalogoPrenotazioni  = new CatalogoPrenotazioni();
        catalogoSaleStudio = new CatalogoSaleStudio();
    }

    /**
     * Verifica se una prenotazione su più fasce è soddisfacibile da un'unica
     * postazione, ossia se esiste una postazione libera in tutte le fasce
     * richieste nello stesso giorno, eventualmente in una specifica area.
     * <p>
     * Per una sola fascia la condizione è banalmente soddisfatta e il controllo
     * effettivo è demandato alla fase di effettuazione.
     *
     * @param nomeSala nome della sala
     * @param fasce    fasce orarie richieste
     * @param data     giorno della prenotazione
     * @param area     tipologia di area, oppure {@code null} per l'intera sala
     * @return {@code true} se la richiesta è soddisfacibile da un'unica postazione
     */
    public boolean isPrenotazioneUnicaPossibile(String nomeSala, Set<FasciaOraria> fasce, LocalDate data, String area) {
        SalaStudio s = catalogoSaleStudio.getSalaPerNome(nomeSala);
        List<FasciaOraria> fasceListe = new ArrayList<>(fasce);
        if (fasceListe.size() > 1) {
            return s.isDisponibilePostazione(fasceListe, data, area);
        }  else {
            return true;
        }
    }

    /**
     * Effettua una prenotazione per uno studente su una sala, data e fascia,
     * eventualmente in una specifica area.
     * <p>
     * Reperisce la prima postazione libera idonea, crea la prenotazione tramite
     * lo studente (Creator) e la registra nel catalogo. Se non esiste una
     * postazione libera o la creazione fallisce, l'operazione non ha effetto.
     *
     * @param nomeSala nome della sala
     * @param data     giorno della prenotazione
     * @param fascia   fascia oraria richiesta
     * @param stud     studente che effettua la prenotazione
     * @param area     tipologia di area richiesta, oppure {@code null}
     * @return {@code true} se la prenotazione è andata a buon fine, {@code false} altrimenti
     */
    public boolean effettuaPrenotazione(String nomeSala, LocalDate data, FasciaOraria fascia,  Studente stud, String area) {
        SalaStudio s = catalogoSaleStudio.getSalaPerNome(nomeSala);
        Postazione postazione = s.cercaPrimaPostazioneLibera(fascia, area, data);
        if (postazione == null) {
            return false;
        }
        Prenotazione prenotazione = stud.creaPrenotazione(postazione, data, fascia);
        if( prenotazione == null ){
            return false;
        }
        catalogoPrenotazioni.registraPrenotazione(prenotazione);
        return true;
    }

    /**
     * Restituisce le prenotazioni attive oggi per lo studente, ossia quelle su
     * cui può effettuare il check-in nella giornata corrente.
     *
     * @param studente studente di cui recuperare le prenotazioni
     * @return le prenotazioni attive odierne dello studente
     */
    public List<Prenotazione> cercaPrenotazioniAttive(Studente studente) {
        return catalogoPrenotazioni.getPrenotazioniAttiveOggi(studente);
    }

    /**
     * Effettua il check-in su una prenotazione, delegandone la conferma
     * all'entità (pattern State) e propagando l'aggiornamento alla persistenza.
     *
     * @param prenotazione prenotazione su cui effettuare il check-in
     */
    public void effettuaCheckIn(Prenotazione prenotazione) {
        prenotazione.conferma();
        catalogoPrenotazioni.aggiornaPrenotazione(prenotazione);
    }

    /**
     * Indica se una prenotazione è scaduta.
     *
     * @param p prenotazione da verificare
     * @return {@code true} se la prenotazione è nello stato {@code SCADUTA}
     */
    public boolean isPrenotazioneScaduta(Prenotazione p) {
        return p.isScaduta();
    }
}