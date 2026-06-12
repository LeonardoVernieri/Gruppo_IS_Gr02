package entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Prenotazione {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatoPrenotazioneEnum stato;

    @Transient
    private StatoPrenotazione statoPrenotazione;

    private LocalDate dataCheckIn;



    private LocalTime inizioTempo;
    private LocalTime fineTempo;

    @ManyToOne
    @JoinColumn(name = "postazione_id")
    private Postazione postazione;

    @ManyToOne
    @JoinColumn(name = "studente_matricola")
    private Studente studente;

    public Studente getStudente() {
        return studente;
    }

    public void setStudente(Studente studente) {
        this.studente = studente;
    }
    public void setStato(StatoPrenotazioneEnum stato) {
        this.stato = stato;
        this.statoPrenotazione = switch(stato) {
            case ATTIVA -> new StatoPAttiva();
            case SCADUTA -> new StatoPScaduta();
            case ANNULLATA -> new StatoPAnnullata();
            case CONFERMATA -> new StatoPConfermata();
        };
    }

    public void setDataCheckIn(LocalDate dataCheckIn) {
        this.dataCheckIn = dataCheckIn;
    }

    public void setInizioTempo(LocalTime inizioTempo) {
        this.inizioTempo = inizioTempo;
    }

    public void setFineTempo(LocalTime fineTempo) {
        this.fineTempo = fineTempo;
    }




    @Override
    public String toString() {
        return "Data: " + dataCheckIn +
                " | Orario: " + inizioTempo + " - " + fineTempo;
    } //tostring per mostrare le informazioni della prenotazione per il metodo effettua checkin

    // Inizializza la classe stato corretta dell'oggetto prendendolo dallo salvato nel DB
    @PostLoad
    private void inizializzaStato(){
        switch(stato){
            case ATTIVA -> statoPrenotazione = new StatoPAttiva();
            case SCADUTA ->   statoPrenotazione = new StatoPScaduta();
            case ANNULLATA ->  statoPrenotazione = new StatoPAnnullata();
            case CONFERMATA ->   statoPrenotazione = new StatoPConfermata();
        }
    }

    // Costruttore
    public Prenotazione() {}


    public boolean isAttiva(){
        return stato == StatoPrenotazioneEnum.ATTIVA;
    }

    public boolean isConfermata() {
        return stato  == StatoPrenotazioneEnum.CONFERMATA;
    }

    public boolean isScaduta() {
        return stato  == StatoPrenotazioneEnum.SCADUTA;
    }

    public void conferma() {
        this.stato = StatoPrenotazioneEnum.CONFERMATA;
        this.statoPrenotazione = new StatoPConfermata();
    }

    // Restituisce True se la fascia e' contenuta
    public boolean overlaps(FasciaOraria fascia) {
        return inizioTempo.isBefore(fascia.getFine()) && fineTempo.isAfter(fascia.getInizio());
    }
}
