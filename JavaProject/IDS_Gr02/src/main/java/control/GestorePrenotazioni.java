package control;

import boundary.ServizioNotifiche;
import dto.FasciaOraria;
import entity.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GestorePrenotazioni {

    private CatalogoSalaStudio catalogoSalaStudio;
    private CatalogoPrenotazioni catalogoPrenotazioni;

    public GestorePrenotazioni() {
        catalogoPrenotazioni  = new CatalogoPrenotazioni();
        catalogoSalaStudio = new CatalogoSalaStudio();
    }

    public boolean isPrenotazioneUnicaPossibile(String nomeSala, Set<FasciaOraria> fasce, LocalDate data, String sala) {

        SalaStudio s = catalogoSalaStudio.getSalaPerNome(nomeSala);

        List<FasciaOraria> fasceListe = new ArrayList<>(fasce);

        if (fasceListe.size() > 1) {
            return s.isDisponibilePostazione(fasceListe, data);
        }  else {
            return true;
        }
    }

    // Effettua la prenotazione restituendo TRUE se non ci sono errori
    public boolean effettuaPrenotazione(String nomeSala, LocalDate data, FasciaOraria fascia,  Studente stud, String area) {

        // Recupero l'oggetto SalaStudio
        SalaStudio s = catalogoSalaStudio.getSalaPerNome(nomeSala);

        // Recupero una postazione idonea alla prenotazione
        Postazione postazione = s.cercaPrimaPostazioneLibera(fascia, area, data);
        if (postazione == null) {
            return false;
        }

        // Creo l'oggetto Prenotazione
        Prenotazione prenotazione = stud.creaPrenotazione(postazione, data, fascia);
        if( prenotazione == null ){
            return false;
        }

        // Registro l'oggetto Prenotazione
        catalogoPrenotazioni.registraPrenotazione(prenotazione);

        ServizioNotifiche notifiche = new ServizioNotifiche();
        notifiche.inviaNotificaPrenotazione(stud.getEmail(), nomeSala, data.toString(), fascia.toString());

        return true;
    }

    public List<Prenotazione> cercaPrenotazioniAttive(Studente studente) {
        return catalogoPrenotazioni.getPrenotazioniAttiveOggi(studente);
    }


    public void effettuaCheckIn(Prenotazione prenotazione) {
        prenotazione.conferma();
        prenotazione.setDataCheckIn(LocalTime.now());
        catalogoPrenotazioni.aggiornaPrenotazione(prenotazione);
    }
}
