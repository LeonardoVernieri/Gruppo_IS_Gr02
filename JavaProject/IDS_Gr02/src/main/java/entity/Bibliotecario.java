package entity;

import database.GestorePersistenza;
import jakarta.persistence.*;

import javax.swing.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Bibliotecario{

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long codiceInterno;

    private String nome;
    private String cognome;
    private String email;
    private String password;

    @OneToMany
    private List<SalaStudio> saleStudio = new ArrayList<>();

    public Bibliotecario() {}

    public Bibliotecario(Long codiceInterno, String nome,
                         String cognome, String email,
                         String password) {
        this.codiceInterno = codiceInterno;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    public String getNome() { return nome; }
    public void setNome(String p) { this.nome = p; }
    public String getCognome() { return cognome; }
    public void setCognome(String p) { this.cognome = p; }
    @Transient
    public static final GestorePersistenza gp = new GestorePersistenza();

    public boolean creaSalaStudio(String nome,
                                     String descrizione,
                                     int numeroPostazioniTotali,
                                     LocalTime orarioApertura,
                                     LocalTime orarioChiusura,
                                     boolean presenzaAree){

        if(orarioApertura.isBefore(orarioChiusura)){
            SalaStudio s = new SalaStudio(nome, descrizione, numeroPostazioniTotali, orarioApertura, orarioChiusura, presenzaAree);
            gp.salva(s);
            saleStudio.add(s);
            return true;
        }
        JOptionPane.showMessageDialog(null, "L'orario di apertura non può essere successivo o uguale a quello di chiusura");
        return false;

    }

    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    public String getNome() { return nome; }
    public void setNome(String p) { this.nome = p; }
    public String getCognome() { return cognome; }
    public void setCognome(String p) { this.cognome = p; }
}