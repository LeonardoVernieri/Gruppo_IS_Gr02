package boundary;

import control.Sessione;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Form del menu principale del bibliotecario.
 * <p>
 * Presenta le azioni disponibili per il ruolo bibliotecario (creazione sala e,
 * in prospettiva, modifica, eliminazione, monitoraggio e storico). Da qui si
 * accede ai form specifici; al momento è attiva la sola creazione sala, mentre
 * le restanti voci mostrano un avviso di funzionalità in arrivo. Eredita aspetto
 * e componenti grafici da {@link BaseForm}.
 */
public class FormBibliotecario extends BaseForm {

    public FormBibliotecario() {
        super();

        // ── Root ─────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_PAGE);
        setContentPane(root);

        // ── Card ─────────────────────────────────────────────────────────────
        RoundedCard card = new RoundedCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 32, 32, 32));
        card.setPreferredSize(new Dimension(340, 400));

        JLabel titolo = new JLabel("Pannello Bibliotecario");
        titolo.setFont(FONT_BOLD.deriveFont(20f));
        titolo.setForeground(TEXT_PRIMARY);
        titolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titolo);

        JLabel sottotitolo = new JLabel("Cosa vuoi fare?");
        sottotitolo.setFont(FONT_REGULAR.deriveFont(12f));
        sottotitolo.setForeground(TEXT_TERTIARY);
        sottotitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sottotitolo);
        card.add(Box.createVerticalStrut(28));

        // Bottoni: la sola "Crea sala" è attiva, le altre sono placeholder
        card.add(buildMenuButton("Crea sala", true, e -> {
            new FormCreaSala().setVisible(true);
            setVisible(false);
        }));
        card.add(Box.createVerticalStrut(10));
        card.add(buildMenuButton("Modifica sala", false, e -> mostraFunzioneProssimamente()));
        card.add(Box.createVerticalStrut(10));
        card.add(buildMenuButton("Elimina sala", false, e -> mostraFunzioneProssimamente()));
        card.add(Box.createVerticalStrut(10));
        card.add(buildMenuButton("Monitoraggio andamento sale", false, e -> mostraFunzioneProssimamente()));
        card.add(Box.createVerticalStrut(10));
        card.add(buildMenuButton("Visualizza storico prenotazioni", false, e -> mostraFunzioneProssimamente()));

        card.add(Box.createVerticalStrut(10));
        card.add(buildMenuButton("Logout", false, e -> {
            Sessione.getInstance().chiudiSessione();
            new FormLogin().setVisible(true);
            dispose();
        }));

        root.add(card);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    /** Crea un pulsante di menu con stile e listener dati, a larghezza piena. */
    private RoundedButton buildMenuButton(String testo, boolean filled,
                                          java.awt.event.ActionListener listener) {
        RoundedButton btn = new RoundedButton(testo, filled);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.addActionListener(listener);
        return btn;
    }

    /** Mostra un avviso per le funzionalità non ancora implementate. */
    private void mostraFunzioneProssimamente() {
        JOptionPane.showMessageDialog(this,
                "Questa funzione verrà aggiunta a breve...",
                "In arrivo",
                JOptionPane.INFORMATION_MESSAGE);
    }
}