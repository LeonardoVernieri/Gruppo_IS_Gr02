package entity;

import database.GestorePersistenza;
import dto.FasciaOraria;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Information Expert dell'intera collezione delle {@link SalaStudio}.
 * <p>
 * Fa da punto di accesso unico al catalogo delle sale: incapsula il recupero
 * delle sale dalla persistenza e aggrega le informazioni di disponibilità
 * delegando ai metodi della singola {@link SalaStudio}.
 */
public class CatalogoSaleStudio {

    private final GestorePersistenza gestorePersistenza;

    public CatalogoSaleStudio() {
        gestorePersistenza = new GestorePersistenza();
    }

    /**
     * Recupera una sala studio dal suo nome.
     *
     * @param nome nome della sala
     * @return la {@link SalaStudio} corrispondente, oppure {@code null} se non esiste
     */
    public SalaStudio getSalaPerNome(String nome){
        return gestorePersistenza.cercaPrimoPerCampi(SalaStudio.class, Map.of("nome", nome));
    }

    /**
     * Calcola, per una sala e un giorno dati, il numero di posti liberi in
     * ciascuna fascia oraria, eventualmente ristretto a una specifica area.
     * <p>
     * Per ogni fascia generata dalla sala, interroga la {@link SalaStudio} sul
     * numero di posti liberi in quella fascia, data e area.
     *
     * @param nomeSala nome della sala
     * @param date     giorno di interesse
     * @param area     area di interesse, oppure {@code null} per l'intera sala
     * @return mappa fascia oraria → numero di posti liberi
     */
    public Map<FasciaOraria, Integer> getDisponibilitaFasciaOrariaSalaPerData(String nomeSala, LocalDate date, String area){
        Map<FasciaOraria, Integer> fascieOrarie =  new HashMap<FasciaOraria, Integer>();
        SalaStudio s = getSalaPerNome(nomeSala);
        for ( FasciaOraria fascia : s.getFasceOrarie()){
            fascieOrarie.put(fascia, s.getPostiLiberi(fascia, date, area));
        }
        return fascieOrarie;
    }

    /**
     * Restituisce l'elenco completo delle sale studio.
     *
     * @return tutte le {@link SalaStudio} presenti a sistema
     */
    public List<SalaStudio> getSale(){
        return gestorePersistenza.cercaPerCampi(SalaStudio.class, Map.of());
    }

    /**
     * Restituisce il numero totale di postazioni di una sala.
     *
     * @param sala nome della sala
     * @return numero complessivo di postazioni della sala
     */
    public int getPostazioniTotali(String sala){
        return getSalaPerNome(sala).getNumeroPostazioni();
    }

    /**
     * Restituisce il numero di postazioni libere di una sala in una data fascia
     * e giorno, considerando l'intera sala (nessun filtro per area).
     *
     * @param fascia fascia oraria di interesse
     * @param date   giorno di interesse
     * @param sala   nome della sala
     * @return numero di postazioni libere
     */
    public int getPostazioniLibere(FasciaOraria fascia, LocalDate date, String sala){
        SalaStudio s = getSalaPerNome(sala);
        return s.getPostiLiberi(fascia, date, null);
    }
}