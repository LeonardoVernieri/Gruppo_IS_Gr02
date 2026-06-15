package entity;

import database.GestorePersistenza;
import dto.FasciaOraria;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity

public class Postazione {

    @Transient
    GestorePersistenza gestorePersistenza;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Associazione con postazione
    @OneToMany(mappedBy = "postazione")
    private List<Prenotazione> prenotazioni = new ArrayList<>();

    // Associazione con sala studio
    @ManyToOne
    @JoinColumn(name = "salaStudio_id")
    private SalaStudio salaStudio;

    // Associazione con area
    @ManyToOne
    @JoinColumn(name = "area_id")
    private Area area;

    public Postazione(){
        gestorePersistenza = new GestorePersistenza();
    }

    public Postazione(SalaStudio salaStudio){
        this.salaStudio = salaStudio;
    }

    // Getter
    public Area getArea() { return area; }

    public SalaStudio getSalaStudio() { return salaStudio; }

    // Setter
    public void setArea(Area area) { this.area = area; }

    public List<Prenotazione> getPrenotazioni() {
        return(gestorePersistenza.cercaPerCampo(Prenotazione.class, "postazione", this));
    }

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
