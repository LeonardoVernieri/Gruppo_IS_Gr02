package entity;

import database.GestorePersistenza;
import dto.FasciaOraria;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

/**
 * Entità di dominio che rappresenta una sala studio.
 * <p>
 * La {@code SalaStudio} è Information Expert della propria struttura: aggrega le
 * {@link Postazione} che contiene e le eventuali {@link Area} in cui è
 * suddivisa, ed è la sorgente delle {@link FasciaOraria} prenotabili, generate
 * dinamicamente dagli orari di apertura e chiusura. Calcola inoltre la
 * disponibilità di posti delegando alle singole postazioni, coerentemente con
 * la natura derivata della disponibilità.
 */
@Entity
public class SalaStudio {

    /** Accesso alla persistenza per le interrogazioni su aree e postazioni; non persistito. */
    @Transient
    private GestorePersistenza gestorePersistenza = new GestorePersistenza();

    /** Identificatore tecnico, auto-generato dal database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nome;
    private String descrizione;
    private int numeroPostazioni;
    private LocalTime orarioApertura;
    private LocalTime orarioChiusura;

    /** Indica se la sala è suddivisa in aree distinte. */
    private boolean presenzaAree;

    /** Aree in cui la sala è eventualmente suddivisa; ciclo di vita legato alla sala. */
    @OneToMany(mappedBy = "salaStudio", cascade = CascadeType.ALL)
    private List<Area> aree = new ArrayList<>();

    /** Bibliotecari che gestiscono la sala; {@code Set} per evitare duplicati. */
    @ManyToMany
    private Set<Bibliotecario> bibliotecari = new HashSet<>();

    /** Postazioni contenute nella sala; ciclo di vita legato alla sala. */
    @OneToMany(mappedBy = "salaStudio", cascade = CascadeType.ALL)
    private List<Postazione> postazioni = new ArrayList<>();

    /** Costruttore richiesto da JPA. */
    public SalaStudio(){}

    /**
     * Crea una sala studio completa della propria struttura interna.
     * <p>
     * Pattern Creator (GRASP): la sala è Creator delle proprie {@link Postazione}
     * e {@link Area}. Genera prima tutte le postazioni, poi, se previste le aree,
     * le crea assegnando a ciascuna un blocco contiguo di postazioni in base al
     * conteggio indicato. Il cascade {@code ALL} fa sì che postazioni e aree
     * vengano persistite insieme alla sala.
     *
     * @param nome             nome della sala
     * @param descrizione      descrizione della sala
     * @param numeroPostazioni numero totale di postazioni da generare
     * @param orarioApertura   orario di apertura giornaliero
     * @param orarioChiusura   orario di chiusura giornaliero
     * @param presenzaAree     {@code true} se la sala è suddivisa in aree
     * @param nomiAree             nomi/tipologie delle aree
     * @param postazioniPerArea             numero di postazioni assegnate a ciascuna area
     */
    public SalaStudio(String nome, String descrizione, int numeroPostazioni, LocalTime orarioApertura, LocalTime orarioChiusura,  boolean presenzaAree, List<String> nomiAree, List<Integer> postazioniPerArea) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.numeroPostazioni = numeroPostazioni;
        this.orarioApertura = orarioApertura;
        this.orarioChiusura = orarioChiusura;
        this.presenzaAree = presenzaAree;
        this.aree = new ArrayList<>();

        // Creo postazioni
        for( int i=0 ; i<numeroPostazioni; i++){
            Postazione p = new Postazione(this);
            this.postazioni.add(p);
        }

        // Crea aree
        int offset = 0;
        for (int i = 0; i < nomiAree.size(); i++) {
            int count = postazioniPerArea.get(i);
            Area area = new Area(nomiAree.get(i), count, this);
            this.aree.add(area);

            // Prendo una subList delle postazioni non ancora assegnate
            this.postazioni.subList(offset, offset + count)
                    .forEach(p -> p.setArea(area));
            offset += count;
        }
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public int getNumeroPostazioni() {
        return numeroPostazioni;
    }

    /**
     * Restituisce le aree della sala interrogando la persistenza.
     *
     * @return le {@link Area} associate alla sala
     */
    public List<Area> getAree() {
        return gestorePersistenza.cercaPerCampo(Area.class, "salaStudio", this);
    }

    /**
     * Recupera una specifica area della sala dalla sua tipologia.
     *
     * @param tipologiaArea tipologia dell'area cercata
     * @return l'{@link Area} corrispondente, oppure {@code null} se non esiste
     */
    public Area getArea(String tipologiaArea) {
        return gestorePersistenza.cercaPrimoPerCampi(Area.class, Map.of("tipologia", tipologiaArea, "salaStudio", this));
    }

    /**
     * Restituisce le postazioni della sala, eventualmente filtrate per area.
     *
     * @param area area di interesse, oppure {@code null} per tutte le postazioni della sala
     * @return le {@link Postazione} corrispondenti
     */
    public List<Postazione> getPostazioni(Area area) {
        if(area == null){
            return gestorePersistenza.cercaPerCampi(Postazione.class, Map.of("salaStudio", this));
        } else {
            return gestorePersistenza.cercaPerCampi(Postazione.class, Map.of("salaStudio", this, "area", area));
        }
    }

    /**
     * Genera dinamicamente le fasce orarie prenotabili della sala.
     * <p>
     * Le fasce sono slot di un'ora costruiti a partire dall'orario di apertura
     * fino all'orario di chiusura; non sono memorizzate ma calcolate a richiesta.
     *
     * @return l'elenco ordinato delle {@link FasciaOraria} della giornata
     */
    public List<FasciaOraria>  getFasceOrarie() {
        List<FasciaOraria> fasce = new ArrayList<>();
        LocalTime corrente = this.orarioApertura;

        while (corrente.isBefore(this.orarioChiusura)) {
            fasce.add(new FasciaOraria(corrente, corrente.plusHours(1)));
            corrente = corrente.plusHours(1);
        }
        return fasce;
    }

    /**
     * Conta le postazioni libere in una fascia e giorno dati, eventualmente
     * ristrette a una tipologia di area.
     *
     * @param f             fascia oraria di interesse
     * @param data          giorno di interesse
     * @param tipologiaArea tipologia di area, oppure {@code null} per l'intera sala
     * @return numero di postazioni disponibili
     */
    public int getPostiLiberi(FasciaOraria f, LocalDate data, String tipologiaArea) {
        int count = 0;

        Area area = (tipologiaArea != null)
                ? getArea(tipologiaArea)
                : null;

        for (Postazione p : getPostazioni(area)) {
            if (p.isDisponibile(f, data)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Cerca la prima postazione libera per una fascia e giorno dati.
     * <p>
     * Senza tipologia di area, privilegia le postazioni non assegnate ad alcuna
     * area; se non ve ne sono, ricade sull'intero insieme. Con una tipologia
     * indicata, cerca tra le postazioni di quell'area.
     *
     * @param fascia        fascia oraria richiesta
     * @param tipologiaArea tipologia di area, oppure {@code null}
     * @param data          giorno di interesse
     * @return la prima {@link Postazione} disponibile, oppure {@code null} se nessuna lo è
     */
    public Postazione cercaPrimaPostazioneLibera(FasciaOraria fascia, String tipologiaArea, LocalDate data) {

        if(tipologiaArea == null){
            // Prima cerca tra postazioni senza area
            List<Postazione> tutte = getPostazioni(null);

            List<Postazione> senzaArea = tutte.stream()
                    .filter(p -> p.getArea() == null)
                    .toList();

            List<Postazione> daCercare = senzaArea.isEmpty() ? tutte : senzaArea;

            for (Postazione p : daCercare) {
                if (p.isDisponibile(fascia, data)) return p;
            }
        }

        Area area = (tipologiaArea != null)
                ? getArea(tipologiaArea)
                : null;

        for(Postazione p : getPostazioni(area)) {
            if (p.isDisponibile(fascia, data)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Verifica se esiste almeno una postazione libera per <b>tutte</b> le fasce
     * indicate nello stesso giorno, eventualmente ristretta a una tipologia di area.
     * <p>
     * Utile per prenotazioni su più slot: una postazione è valida solo se
     * disponibile in ognuna delle fasce richieste.
     *
     * @param fascia        elenco di fasce orarie che devono essere tutte libere
     * @param data          giorno di interesse
     * @param tipologiaArea tipologia di area, oppure {@code null} per l'intera sala
     * @return {@code true} se almeno una postazione è libera in tutte le fasce
     */
    public boolean isDisponibilePostazione(List<FasciaOraria> fascia, LocalDate data, String tipologiaArea) {
        Area area = (tipologiaArea != null)
                ? getArea(tipologiaArea)
                : null;

        for (Postazione p : getPostazioni(area)) {
            boolean postazioneDisponibile = true;
            for(FasciaOraria f : fascia) {
                if (!p.isDisponibile(f, data)) {
                    postazioneDisponibile = false;
                    break;
                }
            }
            if (postazioneDisponibile) { return true; }
        }
        return false;
    }
}