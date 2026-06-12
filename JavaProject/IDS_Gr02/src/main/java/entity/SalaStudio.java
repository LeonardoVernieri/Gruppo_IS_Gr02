package entity;

import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

@Entity
public class SalaStudio{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nome;
    private String descrizione;
    private int numeroPostazioni;
    private LocalTime orarioApertura;
    private LocalTime orarioChiusura;
    private boolean presenzaAree;
    @OneToMany(mappedBy = "sala")
    private List<Area> aree = new ArrayList<>();


    public SalaStudio(String nome, String descrizione, int numeroPostazioni,
                      LocalTime orarioApertura, LocalTime orarioChiusura, boolean presenzaAree) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.numeroPostazioni = numeroPostazioni;
        this.orarioApertura = orarioApertura;
        this.orarioChiusura = orarioChiusura;
        this.presenzaAree = presenzaAree;
        this.aree = new ArrayList<>(); // lista vuota, le aree si aggiungono dopo
    }

    public SalaStudio() {

    }

    public void aggiungiArea(String tipologia, int numeroPostazioni) {
        Area area = new Area(tipologia, numeroPostazioni, this);
        aree.add(area);
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public void setNumeroPostazioni(int numeroPostazioni) {
        this.numeroPostazioni = numeroPostazioni;
    }

    public void setOrarioApertura(LocalTime orarioApertura) {
        this.orarioApertura = orarioApertura;
    }

    public void setOrarioChiusura(LocalTime orarioChiusura) {
        this.orarioChiusura = orarioChiusura;
    }

    public void setPresenzaAree(boolean presenzaAree) {
        this.presenzaAree = presenzaAree;
    }

    public void setAree(List<Area> aree) {
        this.aree = aree;
    }
}