package boundary;

import control.Sessione;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FormStudente extends BaseForm {

    public FormStudente() {
        super("Studente", 420, 500);

        // ── Root ─────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_PAGE);
        setContentPane(root);

        // ── Card ─────────────────────────────────────────────────────────────
        RoundedCard card = new RoundedCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(32, 32, 32, 32));
        card.setPreferredSize(new Dimension(340, 420));

        // Titolo
        JLabel titolo = new JLabel("Benvenuto " + Sessione.getInstance().getStudenteCorrente().getNome());
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

        // Bottoni azioni
        card.add(buildMenuButton("Effettua prenotazione", true,  e -> {
            new FormEffettuaPrenotazione().setVisible(true);
            dispose();
        }));
        card.add(Box.createVerticalStrut(10));

        card.add(buildMenuButton("Annulla prenotazione", false, e -> {
            mostraFunzioneProssimamente();
        }));
        card.add(Box.createVerticalStrut(10));

        card.add(buildMenuButton("Effettua Check-In", false, e -> {
            new FormCheckIn();
            setVisible(false);
        }));
        card.add(Box.createVerticalStrut(10));

        card.add(buildMenuButton("Consulta fasce orarie disponibili", false, e -> {
            new FormConsultaFasceOrarie().setVisible(true);
            dispose();
        }));
        card.add(Box.createVerticalStrut(10));

        card.add(buildMenuButton("Profilo personale", false, e -> {
            mostraFunzioneProssimamente();
        }));

        root.add(card);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private RoundedButton buildMenuButton(String testo, boolean filled,
                                          java.awt.event.ActionListener listener) {
        RoundedButton btn = new RoundedButton(testo, filled);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.addActionListener(listener);
        return btn;
    }

    private void mostraFunzioneProssimamente() {
        JOptionPane.showMessageDialog(this,
                "Questa funzione verrà aggiunta a breve...",
                "In arrivo",
                JOptionPane.INFORMATION_MESSAGE);
    }
}