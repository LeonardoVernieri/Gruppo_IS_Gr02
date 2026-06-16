package entity;

import database.GestorePersistenza;
import dto.FasciaOraria;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entità di dominio che rappresenta una singola postazione di studio,
 * collocata all'interno di una {@link SalaStudio} ed eventualmente di una
 * {@link Area}.
 * <p>
 * La {@code Postazione} è Information Expert della propria disponibilità: questa
 * non è un attributo persistito ma è <b>derivata dinamicamente</b> dalle
 * prenotazioni che insistono sulla postazione, valutate rispetto a una data e a
 * una fascia oraria (vedi {@link #isDisponibile(FasciaOraria, LocalDate)}).
 */
@Entity
public class Postazione {

    /** Identificatore tecnico, auto-generato dal database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Prenotazioni che insistono su questa postazione (lato inverso dell'associazione). */
    @OneToMany(mappedBy = "postazione")
    private List<Prenotazione> prenotazioni = new ArrayList<>();

    /** Sala studio a cui la postazione appartiene. */
    @ManyToOne
    @JoinColumn(name = "salaStudio_id")
    private SalaStudio salaStudio;

    /** Area di appartenenza (opzionale: una sala può non essere suddivisa in aree). */
    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;

    @Transient
    GestorePersistenza gestorePersistenza = new GestorePersistenza();

    public Postazione(){
    }

    public Postazione(SalaStudio salaStudio){
        this.salaStudio = salaStudio;
    }

    public Area getArea() { return area; }
    public SalaStudio getSalaStudio() { return salaStudio; }

    public void setArea(Area area) { this.area = area; }

    /**
     * Restituisce le prenotazioni associate a questa postazione interrogando la
     * persistenza, anziché basarsi sulla collezione mappata in memoria.
     * <p>
     * La lettura a richiesta garantisce di lavorare sullo stato aggiornato del
     * database, evitando disallineamenti con la collezione {@code prenotazioni}.
     *
     * @return le prenotazioni della postazione presenti a sistema
     */
    public List<Prenotazione> getPrenotazioni() {
        return(gestorePersistenza.cercaPerCampo(Prenotazione.class, "postazione", this));
    }

    /**
     * Verifica se la postazione è libera per una data fascia oraria in un dato giorno.
     * <p>
     * La postazione è considerata occupata se esiste almeno una prenotazione
     * attiva o confermata, nello stesso giorno, la cui fascia si sovrappone a
     * quella richiesta. È il meccanismo a runtime che, insieme al vincolo di
     * unicità su {@code (Postazione, FasciaOraria, data)}, previene la doppia
     * assegnazione della stessa postazione.
     *
     * @param fascia fascia oraria richiesta
     * @param date   giorno della prenotazione
     * @return {@code true} se nessuna prenotazione attiva/confermata occupa la postazione, {@code false} altrimenti
     */
    public boolean isDisponibile(FasciaOraria fascia, LocalDate date) {
        for (Prenotazione p : getPrenotazioni()) {
            if ((p.isAttiva() || p.isConfermata())
                    && p.isOverlap(fascia)
                    && p.getData().equals(date)) {
                return false;
            }
        }
        return true;
    }
}