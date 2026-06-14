package entity;

import dto.FasciaOraria;
import jakarta.persistence.*;
import org.hibernate.tool.schema.spi.DelayedDropRegistryNotAvailableImpl;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Studente {

    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long matricola;

    private String nome;
    private String cognome;
    private String email;
    private String password;

    @OneToMany
    private List<Prenotazione> prenotazioni;

    public Studente() {}

    public Studente(long matricola, String nome, String cognome, String email, String password) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }

    public String getPassword() { return password; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }


    public void setNome(String p) { this.nome = p; }


    public Prenotazione creaPrenotazione(Postazione postazione, LocalDate data, FasciaOraria fascia) {
        if (postazione == null) {
            return null;
        }
        return new Prenotazione(data, this, postazione, fascia.getOraInizio(), fascia.getOraFine());
    }

}
