package control;

import dto.FasciaOraria;
import entity.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GestorePrenotazioni {

    private CatalogoSalaStudio catalogoSalaStudio;

    public GestorePrenotazioni() {
        catalogoSalaStudio = new CatalogoSalaStudio();
    }

    public boolean isPrenotazioneUnicaPossibile(String nomeSala, Set<FasciaOraria> fasce, LocalDate data, String sala) {

        SalaStudio s = catalogoSalaStudio.getSalaPerNome(nomeSala);

        List<FasciaOraria> fasceListe = new ArrayList<>(fasce);

        if (fasceListe.size() > 1) {
            return s.isDisponibilePostazione(fasceListe, data, sala);
        }  else {
            return true;
        }
    }

    // Effettua la prenotazione restituendo TRUE se non ci sono errori
    public boolean effettuaPrenotazione(String nomeSala, LocalDate data, FasciaOraria fascia,  Studente stud, String area){
        System.out.println("Effettua prenotazioni");
        System.out.println("NomeSala: " + nomeSala);
        System.out.println("Data: " + data);
        System.out.println("Fasce: " + fascia);
        System.out.println("Area: " + area);

        CatalogoPrenotazioni catalogoPrenotazioni = new CatalogoPrenotazioni();

        // Recupero l'oggetto SalaStudio
        SalaStudio s = catalogoSalaStudio.getSalaPerNome(nomeSala);

        // Recupero una postazione idonea alla prenotazione
        Postazione postazione = s.cercaPrimaPostazioneLibera(fascia, area, data);

        // Creo l'oggetto Prenotazione
        Prenotazione prenotazione = stud.creaPrenotazione(postazione, data, fascia);
        if(prenotazione == null){
            return false;
        }

        // Registro l'oggetto Prenotazione
        catalogoPrenotazioni.registraPrenotazione(prenotazione);

        return true;
    }



}
