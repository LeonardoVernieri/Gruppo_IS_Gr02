package control;

import entity.Bibliotecario;
import entity.Studente;

/**
 * Singleton (GoF) che rappresenta la sessione utente corrente dell'applicazione.
 * <p>
 * Mantiene il riferimento all'utente autenticato — {@link Bibliotecario} oppure
 * {@link Studente} — e ne espone il ruolo.
 * <p>
 * Invariante: al più uno tra bibliotecario e studente è valorizzato; l'apertura
 * di una sessione per un ruolo azzera l'altro, così che il ruolo corrente sia
 * sempre univoco.
 */
public class Sessione {

    private static Sessione instance;

    private Bibliotecario bibliotecarioCorrente;
    private Studente studenteCorrente;

    /** Costruttore privato: l'istanza si ottiene solo tramite {@link #getInstance()}. */
    private Sessione() {}

    /**
     * Restituisce l'unica istanza della sessione, creandola alla prima chiamata.
     *
     * @return l'istanza condivisa di {@code Sessione}
     */
    public static Sessione getInstance() {
        if (instance == null) {
            instance = new Sessione();
        }
        return instance;
    }

    /**
     * Apre una sessione per un bibliotecario, azzerando un'eventuale sessione studente.
     *
     * @param b bibliotecario autenticato
     */
    public void apriSessioneBibliotecario(Bibliotecario b) {
        this.bibliotecarioCorrente = b;
        this.studenteCorrente = null;
    }

    /**
     * Apre una sessione per uno studente, azzerando un'eventuale sessione bibliotecario.
     *
     * @param s studente autenticato
     */
    public void apriSessioneStudente(Studente s) {
        this.studenteCorrente = s;
        this.bibliotecarioCorrente = null;
    }

    public Bibliotecario getBibliotecarioCorrente() {
        return bibliotecarioCorrente;
    }

    public Studente getStudenteCorrente() {
        return studenteCorrente;
    }

    /** @return {@code true} se la sessione corrente appartiene a un bibliotecario */
    public boolean isBibliotecario() {
        return bibliotecarioCorrente != null;
    }

    /** @return {@code true} se la sessione corrente appartiene a uno studente */
    public boolean isStudente() {
        return studenteCorrente != null;
    }

    /** Chiude la sessione corrente, rimuovendo qualsiasi utente autenticato (logout). */
    public void chiudiSessione() {
        this.bibliotecarioCorrente = null;
        this.studenteCorrente = null;
    }
}