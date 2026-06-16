package boundary;

import control.GestoreAccesso;
import control.RegistrazioneException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Form (boundary) per la registrazione di un nuovo utente.
 * <p>
 * Raccoglie i dati anagrafici e il ruolo (studente o bibliotecario, con il
 * relativo identificativo: matricola o codice interno), esegue la validazione di
 * formato e delega la creazione a {@link GestoreAccesso}. Gli errori di regola
 * applicativa (es. unicità) sollevati dal control sono segnalati tramite
 * {@link RegistrazioneException} e mostrati all'utente. Eredita aspetto e
 * componenti grafici da {@link BaseForm}.
 */
public class FormRegistrazione extends BaseForm {

    private JTextField     nomeField;
    private JTextField     cognomeField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JComboBox<String> ruoloComboBox;
    private JLabel         campoAggiuntivoLabel;
    private JTextField     campoAggiuntivoField;
    private JLabel         messaggioLabel;

    private GestoreAccesso gestoreAccesso;

    public FormRegistrazione(FormLogin parent) {
        super();

        gestoreAccesso = new GestoreAccesso();

        // ── Root ─────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_PAGE);
        setContentPane(root);

        // ── Card ─────────────────────────────────────────────────────────────
        RoundedCard card = new RoundedCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 32, 32, 32));

        // Titolo
        JLabel titolo = new JLabel("Registrazione");
        titolo.setFont(FONT_BOLD.deriveFont(20f));
        titolo.setForeground(TEXT_PRIMARY);
        titolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titolo);

        JLabel sottotitolo = new JLabel("Crea un nuovo account");
        sottotitolo.setFont(FONT_REGULAR.deriveFont(12f));
        sottotitolo.setForeground(TEXT_TERTIARY);
        sottotitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sottotitolo);
        card.add(Box.createVerticalStrut(24));

        // Nome
        card.add(buildSectionLabel("Nome"));
        card.add(Box.createVerticalStrut(6));
        nomeField = buildTextField("es. Mario");
        card.add(nomeField);
        card.add(Box.createVerticalStrut(12));

        // Cognome
        card.add(buildSectionLabel("Cognome"));
        card.add(Box.createVerticalStrut(6));
        cognomeField = buildTextField("es. Rossi");
        card.add(cognomeField);
        card.add(Box.createVerticalStrut(12));

        // Email
        card.add(buildSectionLabel("Email"));
        card.add(Box.createVerticalStrut(6));
        emailField = buildTextField("es. mario.rossi@studenti.it");
        card.add(emailField);
        card.add(Box.createVerticalStrut(12));

        // Password
        card.add(buildSectionLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        card.add(passwordField);
        card.add(Box.createVerticalStrut(12));

        // Ruolo
        card.add(buildSectionLabel("Ruolo"));
        card.add(Box.createVerticalStrut(6));
        ruoloComboBox = new JComboBox<>();
        ruoloComboBox.addItem("Studente");
        ruoloComboBox.addItem("Bibliotecario");
        ruoloComboBox.setFont(FONT_REGULAR);
        ruoloComboBox.setBackground(BG_CARD);
        ruoloComboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        ruoloComboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        ruoloComboBox.addActionListener(e -> aggiornaCampoAggiuntivo());
        card.add(ruoloComboBox);
        card.add(Box.createVerticalStrut(12));

        // Campo aggiuntivo (matricola / codice identificativo)
        campoAggiuntivoLabel = buildSectionLabel("Matricola");
        card.add(campoAggiuntivoLabel);
        card.add(Box.createVerticalStrut(6));
        campoAggiuntivoField = buildTextField("");
        card.add(campoAggiuntivoField);
        card.add(Box.createVerticalStrut(24));

        // Bottone registrati (filled)
        RoundedButton registratiButton = new RoundedButton("Registrati", true);
        registratiButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registratiButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        registratiButton.addActionListener(e -> eseguiRegistrazione());
        card.add(registratiButton);
        card.add(Box.createVerticalStrut(10));

        // Bottone annulla (outline)
        RoundedButton annullaButton = new RoundedButton("Annulla", false);
        annullaButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        annullaButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        annullaButton.addActionListener(e -> {
            if (parent != null) parent.setVisible(true);
            dispose();
        });
        card.add(annullaButton);
        card.add(Box.createVerticalStrut(16));

        // Messaggio esito
        messaggioLabel = new JLabel(" ");
        messaggioLabel.setFont(FONT_REGULAR.deriveFont(12f));
        messaggioLabel.setForeground(TEXT_TERTIARY);
        messaggioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(messaggioLabel);

        JScrollPane scrollPane = new JScrollPane(card,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(400, 520));

        JScrollBar vsb = scrollPane.getVerticalScrollBar();
        vsb.setPreferredSize(new Dimension(6, 0));
        vsb.setUI(new SlimScrollBarUI());

        root.add(scrollPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── Logica ────────────────────────────────────────────────────────────────

    /**
     * Aggiorna l'etichetta e svuota il campo identificativo in base al ruolo
     * selezionato (matricola per lo studente, codice identificativo per il
     * bibliotecario).
     */
    private void aggiornaCampoAggiuntivo() {
        String ruolo = (String) ruoloComboBox.getSelectedItem();
        if ("Studente".equals(ruolo)) {
            campoAggiuntivoLabel.setText("MATRICOLA");
        } else {
            campoAggiuntivoLabel.setText("CODICE IDENTIFICATIVO");
        }
        campoAggiuntivoField.setText("");
    }

    /**
     * Raccoglie i dati, esegue la validazione di formato e delega la
     * registrazione al control in base al ruolo.
     * <p>
     * L'identificativo viene interpretato in modo difensivo: se assente o non
     * numerico resta {@code null} e il caso è gestito da {@link #validaDati}. Gli
     * errori di regola applicativa sollevati dal control sono mostrati all'utente.
     */
    private void eseguiRegistrazione() {
        String nome     = nomeField.getText().trim();
        String cognome  = cognomeField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String ruolo    = (String) ruoloComboBox.getSelectedItem();

        Long extra = null;
        try {
            extra = Long.valueOf(campoAggiuntivoField.getText().trim());
        } catch (NumberFormatException ex) {
            extra = null;
        }

        if (!validaDati(nome, cognome, email, extra, ruolo)) {
            return;
        }

        try {
            if ("Studente".equals(ruolo)) {
                gestoreAccesso.registraStudente(extra, nome, cognome, email, password);
                JOptionPane.showMessageDialog(null, "Registrazione studente completata");
            } else {
                gestoreAccesso.registraBibliotecario(extra, nome, cognome, email, password);
                JOptionPane.showMessageDialog(null, "Registrazione bibliotecario completata");
            }
            new FormLogin().setVisible(true);
            dispose();
        } catch (RegistrazioneException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    /**
     * Valida il formato dei dati di registrazione: nome e cognome non vuoti,
     * email contenente "@" e identificativo presente per il ruolo scelto.
     *
     * @return {@code true} se i dati superano i controlli di formato
     */
    private boolean validaDati(String nome, String cognome, String email, Long extra, String ruolo) {
        if (nome == null || nome.isBlank()) {
            JOptionPane.showMessageDialog(null, "Nome obbligatorio");
            return false;
        }
        if (cognome == null || cognome.isBlank()) {
            JOptionPane.showMessageDialog(null, "Cognome obbligatorio");
            return false;
        }
        if (email == null || !email.contains("@")) {
            JOptionPane.showMessageDialog(null, "Email non valida");
            return false;
        }
        if (extra == null && ruolo.equals("Studente")) {
            JOptionPane.showMessageDialog(null, "Inserire matricola");
            return false;
        }
        if (extra == null && ruolo.equals("Bibliotecario")) {
            JOptionPane.showMessageDialog(null, "Inserire codice identificativo");
            return false;
        }
        return true;
    }
}