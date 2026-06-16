package control;

import control.RegistrazioneException;
import entity.Bibliotecario;
import entity.RegistroBibliotecari;
import entity.RegistroStudenti;
import entity.Studente;

/**
 * Use Case Controller per l'autenticazione e la registrazione degli utenti.
 * <p>
 * Gestisce il login (con blocco dopo un numero massimo di tentativi falliti) e
 * la registrazione di studenti e bibliotecari. La validazione di formato dei
 * dati è responsabilità del boundary; qui restano le verifiche che richiedono
 * l'accesso alla persistenza (unicità) e la creazione effettiva, segnalando i
 * fallimenti tramite {@link RegistrazioneException}.
 */
public class GestoreAccesso {

    private static final int MAX_TENTATIVI = 3;
    private int contatore = 0;

    private final RegistroStudenti gestoreStudenti = new RegistroStudenti();
    private final RegistroBibliotecari gestoreBibliotecari = new RegistroBibliotecari();

    /**
     * Autentica un utente a partire dalle credenziali fornite.
     * <p>
     * Cerca prima tra gli studenti, poi tra i bibliotecari; incrementa il
     * contatore dei tentativi falliti e lo azzera in caso di successo. L'esito
     * negativo è un caso atteso del flusso e viene segnalato con {@code null}.
     *
     * @param email    email dell'utente
     * @param password password da verificare
     * @return lo {@link Studente} o il {@link Bibliotecario} autenticato, oppure
     *         {@code null} se le credenziali sono errate o l'accesso è bloccato
     */
    public Object loginUtente(String email, String password) {
        if (contatore >= MAX_TENTATIVI)
            return null;
        Studente studente = gestoreStudenti.cercaStudentePerEmail(email);
        if (studente == null) {
            Bibliotecario bibliotecario = gestoreBibliotecari.cercaBibliotecarioPerEmail(email);
            if (bibliotecario == null || !bibliotecario.getPassword().equals(password)) {
                contatore++;
                return null;
            }
            contatore = 0;
            return bibliotecario;
        }
        if (!studente.getPassword().equals(password)) {
            contatore++;
            return null;
        }
        contatore = 0;
        return studente;
    }

    /**
     * Indica se l'accesso è bloccato per aver raggiunto il numero massimo di
     * tentativi di login falliti.
     *
     * @return {@code true} se i tentativi hanno raggiunto {@code MAX_TENTATIVI}
     */
    public boolean isBloccato() {
        return contatore >= MAX_TENTATIVI;
    }

    /**
     * Registra un nuovo studente, verificando l'unicità di email e matricola.
     * <p>
     * I controlli di formato sui dati si assumono già effettuati dal boundary.
     *
     * @param matricola matricola dello studente
     * @param nome      nome
     * @param cognome   cognome
     * @param email     email istituzionale
     * @param password  password
     * @throws RegistrazioneException se email o matricola sono già registrate
     */
    public void registraStudente(Long matricola, String nome, String cognome, String email, String password){
        if (gestoreStudenti.cercaStudentePerEmail(email) != null) {
            throw new RegistrazioneException("Email già associata ad un altro studente");
        }
        if (gestoreStudenti.cercaPerMatricola(matricola) != null) {
            throw new RegistrazioneException("Matricola già registrata");
        }
        gestoreStudenti.creaStudente(matricola, nome, cognome, email, password);
    }

    /**
     * Registra un nuovo bibliotecario, verificando l'unicità di email e codice interno.
     * <p>
     * I controlli di formato sui dati si assumono già effettuati dal boundary.
     *
     * @param codiceInterno codice identificativo del bibliotecario
     * @param nome          nome
     * @param cognome       cognome
     * @param email         email istituzionale
     * @param password      password
     * @throws RegistrazioneException se email o codice interno sono già registrati
     */
    public void registraBibliotecario(Long codiceInterno, String nome, String cognome, String email, String password){
        if (gestoreBibliotecari.cercaBibliotecarioPerEmail(email) != null) {
            throw new RegistrazioneException("Email già associata ad un altro bibliotecario");
        }
        if (gestoreBibliotecari.cercaBibliotecarioPerCodiceInterno(codiceInterno) != null) {
            throw new RegistrazioneException("Codice interno già registrato");
        }
        gestoreBibliotecari.creaBibliotecario(codiceInterno, nome, cognome, email, password);
    }
}