package boundary;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.IOException;
import java.util.Properties;

/**
 * Boundary responsabile dell'interazione con il sistema di posta esterno.
 * Incapsula la configurazione SMTP e l'invio delle notifiche via email,
 * leggendo i parametri di connessione da un file di configurazione esterno.
 */
public class ServizioNotifiche {

    private final String host;
    private final String porta;
    private final boolean auth;
    private final String from;

    /**
     * Costruisce la boundary caricando i parametri SMTP dal file
     * di configurazione esterno {@code config.properties}.
     *
     * @throws IOException se il file di configurazione non è leggibile
     */
    public ServizioNotifiche() {
        Properties config = new Properties();
        try (var in = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties non trovato");
            }
            config.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Errore caricamento configurazione email", e);
        }
        this.host = config.getProperty("mail.host");
        this.porta = config.getProperty("mail.port");
        this.auth = Boolean.parseBoolean(config.getProperty("mail.auth"));
        this.from = config.getProperty("mail.from");
    }

    /**
     * Invia una notifica di conferma prenotazione all'indirizzo indicato.
     *
     * @param destinatario email dello studente destinatario
     * @param nomeSala     nome della sala studio prenotata
     * @param data         data della prenotazione
     * @param fascia       fascia oraria prenotata
     * @return true se l'invio va a buon fine, false in caso di errore
     */
    public boolean inviaNotificaPrenotazione(String destinatario, String nomeSala,
                                             String data, String fascia) {
        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", porta);
        props.put("mail.smtp.auth", String.valueOf(auth));

        Session session = Session.getInstance(props);

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(destinatario));
            msg.setSubject("Conferma prenotazione sala studio");
            msg.setText("Gentile studente,\n\n"
                    + "la tua prenotazione e' stata registrata:\n"
                    + "Sala: " + nomeSala + "\n"
                    + "Data: " + data + "\n"
                    + "Fascia oraria: " + fascia + "\n\n"
                    + "Ricorda di effettuare il check-in.");
            Transport.send(msg);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }
}