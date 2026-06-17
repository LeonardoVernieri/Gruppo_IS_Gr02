package boundary;

import control.GestorePrenotazioni;
import control.Sessione;
import entity.Prenotazione;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Form (boundary) per l'effettuazione del check-in su una prenotazione attiva.
 * <p>
 * Mostra allo studente le sue prenotazioni attive odierne e gli consente di
 * confermare la presenza su quella selezionata. Delega la logica a
 * {@link GestorePrenotazioni}; non contiene regole di dominio né accede alla
 * persistenza. Eredita aspetto e componenti grafici da {@link BaseForm}.
 */
public class FormCheckIn extends BaseForm {

    private final JList<Prenotazione>    listaPrenotazioni;
    private final GestorePrenotazioni    gestorePrenotazioni;

    /**
     * Costruisce il form, recupera le prenotazioni attive dello studente in
     * sessione e le presenta in una lista selezionabile.
     */
    public FormCheckIn() {
        super();

        gestorePrenotazioni = new GestorePrenotazioni();

        // ── Root ─────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BG_PAGE);
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        setContentPane(root);

        // ── Card ─────────────────────────────────────────────────────────────
        RoundedCard card = new RoundedCard();
        card.setLayout(new BorderLayout(0, 16));
        card.setBorder(new EmptyBorder(28, 28, 28, 28));

        // Titolo
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel titolo = new JLabel("Effettua Check-In");
        titolo.setFont(FONT_BOLD.deriveFont(20f));
        titolo.setForeground(TEXT_PRIMARY);
        titolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(titolo);

        JLabel sottotitolo = new JLabel("Seleziona la prenotazione per cui fare il check-in");
        sottotitolo.setFont(FONT_REGULAR.deriveFont(12f));
        sottotitolo.setForeground(TEXT_TERTIARY);
        sottotitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(sottotitolo);
        headerPanel.add(Box.createVerticalStrut(12));
        headerPanel.add(buildDivider());

        card.add(headerPanel, BorderLayout.NORTH);

        // Popola la lista con le prenotazioni attive dello studente in sessione
        listaPrenotazioni = new JList<>();
        listaPrenotazioni.setFont(FONT_REGULAR);
        listaPrenotazioni.setBackground(BG_CARD);
        listaPrenotazioni.setForeground(TEXT_PRIMARY);
        listaPrenotazioni.setSelectionBackground(BLUE_BG);
        listaPrenotazioni.setSelectionForeground(BLUE_TEXT);
        listaPrenotazioni.setFixedCellHeight(40);
        listaPrenotazioni.setBorder(new EmptyBorder(4, 8, 4, 8));
        List<Prenotazione> prenotazioni = gestorePrenotazioni
                .cercaPrenotazioniAttive(Sessione.getInstance().getStudenteCorrente());

        if (prenotazioni.isEmpty()) {
            // Empty state: nessuna prenotazione attiva per oggi
            JLabel emptyLabel = new JLabel("Non hai prenotazioni attive per la giornata di oggi");
            emptyLabel.setFont(FONT_REGULAR.deriveFont(13f));
            emptyLabel.setForeground(TEXT_TERTIARY);
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setBorder(new EmptyBorder(40, 0, 40, 0));
            card.add(emptyLabel, BorderLayout.CENTER);
        } else {
            DefaultListModel<Prenotazione> model = new DefaultListModel<>();
            for (Prenotazione p : prenotazioni) model.addElement(p);
            listaPrenotazioni.setModel(model);

            JScrollPane scrollPane = new JScrollPane(listaPrenotazioni,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
            scrollPane.setOpaque(false);
            scrollPane.setPreferredSize(new Dimension(380, 320));

            JScrollBar vsb = scrollPane.getVerticalScrollBar();
            vsb.setPreferredSize(new Dimension(6, 0));
            vsb.setUI(new SlimScrollBarUI());

            card.add(scrollPane, BorderLayout.CENTER);
        }

        // Bottoni
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setOpaque(false);
        bottomPanel.add(buildDivider());
        bottomPanel.add(Box.createVerticalStrut(12));

        RoundedButton btnCheckIn = new RoundedButton("Effettua Check-Ien", true);
        btnCheckIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCheckIn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnCheckIn.addActionListener(e -> eseguiCheckIn());
        bottomPanel.add(btnCheckIn);
        bottomPanel.add(Box.createVerticalStrut(10));
        if(prenotazioni.isEmpty()) {
            btnCheckIn.setVisible(false);
        } else {
            btnCheckIn.setVisible(true);
        }

        RoundedButton btnAnnulla = new RoundedButton("Annulla", false);
        btnAnnulla.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAnnulla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnAnnulla.addActionListener(e -> {new FormStudente().setVisible(true); dispose();});
        bottomPanel.add(btnAnnulla);

        card.add(bottomPanel, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── Logica ────────────────────────────────────────────────────────────────

    /**
     * Esegue il check-in sulla prenotazione selezionata.
     * <p>
     * Verifica che una prenotazione sia selezionata e non scaduta, chiede
     * conferma all'utente e, in caso affermativo, delega il check-in a
     * {@link GestorePrenotazioni#effettuaCheckIn(Prenotazione)}, tornando poi al
     * menu studente.
     */
    private void eseguiCheckIn() {
        Prenotazione selezionata = listaPrenotazioni.getSelectedValue();
        if (selezionata == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleziona una prenotazione!",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (gestorePrenotazioni.isPrenotazioneScaduta(selezionata)) {
            JOptionPane.showMessageDialog(this,
                    "Impossibile effettuare il check-in: prenotazione scaduta!",
                    "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int risposta = JOptionPane.showConfirmDialog(this,
                "Confermi il check-in?", "Conferma", JOptionPane.YES_NO_OPTION);
        if (risposta == JOptionPane.YES_OPTION) {
           boolean esito= gestorePrenotazioni.effettuaCheckIn(selezionata);
            if(esito==true){
            JOptionPane.showMessageDialog(this,
                    "Check-in effettuato con successo!",
                    "Successo", JOptionPane.INFORMATION_MESSAGE);
            new FormStudente().setVisible(true);
            dispose();}else if(esito==false){
                JOptionPane.showMessageDialog(this,
                        "Check-in non effettuato, tempo limite scaduto",
                        "Fallito", JOptionPane.INFORMATION_MESSAGE);

            }
        }
    }
}