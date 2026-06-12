package boundary;

import control.Sessione;
import database.GestorePersistenza;
import dto.FasciaOraria;
import control.GestoreSaleStudio;
import control.GestorePrenotazioni;
import entity.Studente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.*;
import java.util.List;

public class FormEffettuaPrenotazione extends FormConsultaFasceOrarie {

    // ── Stato aggiuntivo ──────────────────────────────────────────────────────
    private final Set<FasciaOraria> fasceSelezionate = new LinkedHashSet<>();
    private JPanel areaSection;
    private JComboBox<String> comboArea;

    private Sessione session;

    // ═════════════════════════════════════════════════════════════════════════
    public FormEffettuaPrenotazione() {
        // Il costruttore padre costruisce già tutta la UI base.
        // Qui personalizziamo titolo, dimensione e aggiungiamo la sezione area.

        session = Sessione.getInstance();

        // DA CAMBIARE
        GestorePersistenza gestore = new GestorePersistenza();

        session.apriSessioneStudente(gestore.cercaPrimoPerCampi(Studente.class, Map.of("nome", "Matteo")));

        setTitle("Effettua prenotazione");
        setSize(660, 640);

        // Aggiorna il titolo visivo (il JLabel è già nel root, lo sostituiamo)
        // È più semplice aggiungere la sezione area in fondo al root esistente.
        areaSection = buildAreaSection();
        root.add(areaSection);

        // Aggiorna l'header fasce per indicare la selezione multipla
        fasceHeaderLabel.setText("FASCE ORARIE — SELEZIONA UNA O PIÙ");

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
        areaSection.setVisible(false);
        aggiornaComboAree();
        super.onSalaSelezionata();
    }

    @Override
    protected void buildDateButtons() {
        fasceSelezionate.clear();
        areaSection.setVisible(false);
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
        areaSection.setVisible(!fasceSelezionate.isEmpty());
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

        if(esito) {
            List<FasciaOraria> fasceList = new ArrayList<>(fasceSelezionate);
            FasciaOraria fasciaUnita = new FasciaOraria(fasceList.getFirst().getOraInizio(), fasceList.getLast().getOraFine());
            gestorePrenotazioni.effettuaPrenotazione(nomeSala, dataSelezionata, fasciaUnita, session.getStudenteCorrente(), areaSel);

            JOptionPane.showMessageDialog(this,
                    "Prenotazione effettuata per la fascia oraria " + fasciaUnita,
                    "Conferma", JOptionPane.INFORMATION_MESSAGE);

        } else {
            if (mostraDialogSeparazione()){
                for (FasciaOraria fascia : fasceSelezionate) {
                    gestorePrenotazioni.effettuaPrenotazione(nomeSala, dataSelezionata, fascia, session.getStudenteCorrente(), areaSel);
                }
            }
            JOptionPane.showMessageDialog(this,
                     fasceSelezionate.size() + " prenotazioni effettuate per la fasce orarie " + fasceSelezionate,
                    "Conferma", JOptionPane.INFORMATION_MESSAGE);
        }




        new FormStudente();
        dispose();
    }

    private boolean mostraDialogSeparazione() {
        // logica UI per chiedere all'utente
        return true; // placeholder
    }

    // ── Sezione area ──────────────────────────────────────────────────────────

    private JPanel buildAreaSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
        section.setVisible(false);

        section.add(Box.createVerticalStrut(16));
        section.add(buildDivider());
        section.add(Box.createVerticalStrut(16));

        section.add(buildSectionLabel("Area (opzionale)"));
        section.add(Box.createVerticalStrut(8));

        comboArea = new JComboBox<>();
        comboArea.addItem("Nessuna preferenza");
        comboArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comboArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboArea.setFont(comboArea.getFont().deriveFont(13f));
        comboArea.setBackground(BG_CARD);
        section.add(comboArea);
        section.add(Box.createVerticalStrut(16));

        RoundedButton btnPrenota = new RoundedButton("Prenota");
        btnPrenota.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnPrenota.setFont(btnPrenota.getFont().deriveFont(Font.BOLD, 13f));
        btnPrenota.addActionListener(e -> onPrenota());
        section.add(btnPrenota);

        return section;
    }

    private void aggiornaComboAree() {
        if (comboArea == null) return;
        comboArea.removeAllItems();
        comboArea.addItem("Nessuna preferenza");
        List<String> aree = gestore.getAreeSala(nomeSala);
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

    // ═════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new FormEffettuaPrenotazione().setVisible(true);
        });
    }
}