package boundary;

import com.icegreen.greenmail.util.GreenMailUtil;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Boundary di sola lettura sulla casella di posta: mostra le notifiche
 * email catturate dal server SMTP embedded (GreenMail). Non invia nulla
 * e non conosce la logica di prenotazione — legge soltanto i messaggi
 * ricevuti tramite {@link ServerMailEmbedded}.
 * <p>
 * Finestra dimostrativa: in un sistema reale le notifiche sarebbero
 * recapitate via email allo studente; qui sono catturate localmente
 * ed esposte a scopo di dimostrazione. Viene riusata un'unica istanza
 * tramite {@link #mostra(Window)}, così non si accumulano più finestre.
 */
public class FormCasellaNotifiche extends JDialog {

    private static FormCasellaNotifiche istanza;

    private final DefaultListModel<MimeMessage> modello = new DefaultListModel<>();
    private final JList<MimeMessage> lista = new JList<>(modello);
    private final JTextArea dettaglio = new JTextArea();

    private FormCasellaNotifiche(Window parent) {
        super(parent, "Casella notifiche", ModalityType.MODELESS);
        buildUI();
        aggiorna();
        setResizable(false);
        setSize(640, 440);
        setLocationRelativeTo(parent);
        setLocation(getX() - 320, getY());
    }

    /**
     * Apre la casella dimostrativa, riusando l'unica istanza esistente.
     * Chiamate ripetute non creano nuove finestre: riportano in primo piano
     * quella già aperta e ne aggiornano il contenuto.
     *
     * @param parent finestra proprietaria (es. quella di {@code FormStudente})
     */
    public static void mostra(Window parent) {
        if (istanza == null) {
            istanza = new FormCasellaNotifiche(parent);
        } else {
            istanza.aggiorna();
        }
        istanza.setVisible(true);
        istanza.toFront();
    }

    private void buildUI() {
        JLabel avviso = new JLabel("<html><b>Finestra dimostrativa.</b> "
                + "Simula la casella di posta dello studente: in un sistema reale queste "
                + "notifiche verrebbero recapitate via email. Qui sono catturate da un "
                + "server SMTP locale (GreenMail) ed esposte solo a scopo di dimostrazione.</html>");
        avviso.setOpaque(true);
        avviso.setBackground(new Color(255, 249, 196)); // giallo nota, tenue
        avviso.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        lista.setCellRenderer(new RenderMessaggio());
        lista.addListSelectionListener(e -> mostraDettaglio(lista.getSelectedValue()));

        dettaglio.setEditable(false);
        dettaglio.setLineWrap(true);
        dettaglio.setWrapStyleWord(true);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(lista), new JScrollPane(dettaglio));
        split.setDividerLocation(170);

        JButton aggiorna = new JButton("Aggiorna");
        aggiorna.addActionListener(e -> aggiorna());
        JPanel sud = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sud.add(aggiorna);

        setLayout(new BorderLayout());
        add(avviso, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(sud, BorderLayout.SOUTH);
    }

    /** Ricarica la lista dei messaggi catturati da GreenMail. */
    private void aggiorna() {
        modello.clear();
        MimeMessage[] messaggi = ServerMailEmbedded.getIstanza().getMessaggiRicevuti();
        Arrays.stream(messaggi).forEach(modello::addElement);
    }

    private void mostraDettaglio(MimeMessage msg) {
        if (msg == null) {
            dettaglio.setText("");
            return;
        }
        try {
            Address[] to = msg.getRecipients(Message.RecipientType.TO);
            String destinatari = (to == null) ? "" : String.join(", ",
                    Arrays.stream(to).map(Object::toString).toArray(String[]::new));
            dettaglio.setText("Da:      " + msg.getFrom()[0] + "\n"
                    + "A:       " + destinatari + "\n"
                    + "Oggetto: " + msg.getSubject() + "\n"
                    + "Data:    " + msg.getSentDate() + "\n\n"
                    + GreenMailUtil.getBody(msg));
        } catch (MessagingException ex) {
            dettaglio.setText("Impossibile leggere il messaggio: " + ex.getMessage());
        }
    }

    /** Renderer: nella lista mostra l'oggetto di ciascun messaggio. */
    private static class RenderMessaggio extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellFocus);
            if (value instanceof MimeMessage msg) {
                try {
                    setText(msg.getSubject());
                } catch (MessagingException e) {
                    setText("(messaggio illeggibile)");
                }
            }
            return this;
        }
    }
}