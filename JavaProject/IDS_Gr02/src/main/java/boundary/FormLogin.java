package boundary;

import control.GestoreAccesso;
import control.Sessione;
import entity.Bibliotecario;
import entity.Studente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Form (boundary) di accesso al sistema.
 * <p>
 * Raccoglie le credenziali e delega l'autenticazione a {@link GestoreAccesso};
 * in base al ruolo dell'utente autenticato apre la sessione tramite
 * {@link Sessione} e instrada verso il form corrispondente (studente o
 * bibliotecario). Consente inoltre l'accesso alla registrazione. Eredita aspetto
 * e componenti grafici da {@link BaseForm}.
 */
public class FormLogin extends BaseForm {

    private JTextField     emailField;
    private JPasswordField passwordField;
    private JLabel         messaggioLabel;

    private final GestoreAccesso gestoreAccesso = new GestoreAccesso();

    public FormLogin() {
        super();

        // ── Root ─────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_PAGE);
        setContentPane(root);

        // ── Card centrale ────────────────────────────────────────────────────
        RoundedCard card = new RoundedCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 32, 32, 32));
        card.setPreferredSize(new Dimension(320, 380));

        // Titolo
        JLabel titolo = new JLabel("Accedi");
        titolo.setFont(FONT_BOLD.deriveFont(20f));
        titolo.setForeground(TEXT_PRIMARY);
        titolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titolo);

        JLabel sottotitolo = new JLabel("Sistema di prenotazione sale studio");
        sottotitolo.setFont(FONT_REGULAR.deriveFont(12f));
        sottotitolo.setForeground(TEXT_TERTIARY);
        sottotitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sottotitolo);
        card.add(Box.createVerticalStrut(28));

        // Email
        card.add(buildSectionLabel("Email"));
        card.add(Box.createVerticalStrut(6));
        emailField = buildTextField("es. mario.rossi@studenti.it");
        card.add(emailField);
        card.add(Box.createVerticalStrut(16));

        // Password
        card.add(buildSectionLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        passwordField = new JPasswordField();
        styleTextField(passwordField);
        card.add(passwordField);
        card.add(Box.createVerticalStrut(24));

        // Bottone login (filled)
        RoundedButton loginButton = new RoundedButton("Accedi", true);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loginButton.addActionListener(e -> eseguiLogin());
        card.add(loginButton);
        card.add(Box.createVerticalStrut(10));

        // Bottone registrati (outline)
        RoundedButton registratiButton = new RoundedButton("Registrati", false);
        registratiButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        registratiButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        registratiButton.addActionListener(e -> apriRegistrazione());
        card.add(registratiButton);
        card.add(Box.createVerticalStrut(16));

        // Messaggio esito
        messaggioLabel = new JLabel(" ");
        messaggioLabel.setFont(FONT_REGULAR.deriveFont(12f));
        messaggioLabel.setForeground(TEXT_TERTIARY);
        messaggioLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(messaggioLabel);

        root.add(card);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── Logica ────────────────────────────────────────────────────────────────

    /**
     * Esegue il tentativo di login con le credenziali inserite.
     * <p>
     * Se l'accesso è bloccato per troppi tentativi, avvisa e chiude. Altrimenti
     * delega a {@link GestoreAccesso#loginUtente}: in caso di esito negativo
     * mostra un messaggio d'errore (distinguendo il blocco dalle credenziali
     * errate), in caso positivo apre la sessione del ruolo corrispondente e apre
     * il form adeguato.
     */
    private void eseguiLogin() {
        if (gestoreAccesso.isBloccato()) {
            JOptionPane.showMessageDialog(null, "Tentativi massimi raggiunti");
            dispose();
            return;
        }
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        Object utente   = gestoreAccesso.loginUtente(email, password);

        if (utente == null) {
            if (gestoreAccesso.isBloccato()) {
                JOptionPane.showMessageDialog(this, "Account bloccato: troppi tentativi falliti.", "Errore", JOptionPane.ERROR_MESSAGE);
                messaggioLabel.setText("Tentativi massimi raggiunti.");
            } else {
                JOptionPane.showMessageDialog(this, "Credenziali errate, o registrazione non effettuata", "Errore di Accesso", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        messaggioLabel.setForeground(GREEN_TEXT);
        if (utente instanceof Studente s) {
            messaggioLabel.setText("Benvenuto " + s.getNome() + "!");
            Sessione session = Sessione.getInstance();
            session.apriSessioneStudente(s);
            new FormStudente().setVisible(true);
            dispose();
        } else if (utente instanceof Bibliotecario b) {
            messaggioLabel.setText("Benvenuto " + b.getNome() + "!");
            Sessione session = Sessione.getInstance();
            session.apriSessioneBibliotecario(b);
            new FormBibliotecario().setVisible(true);
            dispose();
        }
    }

    /** Apre il form di registrazione e chiude quello di login. */
    private void apriRegistrazione() {
        new FormRegistrazione(this).setVisible(true);
        dispose();
    }
}