package boundary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FormSplash extends BaseForm {

    public FormSplash() {
        super("Benvenuto");

        // ── Root ─────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_PAGE);
        setContentPane(root);

        // ── Card ─────────────────────────────────────────────────────────────
        RoundedCard card = new RoundedCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(48, 48, 48, 48));
        card.setPreferredSize(new Dimension(380, 300));

        // Icona / emoji
        JLabel icona = new JLabel();
        icona.setHorizontalAlignment(SwingConstants.CENTER);
        icona.setAlignmentX(Component.CENTER_ALIGNMENT);

        java.net.URL urlIcona = getClass().getResource("/icons/logo.png");
        if (urlIcona != null) {
            ImageIcon iconaRaw = new ImageIcon(urlIcona);
            Image scaled = iconaRaw.getImage()
                    .getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            icona.setIcon(new ImageIcon(scaled));
        } else {
            // fallback se la risorsa non viene trovata
            icona.setText("📚");
            icona.setFont(icona.getFont().deriveFont(48f));
        }
        card.add(icona);
        card.add(Box.createVerticalStrut(20));

        // Titolo
        JLabel titolo = new JLabel("Sale Studio", SwingConstants.CENTER);
        titolo.setFont(FONT_BOLD.deriveFont(26f));
        titolo.setForeground(TEXT_PRIMARY);
        titolo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titolo);
        card.add(Box.createVerticalStrut(8));

        // Sottotitolo
        JLabel sottotitolo = new JLabel("Sistema di prenotazione", SwingConstants.CENTER);
        sottotitolo.setFont(FONT_REGULAR.deriveFont(14f));
        sottotitolo.setForeground(TEXT_SECONDARY);
        sottotitolo.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(sottotitolo);
        card.add(Box.createVerticalStrut(32));

        // Barra di caricamento
        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 4));
        progressBar.setBorderPainted(false);
        progressBar.setForeground(new Color(0x6366F1));
        progressBar.setBackground(BORDER_LIGHT);
        card.add(progressBar);
        card.add(Box.createVerticalStrut(12));

        // Label stato
        JLabel lblStato = new JLabel("Avvio in corso...", SwingConstants.CENTER);
        lblStato.setFont(FONT_REGULAR.deriveFont(11f));
        lblStato.setForeground(TEXT_TERTIARY);
        lblStato.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(lblStato);

        root.add(card);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // ── Timer: anima la progress bar e poi apre il login ─────────────────
        Timer timer = new Timer(20, null);
        timer.addActionListener(e -> {
            int val = progressBar.getValue() + 1;
            progressBar.setValue(val);

            if (val >= 30)  lblStato.setText("Connessione al database...");
            if (val >= 70)  lblStato.setText("Quasi pronto...");
            if (val >= 100) {
                timer.stop();
                dispose();
                new FormLogin().setVisible(true);
            }
        });
        timer.start();
    }
}