package entity;

import database.GestorePersistenza;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class CatalogoPrenotazioni {

    private GestorePersistenza gestorePersistenza;

    public CatalogoPrenotazioni(){
        gestorePersistenza = new GestorePersistenza();
    }

    public void registraPrenotazione(Prenotazione prenotazione){
        gestorePersistenza.salva(prenotazione);
    }

    public void aggiornaPrenotazione(Prenotazione prenotazione) {
        gestorePersistenza.aggiorna(prenotazione);

    }

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
