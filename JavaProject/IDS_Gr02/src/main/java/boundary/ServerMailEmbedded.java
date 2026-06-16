package boundary;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.util.Properties;

/**
 * Server SMTP embedded (GreenMail) avviato in-process per la cattura
 * delle notifiche email, senza richiedere installazioni o container esterni.
 * <p>
 * La porta di ascolto è letta da {@code config.properties}, la stessa
 * sorgente usata da {@link ServizioNotifiche}, per garantire coerenza:
 * cambiando {@code mail.port} si riconfigurano sia l'invio sia la cattura.
 */
public final class ServerMailEmbedded {

    private static ServerMailEmbedded istanza;

    private final GreenMail greenMail;

    private ServerMailEmbedded() {
        Properties config = caricaConfig();
        String host = config.getProperty("mail.host", "localhost");
        int porta = Integer.parseInt(config.getProperty("mail.port", "3025"));
        ServerSetup setup = new ServerSetup(porta, host, ServerSetup.PROTOCOL_SMTP);
        this.greenMail = new GreenMail(setup);
    }

    /**
     * Avvia il server (idempotente) e ne registra lo spegnimento automatico
     * alla chiusura della JVM.
     *
     * @return l'istanza attiva del server embedded
     */
    public static synchronized ServerMailEmbedded avvia() {
        if (istanza == null) {
            istanza = new ServerMailEmbedded();
            istanza.greenMail.start();
            Runtime.getRuntime().addShutdownHook(new Thread(istanza::ferma));
        }
        return istanza;
    }

    /** Ferma il server, se attivo. */
    public synchronized void ferma() {
        if (greenMail != null) {
            greenMail.stop();
        }
    }

    /**
     * Restituisce le email catturate finora. Usato dal pannello di
     * visualizzazione delle notifiche (boundary di sola lettura sulla casella).
     */
    public MimeMessage[] getMessaggiRicevuti() {
        return greenMail.getReceivedMessages();
    }

    private static Properties caricaConfig() {
        Properties config = new Properties();
        try (var in = ServerMailEmbedded.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException("config.properties non trovato");
            }
            config.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Errore caricamento configurazione email", e);
        }
        return config;
    }

    public static ServerMailEmbedded getIstanza() {
        if (istanza == null) {
            throw new IllegalStateException("Server mail non avviato: chiamare avvia() nel main");
        }
        return istanza;
    }
}