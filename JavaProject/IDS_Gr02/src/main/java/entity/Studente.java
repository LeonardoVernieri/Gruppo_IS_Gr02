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

    public Studente(String nome, String cognome, String email, String password) {
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }

    public Prenotazione creaPrenotazione(Postazione postazione, LocalDate data, FasciaOraria fascia) {
        return new Prenotazione(data, this, postazione, fascia.getOraInizio(), fascia.getOraFine());
    }

}
