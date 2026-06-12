package boundary;

import dto.FasciaOraria;
import control.GestoreSaleStudio;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

public class FormConsultaFasceOrarie extends JFrame {

    // ── Palette (protected così la sottoclasse può usarla) ────────────────────
    protected static final Color BG_PAGE        = new Color(0xF9F9F8);
    protected static final Color BG_CARD        = Color.WHITE;
    protected static final Color BORDER_LIGHT   = new Color(0xE5E5E3);
    protected static final Color BLUE_BG        = new Color(0xE6F1FB);
    protected static final Color BLUE_BORDER    = new Color(0x85B7EB);
    protected static final Color BLUE_TEXT      = new Color(0x0C447C);
    protected static final Color GREEN_BG       = new Color(0xEAF3DE);
    protected static final Color GREEN_TEXT     = new Color(0x3B6D11);
    protected static final Color AMBER_BG       = new Color(0xFAEEDA);
    protected static final Color AMBER_TEXT     = new Color(0x854F0B);
    protected static final Color RED_BG         = new Color(0xFCEBEB);
    protected static final Color RED_TEXT       = new Color(0xA32D2D);
    protected static final Color TEXT_PRIMARY   = new Color(0x1A1A18);
    protected static final Color TEXT_SECONDARY = new Color(0x6B6B68);
    protected static final Color TEXT_TERTIARY  = new Color(0x9B9B98);
    protected static final double SOGLIA_QUASI_PIENO = 0.35;

    // ── Stato (protected per consentire l'override di buildSlotRow) ───────────
    protected final GestoreSaleStudio gestore;
    protected JComboBox<String> comboSale;
    protected JPanel datePanelInner;
    protected JScrollPane dateScrollPane;
    protected JPanel fascePanelInner;
    protected JScrollPane fasceScrollPane;
    protected JLabel fasceHeaderLabel;
    protected JLabel hintLabel;
    protected JPanel root;
    protected JLabel labelTitolo;

    protected final List<DateButton> dateButtons = new ArrayList<>();
    protected LocalDate dataSelezionata = null;
    protected String nomeSala = null;

    // ═════════════════════════════════════════════════════════════════════════
    public FormConsultaFasceOrarie() {
        gestore = new GestoreSaleStudio();

        setTitle("Fasce orarie disponibili");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(660, 560);
        setLocationRelativeTo(null);
        setResizable(false);

        root = new JPanel();
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBackground(BG_PAGE);
        root.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Titolo
        labelTitolo = new JLabel("Consulta fasce orarie disponibili");
        labelTitolo.setFont(labelTitolo.getFont().deriveFont(Font.BOLD, 18f));
        labelTitolo.setForeground(TEXT_PRIMARY);
        labelTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(labelTitolo);
        root.add(Box.createVerticalStrut(20));

        // Bottone torna
        // root.add(Box.createVerticalStrut(8));
        RoundedButton btnTorna = new RoundedButton("← Torna al menu");
        btnTorna.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTorna.addActionListener(e -> { new FormStudente(); dispose(); });
        root.add(btnTorna);
        root.add(Box.createVerticalStrut(20));


        // Sala
        root.add(buildSectionLabel("Sala"));
        root.add(Box.createVerticalStrut(8));
        root.add(buildCombo());
        root.add(Box.createVerticalStrut(20));

        // Data
        root.add(buildSectionLabel("Data"));
        root.add(Box.createVerticalStrut(8));
        root.add(buildDateScrollPane());
        root.add(Box.createVerticalStrut(20));

        // Separatore
        root.add(buildDivider());
        root.add(Box.createVerticalStrut(20));

        // Fasce orarie
        fasceHeaderLabel = buildSectionLabel("Fasce orarie");
        fasceHeaderLabel.setVisible(false);
        root.add(fasceHeaderLabel);
        root.add(Box.createVerticalStrut(8));
        root.add(buildFasceScrollPane());

        // Hint iniziale
        hintLabel = new JLabel("Seleziona una sala per vedere le date disponibili.");
        hintLabel.setFont(hintLabel.getFont().deriveFont(13f));
        hintLabel.setForeground(TEXT_TERTIARY);
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(hintLabel);

        setContentPane(root);
        getContentPane().setBackground(BG_PAGE);
    }

    // ── Builder sezioni ───────────────────────────────────────────────────────

    private JComboBox<String> buildCombo() {
        comboSale = new JComboBox<>();
        comboSale.addItem("Seleziona una sala...");
        for (String nome : gestore.getNomiSale()) comboSale.addItem(nome);
        comboSale.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comboSale.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboSale.setFont(comboSale.getFont().deriveFont(13f));
        comboSale.setBackground(BG_CARD);
        comboSale.addActionListener(e -> onSalaSelezionata());
        return comboSale;
    }

    private JScrollPane buildDateScrollPane() {
        datePanelInner = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        datePanelInner.setOpaque(false);

        dateScrollPane = new JScrollPane(datePanelInner,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        dateScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        dateScrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 68));
        dateScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateScrollPane.setBorder(BorderFactory.createEmptyBorder());
        dateScrollPane.setOpaque(false);
        dateScrollPane.getViewport().setOpaque(false);
        dateScrollPane.setVisible(false);
        dateScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 3));
        return dateScrollPane;
    }

    private JScrollPane buildFasceScrollPane() {
        fascePanelInner = new JPanel();
        fascePanelInner.setLayout(new BoxLayout(fascePanelInner, BoxLayout.Y_AXIS));
        fascePanelInner.setOpaque(false);

        fasceScrollPane = new JScrollPane(fascePanelInner,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        fasceScrollPane.setBorder(BorderFactory.createEmptyBorder());
        fasceScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        fasceScrollPane.setOpaque(false);
        fasceScrollPane.getViewport().setOpaque(false);
        fasceScrollPane.setVisible(false);
        fasceScrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        fasceScrollPane.setPreferredSize(new Dimension(Integer.MAX_VALUE, 220));
        return fasceScrollPane;
    }

    // ── Logica eventi ─────────────────────────────────────────────────────────

    protected void onSalaSelezionata() {
        if (comboSale.getSelectedIndex() == 0) {
            dateScrollPane.setVisible(false);
            fasceScrollPane.setVisible(false);
            fasceHeaderLabel.setVisible(false);
            hintLabel.setVisible(true);
            revalidate(); repaint();
            return;
        }
        nomeSala = (String) comboSale.getSelectedItem();
        hintLabel.setVisible(false);
        buildDateButtons();
        dateScrollPane.setVisible(true);
        fasceScrollPane.setVisible(false);
        fasceHeaderLabel.setVisible(false);
        dataSelezionata = null;
        revalidate(); repaint();
    }

    protected void buildDateButtons() {
        datePanelInner.removeAll();
        dateButtons.clear();

        LocalDate oggi = LocalDate.now();
        for (int i = 0; i < 10; i++) {
            LocalDate data = oggi.plusDays(i);
            String giorno = data.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ITALIAN);
            DateButton btn = new DateButton(giorno, data.getDayOfMonth());

            LocalDate finalData = data;
            btn.addActionListener(e -> {
                dateButtons.forEach(b -> b.setSelected(false));
                btn.setSelected(true);
                dataSelezionata = finalData;
                aggiornaFasce();
            });

            dateButtons.add(btn);
            datePanelInner.add(btn);
        }

        datePanelInner.revalidate();
        datePanelInner.repaint();
    }

    protected void aggiornaFasce() {
        fascePanelInner.removeAll();

        Map<FasciaOraria, Integer> fasceDisponibili = new TreeMap<>(
                Comparator.comparing(FasciaOraria::getOraInizio)
        );
        fasceDisponibili.putAll(gestore.getFascieOrarieDisponibili(nomeSala, dataSelezionata));

        if (fasceDisponibili.isEmpty()) {
            JLabel vuoto = new JLabel("Nessuna fascia oraria disponibile per questa data.");
            vuoto.setFont(vuoto.getFont().deriveFont(13f));
            vuoto.setForeground(TEXT_TERTIARY);
            vuoto.setAlignmentX(Component.LEFT_ALIGNMENT);
            fascePanelInner.add(vuoto);
        } else {
            RoundedCard card = new RoundedCard();
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            card.setAlignmentX(Component.LEFT_ALIGNMENT);
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            List<Map.Entry<FasciaOraria, Integer>> entries = new ArrayList<>(fasceDisponibili.entrySet());
            for (int i = 0; i < entries.size(); i++) {
                Map.Entry<FasciaOraria, Integer> entry = entries.get(i);
                int totale = gestore.getNumPostazioniSala(nomeSala);
                card.add(buildSlotRow(entry.getKey(), entry.getValue(), totale));
                if (i < entries.size() - 1) card.add(buildInternalDivider());
            }

            fascePanelInner.add(card);
        }

        String giornoNome = dataSelezionata.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
        String mese = dataSelezionata.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
        fasceHeaderLabel.setText("Fasce orarie — " + giornoNome + " " + dataSelezionata.getDayOfMonth() + " " + mese);
        fasceHeaderLabel.setVisible(true);
        fasceScrollPane.setVisible(true);

        fascePanelInner.revalidate();
        fascePanelInner.repaint();
        revalidate();
        repaint();
    }

    // ── Componenti UI ─────────────────────────────────────────────────────────

    /** Protected: la sottoclasse fa override per aggiungere il bottone di selezione. */
    protected JPanel buildSlotRow(FasciaOraria fascia, int liberi, int totale) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 16, 12, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JLabel orario = new JLabel(fascia.toString());
        orario.setFont(orario.getFont().deriveFont(Font.PLAIN, 14f));
        orario.setForeground(TEXT_PRIMARY);
        row.add(orario, BorderLayout.WEST);

        JPanel destra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        destra.setOpaque(false);

        JLabel postiLabel = new JLabel(liberi + " / " + totale + " postazioni libere");
        postiLabel.setFont(postiLabel.getFont().deriveFont(12f));
        postiLabel.setForeground(TEXT_SECONDARY);
        destra.add(postiLabel);
        destra.add(buildBadgePill(liberi, totale));

        row.add(destra, BorderLayout.EAST);
        return row;
    }

    protected JLabel buildBadgePill(int liberi, int totale) {
        String testo;
        Color bg, fg;

        if (liberi == 0) {
            testo = "esaurito";    bg = RED_BG;   fg = RED_TEXT;
        } else if ((double) liberi / totale <= SOGLIA_QUASI_PIENO) {
            testo = "quasi pieno"; bg = AMBER_BG; fg = AMBER_TEXT;
        } else {
            testo = "disponibile"; bg = GREEN_BG; fg = GREEN_TEXT;
        }
        return new PillLabel(testo, bg, fg);
    }

    protected JSeparator buildInternalDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_LIGHT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    protected JSeparator buildDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_LIGHT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    protected JLabel buildSectionLabel(String testo) {
        JLabel lbl = new JLabel(testo.toUpperCase());
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 10.5f));
        lbl.setForeground(TEXT_TERTIARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // ═════════════════════════════════════════════════════════════════════════
    // Componenti custom (protected per riuso nella sottoclasse)
    // ═════════════════════════════════════════════════════════════════════════

    protected static class DateButton extends JButton {
        private boolean selected = false;
        private final String giorno;
        private final String numero;

        DateButton(String giorno, int numero) {
            super();
            this.giorno = giorno;
            this.numero = String.valueOf(numero);
            setPreferredSize(new Dimension(52, 60));
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
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (selected) {
                g2.setColor(BLUE_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(BLUE_BORDER);
                g2.setStroke(new BasicStroke(1f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
            } else {
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(BORDER_LIGHT);
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
            }

            Color colTertiary = selected ? new Color(0x185FA5) : TEXT_TERTIARY;
            Color colPrimary  = selected ? BLUE_TEXT : TEXT_PRIMARY;
            int cx = getWidth() / 2;

            Font fontSmall = getFont().deriveFont(Font.PLAIN, 10.5f);
            Font fontBig   = getFont().deriveFont(Font.BOLD, 16f);
            FontMetrics fmSmall = g2.getFontMetrics(fontSmall);
            FontMetrics fmBig   = g2.getFontMetrics(fontBig);

            int totalH = fmSmall.getAscent() + 3 + fmBig.getAscent();
            int startY = (getHeight() - totalH) / 2 + fmSmall.getAscent();

            g2.setFont(fontSmall);
            g2.setColor(colTertiary);
            g2.drawString(giorno, cx - fmSmall.stringWidth(giorno) / 2, startY);

            g2.setFont(fontBig);
            g2.setColor(colPrimary);
            g2.drawString(numero, cx - fmBig.stringWidth(numero) / 2, startY + 3 + fmBig.getAscent());

            g2.dispose();
        }
    }

    protected static class RoundedCard extends JPanel {
        RoundedCard() {
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 0, 0));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BG_CARD);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
            g2.setColor(BORDER_LIGHT);
            g2.setStroke(new BasicStroke(0.8f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 12, 12));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    protected static class PillLabel extends JLabel {
        private final Color bg;

        PillLabel(String testo, Color bg, Color fg) {
            super(testo);
            this.bg = bg;
            setForeground(fg);
            setFont(getFont().deriveFont(Font.PLAIN, 11f));
            setBorder(new EmptyBorder(3, 10, 3, 10));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g2.dispose();
            super.paintComponent(g);
        }
    }

    protected static class RoundedButton extends JButton {
        RoundedButton(String testo) {
            super(testo);
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(getFont().deriveFont(Font.PLAIN, 13f));
            setBorder(new EmptyBorder(7, 16, 7, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(BG_CARD);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
            g2.setColor(BORDER_LIGHT);
            g2.setStroke(new BasicStroke(0.8f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, getHeight(), getHeight()));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            new FormConsultaFasceOrarie().setVisible(true);
        });
    }
}