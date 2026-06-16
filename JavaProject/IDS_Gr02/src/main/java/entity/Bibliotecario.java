package entity;

import database.GestorePersistenza;
import jakarta.persistence.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entità di dominio che rappresenta un bibliotecario.
 * <p>
 * Il {@code Bibliotecario} è Information Expert dei propri dati anagrafici e di
 * autenticazione e aggrega le {@link SalaStudio} che gestisce. È inoltre Creator
 * delle sale studio, che crea e affida alla persistenza.
 * <p>
 * Il codice interno funge da chiave naturale e non è generato dal sistema.
 */
@Entity
public class Bibliotecario {

    /** Accesso alla persistenza per il salvataggio delle sale; non persistito. */
    @Transient
    private GestorePersistenza gestorePersistenza = new GestorePersistenza();

    /** Chiave naturale del bibliotecario; non auto-generata. */
    @Id
    private long codiceInterno;

    private String nome;
    private String cognome;

    private String email;
    private String password;

    /** Sale studio gestite dal bibliotecario. */
    @OneToMany
    private List<SalaStudio> saleStudio = new ArrayList<>();

    /** Costruttore richiesto da JPA. */
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

    /**
     * Crea una nuova sala studio gestita da questo bibliotecario e la persiste.
     * <p>
     * Pattern Creator (GRASP): il {@code Bibliotecario} aggrega le proprie sale
     * ed è quindi Creator di {@link SalaStudio}. La creazione avviene solo se
     * l'orario di apertura precede quello di chiusura; in caso contrario
     * l'operazione viene rifiutata restituendo {@code false}, lasciando al
     * boundary la segnalazione dell'errore all'utente.
     *
     * @param nome                   nome della sala
     * @param descrizione            descrizione della sala
     * @param numeroPostazioniTotali numero totale di postazioni
     * @param orarioApertura         orario di apertura giornaliero
     * @param orarioChiusura         orario di chiusura giornaliero
     * @param presenzaAree           {@code true} se la sala è suddivisa in aree
     * @param nomiAree               nomi/tipologie delle aree
     * @param postazioniPerArea      numero di postazioni per ciascuna area
     * @return {@code true} se la sala è stata creata, {@code false} se l'orario non è valido
     */
    public boolean creaSalaStudio(String nome,
                                  String descrizione,
                                  int numeroPostazioniTotali,
                                  LocalTime orarioApertura,
                                  LocalTime orarioChiusura,
                                  boolean presenzaAree,
                                  List<String> nomiAree,
                                  List<Integer> postazioniPerArea){
        if(orarioApertura.isBefore(orarioChiusura)){
            SalaStudio s = new SalaStudio(nome, descrizione, numeroPostazioniTotali, orarioApertura, orarioChiusura, presenzaAree, nomiAree, postazioniPerArea);
            gestorePersistenza.salva(s);
            return true;
        }
        return false;
    }
}