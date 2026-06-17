package entity;

import dto.FasciaOraria;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entità di dominio che rappresenta una prenotazione di una {@link Postazione}
 * effettuata da uno {@link Studente} per una data e una fascia oraria.
 * <p>
 * La {@code Prenotazione} è Information Expert del proprio stato e dei propri
 * attributi temporali. Il ciclo di vita dello stato è modellato con il pattern
 * State: lo stato è persistito come {@code enum} ({@link StatoPrenotazioneEnum})
 * tramite {@code @Enumerated(EnumType.STRING)}, mentre l'oggetto-stato
 * comportamentale ({@link StatoPrenotazione}) è {@code @Transient} e viene
 * ricostruito dall'enum tramite {@link #setStato(StatoPrenotazioneEnum)}. Le
 * transizioni di stato sono delegate all'oggetto-stato corrente; le query di
 * stato leggono invece direttamente l'enum.
 */
@Entity
public class Prenotazione {

    /** Identificatore tecnico, auto-generato dal database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Stato persistito, salvato come stringa per leggibilità e stabilità sul DB. */
    @Enumerated(EnumType.STRING)
    private StatoPrenotazioneEnum stato;

    /** Oggetto-stato del pattern State; non persistito, ricostruito dall'enum. */
    @Transient
    private StatoPrenotazione statoPrenotazione;

    /** Orario in cui è stato effettuato il check-in; {@code null} finché non avviene. */
    private LocalTime dataCheckIn;

    private LocalDate data;
    private LocalTime inizioTempo;
    private LocalTime fineTempo;

    @ManyToOne
    @JoinColumn(name = "studente_id")
    private Studente studente;

    @ManyToOne
    @JoinColumn(name = "postazione_id")
    private Postazione postazione;

    /**
     * Ricostruisce l'oggetto-stato comportamentale dopo il caricamento da
     * database, riusando {@link #setStato(StatoPrenotazioneEnum)} a partire
     * dall'enum persistito.
     * <p>
     * Necessario perché {@link #statoPrenotazione} è {@code @Transient} e non
     * viene quindi materializzato da Hibernate.
     */
    @PostLoad
    private void inizializzaStato(){
        setStato(stato);
    }

    /**
     * Crea una nuova prenotazione nello stato iniziale {@code ATTIVA}, senza
     * check-in registrato.
     * <p>
     * Riceve la fascia oraria come {@link FasciaOraria} e ne estrae internamente
     * gli estremi temporali, evitando al chiamante di destrutturarla.
     *
     * @param data       giorno della prenotazione
     * @param stud       studente titolare
     * @param postazione postazione prenotata
     * @param fascia     fascia oraria prenotata, da cui si ricavano ora di inizio e fine
     */
    public Prenotazione(LocalDate data, Studente stud, Postazione postazione, FasciaOraria fascia) {
        this.data = data;
        this.studente = stud;
        this.postazione = postazione;
        dataCheckIn = null;
        setStato(StatoPrenotazioneEnum.ATTIVA);
        this.inizioTempo = fascia.getOraInizio();
        this.fineTempo = fascia.getOraFine();
    }

    /** Costruttore richiesto da JPA. */
    public Prenotazione() {}


    /* Getter e Setter */

    public LocalDate getData() { return data; }

    public Studente getStudente() {
        return studente;
    }

    public void setStudente(Studente studente) {
        this.studente = studente;
    }

    public void setDataCheckIn(LocalTime dataCheckIn) {
        this.dataCheckIn = dataCheckIn;
    }

    public void setInizioTempo(LocalTime inizioTempo) {
        this.inizioTempo = inizioTempo;
    }

    public LocalTime getInizioTempo() {return inizioTempo;}

    public void setFineTempo(LocalTime fineTempo) {
        this.fineTempo = fineTempo;
    }

    /**
     * Imposta lo stato persistito e ricostruisce coerentemente l'oggetto-stato
     * comportamentale corrispondente, mantenendo allineate le due
     * rappresentazioni. Unico punto in cui le due viste dello stato vengono
     * sincronizzate.
     *
     * @param stato nuovo stato della prenotazione
     */
    public void setStato(StatoPrenotazioneEnum stato) {
        this.stato = stato;
        this.statoPrenotazione = switch(stato) {
            case ATTIVA -> new StatoPAttiva();
            case SCADUTA -> new StatoPScaduta();
            case ANNULLATA -> new StatoPAnnullata();
            case CONFERMATA -> new StatoPConfermata();
        };
    }

    /**
     * Rappresentazione testuale sintetica (orario, sala ed eventuale area),
     * usata per presentare la prenotazione nel flusso di check-in.
     */
    @Override
    public String toString() {
        return "Orario: " + inizioTempo + " - " + fineTempo
                + " | Sala: " + postazione.getSalaStudio().getNome() +
                (postazione.getArea() != null ? (" | Area: " + postazione.getArea().getTipologia()) : (" "));
    }

    public boolean isAttiva(){
        return stato == StatoPrenotazioneEnum.ATTIVA;
    }

    public boolean isConfermata() {
        return stato  == StatoPrenotazioneEnum.CONFERMATA;
    }

    /**
     * Verifica se questa prenotazione si sovrappone temporalmente alla fascia indicata.
     * <p>
     * La sovrapposizione è vera quando l'inizio di questa prenotazione precede
     * la fine della fascia e la sua fine segue l'inizio della fascia (overlap
     * stretto sugli estremi).
     *
     * @param fascia fascia oraria da confrontare
     * @return {@code true} se i due intervalli si sovrappongono
     */
    public boolean isOverlap(FasciaOraria fascia) {
        return inizioTempo.isBefore(fascia.getOraFine()) && fineTempo.isAfter(fascia.getOraInizio());
    }

    public boolean isScaduta() {
        return stato  == StatoPrenotazioneEnum.SCADUTA;
    }

    /**
     * Conferma la prenotazione delegando la transizione all'oggetto-stato
     * corrente (pattern State): è lo stato attivo a decidere se e come
     * effettuare il passaggio a {@code CONFERMATA}.
     */
    public void conferma() {
        statoPrenotazione.conferma(this);
    }
}