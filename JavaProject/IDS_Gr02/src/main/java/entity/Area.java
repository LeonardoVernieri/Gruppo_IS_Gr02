package entity;

import database.GestorePersistenza;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Area {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    private String tipologia;
    private int numeroPostazioni;
    @ManyToOne
    @JoinColumn(name = "sala_id")
    private SalaStudio sala; //riferimento alla sala che la contiene

    public void setSala(SalaStudio sala) {
        this.sala = sala;
    }

    public Area(String tipologia, int numeroPostazioni, SalaStudio sala) {
        this.tipologia = tipologia;
        this.numeroPostazioni = numeroPostazioni;
        this.sala = sala;
    }

    public Area(String tipologia, int numeroPostazioni) {
        this.tipologia = tipologia;
        this.numeroPostazioni = numeroPostazioni;
    }

    public Area() {

    }

    public static final GestorePersistenza gp = new GestorePersistenza();

    //getter
    public String getTipologia() { return tipologia; }
    public int getNumeroPostazioni() { return numeroPostazioni; }
    public SalaStudio getSala() { return sala; }

    //setter
    public void setNumeroPostazioni(int numeroPostazioni) {
        this.numeroPostazioni = numeroPostazioni;
    }

    public void setTipologia(String tipologia) {
        this.tipologia = tipologia;
    }

    public void creaArea(String str, Integer num){
        Area a = new Area(str, num);
        gp.salva(a);
    }
}