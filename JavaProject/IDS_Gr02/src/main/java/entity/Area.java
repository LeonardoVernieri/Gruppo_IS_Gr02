package entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entità di dominio che rappresenta un'area di una {@link SalaStudio}
 * (ad esempio area silenziosa, consultazione o lavoro di gruppo).
 * <p>
 * L'{@code Area} suddivide opzionalmente una sala e raggruppa un sottoinsieme
 * delle sue {@link Postazione}. È IE della propria tipologia e del numero di
 * postazioni che le competono.
 */
@Entity
public class Area {

    /** Identificatore tecnico, auto-generato dal database. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /** Tipologia dell'area (es. silenziosa, consultazione, lavoro di gruppo). */
    private String tipologia;

    private int numeroPostazioni;

    /** Sala studio che contiene l'area. */
    @ManyToOne
    @JoinColumn(name = "sala_id")
    private SalaStudio salaStudio;

    /** Postazioni appartenenti a questa area (lato inverso dell'associazione). */
    @OneToMany(mappedBy = "area")
    private List<Postazione> postazione = new ArrayList<>();

    public Area(String tipologia, int numeroPostazioni, SalaStudio sala) {
        this.tipologia = tipologia;
        this.numeroPostazioni = numeroPostazioni;
        this.salaStudio = sala;
    }

    /** Costruttore richiesto da JPA. */
    public Area() {
    }

    public String getTipologia() { return tipologia; }
    public int getNumeroPostazioni() { return numeroPostazioni; }
    public long getId() { return id; }

}