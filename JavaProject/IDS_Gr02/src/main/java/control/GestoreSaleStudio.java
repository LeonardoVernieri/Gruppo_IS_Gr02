package control;

import dto.FasciaOraria;
import entity.Area;
import entity.Bibliotecario;
import entity.CatalogoSalaStudio;
import entity.SalaStudio;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GestoreSaleStudio {

    private CatalogoSalaStudio catalogoSala;

    public GestoreSaleStudio() {
        catalogoSala = new CatalogoSalaStudio();
    }

    /**
     * Restituisce le fasce orarie disponibili per una sala in una data specifica,
     * escludendo automaticamente le fasce già iniziate se la data è oggi.
     *
     * @param nomeSala il nome della sala di cui recuperare la disponibilità
     * @param data     la data per cui si vuole consultare la disponibilità
     * @return mappa da FasciaOraria al numero di postazioni libere in quella fascia
     */
    public Map<FasciaOraria, Integer> getNumPostazioniDisponibili(String nomeSala, LocalDate data, String area) {
        // Recupera dal catalogo tutte le fasce orarie con il relativo numero di posti liberi
        Map<FasciaOraria, Integer> mapDisponibilitaPerFasceOrarie = catalogoSala.getDisponibilitaFasciaOrariaSalaPerData(nomeSala, data, area);

        // Se la data richiesta è oggi, filtra le fasce già iniziate
        if (data.equals(LocalDate.now())) {
            LocalTime adesso = LocalTime.now();
            // Rimuove tutte le fasce la cui ora di inizio è già passata
            mapDisponibilitaPerFasceOrarie.entrySet().removeIf(e -> e.getKey().getOraInizio().isBefore(adesso));
        }

        return mapDisponibilitaPerFasceOrarie;
    }

    /**
     * Restituisce la lista dei nomi di tutte le sale studio disponibili nel catalogo.
     *
     * @return lista di stringhe con i nomi delle sale
     */
    public List<String> getNomiSale() {
        List<String> nomiSale = new ArrayList<>();
        for (SalaStudio s : catalogoSala.getSale()) {
            nomiSale.add(s.getNome());
        }
        return nomiSale;
    }

    /**
     * Restituisce il numero totale di postazioni della sala specificata.
     *
     * @param nomeSala il nome della sala
     * @return numero totale di postazioni
     */
    public int getNumPostazioniSala(String nomeSala) {
        return catalogoSala.getPostazioniTotali(nomeSala);
    }

    /**
     * Restituisce il numero di postazioni libere per una sala in una fascia oraria e data specifiche.
     *
     * @param fascia   la fascia oraria di interesse
     * @param date     la data di interesse
     * @param nomeSala il nome della sala
     * @return numero di postazioni ancora libere
     */
    public int getNumPostazioniLibere(FasciaOraria fascia, LocalDate date, String nomeSala) {
        return catalogoSala.getPostazioniLibere(fascia, date, nomeSala);
    }

    /**
     * Restituisce la lista delle tipologie di aree presenti in una sala studio.
     *
     * @param nomeSala il nome della sala
     * @return lista di stringhe con le tipologie delle aree (es. "silenzio", "gruppi", ...)
     */
    public List<String> getAreeSala(String nomeSala) {
        List<String> areeSale = new ArrayList<>();
        SalaStudio s = catalogoSala.getSalaPerNome(nomeSala);
        for (Area a : s.getAree()) {
            areeSale.add(a.getTipologia());
        }
        return areeSale;
    }

    public int getNumPostazioniArea(String nomeSala, String tipologiaArea) {
        SalaStudio s =  catalogoSala.getSalaPerNome(nomeSala);
        return s.getArea(tipologiaArea).getNumeroPostazioni();
    }

    /**
     * Aggiunge una nuova sala studio al sistema tramite un bibliotecario.
     *
     * @param nome                   nome della sala
     * @param descrizione            descrizione della sala
     * @param numeroPostazioniTotali numero totale di postazioni
     * @param orarioApertura         orario di apertura giornaliero
     * @param orarioChiusura         orario di chiusura giornaliero
     * @param presenzaAree           indica se la sala è suddivisa in aree
     * @param col1                   lista dei nomi delle aree (se presenti)
     * @param col2                   lista del numero di postazioni per ciascuna area
     * @return {@code true} se la sala è stata creata con successo, {@code false} altrimenti
     */
    public static boolean aggiungiSalaStudio(String nome,
                                             String descrizione,
                                             int numeroPostazioniTotali,
                                             LocalTime orarioApertura,
                                             LocalTime orarioChiusura,
                                             boolean presenzaAree,
                                             List<String> col1,
                                             List<Integer> col2) {
        Bibliotecario bibliotecario = new Bibliotecario();
        return bibliotecario.creaSalaStudio(nome, descrizione, numeroPostazioniTotali,
                orarioApertura, orarioChiusura,
                presenzaAree, col1, col2);
    }
}
