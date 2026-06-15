package entity;

import database.GestorePersistenza;
import dto.FasciaOraria;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

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

    @OneToMany(mappedBy = "salaStudio", cascade = CascadeType.ALL)
    private List<Area> aree = new ArrayList<>();

    // Associazione con bibliotecari
    @ManyToMany
    private Set<Bibliotecario> bibliotecari = new HashSet<>(); // Set evita duplicati

    @OneToMany(mappedBy = "salaStudio", cascade = CascadeType.ALL)
    private List<Postazione> postazioni = new ArrayList<>();

    @PostLoad
    public void init(){
        gestorePersistenza = new GestorePersistenza();
    }

    public SalaStudio(){}

    public SalaStudio(String nome, String descrizione, int numeroPostazioni, LocalTime orarioApertura, LocalTime orarioChiusura,  boolean presenzaAree, List<String> col1, List<Integer> col2) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.numeroPostazioni = numeroPostazioni;
        this.orarioApertura = orarioApertura;
        this.orarioChiusura = orarioChiusura;
        this.presenzaAree = presenzaAree;
        this.aree = new ArrayList<>();
        init();

        // Creo postazioni
        for( int i=0 ; i<numeroPostazioni; i++){
            Postazione p = new Postazione(this);
            this.postazioni.add(p);
        }

        // Crea aree
        int offset = 0;
        for (int i = 0; i < col1.size(); i++) {
            int count = col2.get(i);
            Area area = new Area(col1.get(i), count, this);
            this.aree.add(area);

            // Prendo una subList delle postazioni che non stato assegnate
            this.postazioni.subList(offset, offset + count)
                    .forEach(p -> p.setArea(area));
            offset += count;
            }
        }


    // Setter
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

    // Getter
    public String getNome() {
        return nome;
    }

    public int getNumeroPostazioni() {
        return numeroPostazioni;
    }

    public List<Area> getAree() {
        return gestorePersistenza.cercaPerCampo(Area.class, "salaStudio", this);
    }

    public Area getArea(String tipologiaArea) {
        return gestorePersistenza.cercaPrimoPerCampi(Area.class, Map.of("tipologia", tipologiaArea, "salaStudio", this));
    }

    // Metodi
    public List<Postazione> getPostazioni(Area area) {
        if(area == null){
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

    public boolean isDisponibilePostazione(List<FasciaOraria> fascia, LocalDate data) {

        for (Postazione p : getPostazioni(null)) {
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
}