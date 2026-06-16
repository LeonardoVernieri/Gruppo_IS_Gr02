package entity;

import database.GestorePersistenza;

import java.util.Map;

/**
 * Use Case Controller responsabile della gestione degli studenti.
 * <p>
 * Coordina la ricerca e la creazione degli {@link Studente}, delegando ogni
 * accesso ai dati a {@link GestorePersistenza}. Coerentemente con il pattern
 * BCE, è il control a interrogare la persistenza, restituendo entità di dominio
 * agli strati superiori.
 */
public class RegistroStudenti {

    private final GestorePersistenza gestorePersistenza = new GestorePersistenza();

    /**
     * Cerca lo studente associato a una email istituzionale.
     *
     * @param email email da cercare
     * @return lo {@link Studente} corrispondente, oppure {@code null} se nessuno corrisponde
     */
    public Studente cercaStudentePerEmail(String email) {
        return gestorePersistenza.cercaPrimoPerCampi(
                Studente.class,
                Map.of("email", email)
        );
    }

    /**
     * Cerca lo studente associato a una matricola.
     *
     * @param matricola matricola da cercare
     * @return lo {@link Studente} corrispondente, oppure {@code null} se nessuno corrisponde
     */
    public Studente cercaPerMatricola(Long matricola) {
        return gestorePersistenza.cercaPrimoPerCampi(
                Studente.class,
                Map.of("matricola", matricola)
        );
    }

    /**
     * Crea e persiste un nuovo studente a partire dai dati di registrazione.
     *
     * @param matricola matricola (chiave naturale)
     * @param nome      nome
     * @param cognome   cognome
     * @param email     email istituzionale
     * @param password  credenziale di autenticazione
     */
    public void creaStudente(Long matricola, String nome,
                             String cognome, String email,
                             String password) {
        Studente studente = new Studente(matricola, nome, cognome, email, password);
        gestorePersistenza.salva(studente);
    }
}