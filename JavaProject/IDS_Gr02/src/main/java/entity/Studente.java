package entity;

import dto.FasciaOraria;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Entità di dominio che rappresenta uno studente autenticato del sistema.
 * <p>
 * Lo {@code Studente} è Information Expert dei propri dati anagrafici e di
 * autenticazione, nonché della collezione delle prenotazioni che ha effettuato:
 * questo lo rende il punto di accesso naturale per i casi d'uso che operano
 * sulle prenotazioni del singolo utente (es. {@code VisualizzaPrenotazioniEffettuate}).
 * <p>
 * La matricola funge da chiave naturale e non è generata dal sistema, in quanto
 * assegnata in fase di immatricolazione.
 */
@Entity
public class Studente {

    /** Chiave naturale dello studente; non auto-generata. */
    @Id
    private long matricola;

    private String nome;
    private String cognome;

    /** Email istituzionale, usata anche come recapito per le notifiche. */
    private String email;

    private String password;

    /**
     * Prenotazioni effettuate dallo studente.
     * <p>
     * Lo {@code Studente} aggrega le proprie prenotazioni ed è quindi IE della
     * collezione personale, distinta dal catalogo globale gestito da
     * {@code CatalogoPrenotazioni}.
     */
    @OneToMany
    private List<Prenotazione> prenotazioni;

    /** Costruttore richiesto da JPA. */
    public Studente() {}

    public Studente(long matricola, String nome, String cognome, String email, String password) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
    }

    /** Getter e Setter **/
    public String getPassword() { return password; }
    public String getNome() { return nome; }
    public String getEmail() { return email; }
    public void setNome(String p) { this.nome = p; }

    /**
     * Crea una nuova prenotazione a nome di questo studente.
     * <p>
     * Pattern Creator (GRASP): lo {@code Studente} è Creator di
     * {@link Prenotazione} perché ne aggrega le istanze e possiede il dato
     * inizializzante (sé stesso come titolare). La fascia oraria viene
     * scomposta nei suoi estremi temporali, coerentemente con la natura di DTO
     * di {@link FasciaOraria}.
     *
     * @param postazione postazione da prenotare; se {@code null} la creazione è annullata
     * @param data       giorno della prenotazione
     * @param fascia     fascia oraria scelta, da cui si ricavano ora di inizio e fine
     * @return la {@link Prenotazione} creata, oppure {@code null} se la postazione non è valida
     */
    public Prenotazione creaPrenotazione(Postazione postazione, LocalDate data, FasciaOraria fascia) {
        if (postazione == null) {
            return null;
        }
        return new Prenotazione(data, this, postazione, fascia);
    }
}