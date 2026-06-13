package boundary;

import control.Sessione;
import dto.FasciaOraria;
import control.GestorePrenotazioni;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class FormEffettuaPrenotazione extends FormConsultaFasceOrarie {

    // ── Stato aggiuntivo ──────────────────────────────────────────────────────
    private final Set<FasciaOraria> fasceSelezionate = new LinkedHashSet<>();
    private JPanel controlPanel;
    private JPanel prenotaSection;
    private RoundedButton btnPrenota;

    // ═════════════════════════════════════════════════════════════════════════
    public FormEffettuaPrenotazione() {

        super();
        // Il costruttore padre costruisce già tutta la UI base.
        // Qui personalizziamo titolo, dimensione e aggiungiamo la sezione area.
        setTitle("Effettua prenotazione");
        setSize(660, 640);

        // Aggiorna l'header fasce per indicare la selezione multipla
        fasceHeaderLabel.setText("FASCE ORARIE — SELEZIONA UNA O PIÙ");

        // Aggiungo il bottone Prenota
        panelConsultaFasceOrarie.add(Box.createVerticalStrut(10));
        panelConsultaFasceOrarie.add(buildPrenotaButton());

        labelTitolo.setText("Effettua prenotazione");

        revalidate();
        repaint();
    }

    // ── Override: aggiunge SlotButton a ogni riga ─────────────────────────────

    @Override
    protected JPanel buildSlotRow(FasciaOraria fascia, int liberi, int totale) {
        // Prende la riga base dal genitore
        JPanel row = super.buildSlotRow(fascia, liberi, totale);
        dateScrollPane.setMinimumSize(new Dimension(0, 68));
        dateScrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 68));

        // Aggiunge il checkbox solo se ci sono posti disponibili
        if (liberi > 0) {
            JPanel destra = (JPanel) row.getComponent(1); // BorderLayout.EAST
            SlotButton slotBtn = new SlotButton();
            slotBtn.addActionListener(e -> onFasciaToggle(fascia, slotBtn));
            destra.add(slotBtn);
        }

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        return row;
    }

    // ── Logica selezione fasce ────────────────────────────────────────────────

    @Override
    protected void onSalaSelezionata() {
        fasceSelezionate.clear();
        super.onSalaSelezionata();
        aggiornaComboAree();
    }

    @Override
    protected void buildDateButtons() {
        fasceSelezionate.clear();
        super.buildDateButtons();
    }

    private void onFasciaToggle(FasciaOraria fascia, SlotButton slotBtn) {
        if (fasceSelezionate.contains(fascia)) {
            fasceSelezionate.remove(fascia);
            slotBtn.setSelected(false);
        } else {
            fasceSelezionate.add(fascia);
            slotBtn.setSelected(true);
        }
        prenotaSection.setVisible(!fasceSelezionate.isEmpty());
        revalidate();
        repaint();
    }

    private void onPrenota() {
        if (fasceSelezionate.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleziona almeno una fascia oraria.",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String areaSel = comboArea.getSelectedIndex() == 0
                ? null
                : (String) comboArea.getSelectedItem();

        GestorePrenotazioni gestorePrenotazioni = new GestorePrenotazioni();

        boolean esito = gestorePrenotazioni.isPrenotazioneUnicaPossibile(nomeSala, fasceSelezionate, dataSelezionata, areaSel);

        if (esito) {
            List<FasciaOraria> fasceList = new ArrayList<>(fasceSelezionate);
            FasciaOraria fasciaUnita = new FasciaOraria(fasceList.getFirst().getOraInizio(), fasceList.getLast().getOraFine());
            if (gestorePrenotazioni.effettuaPrenotazione(nomeSala, dataSelezionata, fasciaUnita, Sessione.getInstance().getStudenteCorrente(), areaSel)) {
                JOptionPane.showMessageDialog(this,
                        "Prenotazione effettuata per la fascia oraria " + fasciaUnita,
                        "Conferma", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Prenotazione non riuscita",
                        "Continua...", JOptionPane.INFORMATION_MESSAGE);
            }
            new FormStudente();
            dispose();

        } else {
            if (mostraDialogSeparazione()){
                for (FasciaOraria fascia : fasceSelezionate) {
                    gestorePrenotazioni.effettuaPrenotazione(nomeSala, dataSelezionata, fascia, Sessione.getInstance().getStudenteCorrente(), areaSel);
                }
                JOptionPane.showMessageDialog(this,
                        fasceSelezionate.size() + " prenotazioni effettuate per la fasce orarie " + fasceSelezionate,
                        "Conferma", JOptionPane.INFORMATION_MESSAGE);
            }
            new FormStudente();
            dispose();
        }
    }


    private boolean mostraDialogSeparazione() {
        Object[] opzioni = {"Sì, prenota separatamente", "No, annulla"};
        int scelta = JOptionPane.showOptionDialog(
                this,
                "Le fasce selezionate non possono essere unite in un'unica prenotazione.\n" +
                        "Vuoi effettuare prenotazioni separate per ogni fascia?",
                "Conferma prenotazioni separate",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opzioni,
                opzioni[0]
        );
        return scelta == 0; // 0 = primo bottone = "Sì"
    }

    // ── Sezione area ──────────────────────────────────────────────────────────

    private JPanel buildPrenotaButton() {

        prenotaSection = new JPanel();
        prenotaSection.setLayout(new BoxLayout(prenotaSection, BoxLayout.Y_AXIS));
        prenotaSection.setOpaque(false);
        prenotaSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        prenotaSection.setVisible(false);

        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        btnPrenota = new RoundedButton("Prenota", true);
        btnPrenota.setFont(FONT_BOLD.deriveFont(13f));
        btnPrenota.setMaximumSize(btnPrenota.getPreferredSize());
        btnPrenota.addActionListener(e -> onPrenota());

        row.add(Box.createHorizontalGlue());
        row.add(btnPrenota);

        prenotaSection.add(row);
        return prenotaSection;
    }

    private void aggiornaComboAree() {
        if (comboArea == null) return;
        comboArea.removeAllItems();
        comboArea.addItem("Nessuna preferenza");
        List<String> aree = gestoreSaleStudio.getAreeSala(nomeSala);
        for (String area : aree) comboArea.addItem(area);
    }

    // ── SlotButton (checkbox custom) ──────────────────────────────────────────

    private static class SlotButton extends JButton {
        private boolean selected = false;

        SlotButton() {
            super();
            setPreferredSize(new Dimension(32, 32));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        public void setSelected(boolean sel) {
            this.selected = sel;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 4;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            if (selected) {
                g2.setColor(BLUE_BG);
                g2.fillRoundRect(x, y, size, size, 8, 8);
                g2.setColor(BLUE_BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(x, y, size, size, 8, 8);
                // Segno di spunta
                g2.setColor(BLUE_TEXT);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int cx = x + size / 2;
                int cy = y + size / 2;
                g2.drawLine(cx - 5, cy, cx - 1, cy + 4);
                g2.drawLine(cx - 1, cy + 4, cx + 5, cy - 4);
            } else {
                g2.setColor(BG_CARD);
                g2.fillRoundRect(x, y, size, size, 8, 8);
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(x, y, size, size, 8, 8);
            }

            g2.dispose();
        }
    }
}