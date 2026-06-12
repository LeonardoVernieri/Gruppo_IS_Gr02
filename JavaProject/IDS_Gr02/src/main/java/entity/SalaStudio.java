package entity;

import database.GestorePersistenza;
import dto.FasciaOraria;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

@Entity
public class SalaStudio{

    @Transient
    private GestorePersistenza gestorePersistenza;


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

    // Associazione con bibliotecari
    @ManyToMany
    private Set<Bibliotecario> bibliotecari = new HashSet<>(); // Set evita duplicati

    @OneToMany(mappedBy = "salaStudio")
    private List<Postazione> postazioni = new ArrayList<>();

    @PostLoad
    public void init(){
        gestorePersistenza = new GestorePersistenza();
    }

    public SalaStudio(){}

    public SalaStudio(String nome, String descrizione, int numeroPostazioni, LocalTime orarioApertura, LocalTime orarioChiusura) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.numeroPostazioni = numeroPostazioni;
        this.orarioApertura = orarioApertura;
        this.orarioChiusura = orarioChiusura;
        init();
    }

    public String getNome() {
        return nome;
    }

    public int getNumeroPostazioni() {
        return numeroPostazioni;
    }

    public List<Postazione> getPostazioni(String area) {
        if(area == null || area.isBlank()){
            return gestorePersistenza.cercaPerCampi(Postazione.class, Map.of("salaStudio", this));
        } else {
            return gestorePersistenza.cercaPerCampi(Postazione.class, Map.of("salaStudio", this, "area", area));
        }
    }

    public List<FasciaOraria>  getFasceOrarie() {
        List<FasciaOraria> fasce = new ArrayList<>();
        LocalTime corrente = this.orarioApertura;

        while (corrente.isBefore(this.orarioChiusura)) {
            fasce.add(new FasciaOraria(corrente, corrente.plusHours(1)));
            corrente = corrente.plusHours(1);
        }
        return fasce;
    }

    // Ritorna il numero di postiLiberi per una data e una fascia oraria
    public int getPostiLiberi(FasciaOraria f, LocalDate data) {
        int count = 0;
        for (Postazione p : getPostazioni(null)) {
            if (p.isDisponibile(f, data)) {
                count++;
            }
        }
        return count;
    }

    public Postazione cercaPrimaPostazioneLibera(FasciaOraria fascia, String area, LocalDate data) {

        for(Postazione p : getPostazioni(area)){
            if (p.isDisponibile(fascia, data)) {
                return p;
            }
        }
        return null;
    }

    public boolean isDisponibilePostazione(List<FasciaOraria> fascia, LocalDate data, String sala) {

        for (Postazione p : getPostazioni(sala)) {
            boolean postazioneDisponibile = true;
            for(FasciaOraria f : fascia) {
                if (!p.isDisponibile(f, data)) {
                    postazioneDisponibile = false;
                    break;
                }
            }
            if (postazioneDisponibile) { return  true; }
        }

        return false;
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