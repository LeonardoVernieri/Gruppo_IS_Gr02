package control;

/**
 * Eccezione di dominio sollevata quando una registrazione non può essere
 * completata per violazione di una regola applicativa, tipicamente l'unicità
 * di email, matricola o codice interno.
 * <p>
 * È unchecked ({@link RuntimeException}) così da non vincolare le firme dei
 * metodi di controllo: viene lanciata dal control e catturata dal boundary, che
 * ne mostra il messaggio all'utente. Separa la <i>rilevazione</i> del
 * fallimento (responsabilità del control) dalla sua <i>presentazione</i>
 * (responsabilità del boundary).
 *
 * @see GestoreAccesso
 */
public class RegistrazioneException extends RuntimeException {

    /**
     * @param message messaggio descrittivo dell'errore, destinato a essere
     *                mostrato all'utente dal boundary
     */
    public RegistrazioneException(String message) {
        super(message);
    }
}