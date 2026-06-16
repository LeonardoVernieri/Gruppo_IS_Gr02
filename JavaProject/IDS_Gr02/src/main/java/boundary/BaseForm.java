package boundary;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Classe base astratta dei form Swing dell'applicazione.
 * <p>
 * Raccoglie la palette, i font e i componenti grafici custom condivisi dalle
 * interfacce, così che i form concreti ne ereditino aspetto e helper comuni.
 * Contiene esclusivamente logica di presentazione: non accede a control,
 * entità o persistenza.
 */

public abstract class BaseForm extends JFrame {

    // ── Palette ───────────────────────────────────────────────────────────────
    protected static final Color BG_PAGE         = new Color(0xF7F7F6);
    protected static final Color BG_CARD         = Color.WHITE;
    protected static final Color BORDER_LIGHT    = new Color(0xEBEBEA);
    protected static final Color BLUE_BG         = new Color(0xEEF2FF);
    protected static final Color BLUE_BORDER     = new Color(0xA5B4FC);
    protected static final Color BLUE_TEXT       = new Color(0x4338CA);
    protected static final Color GREEN_BG        = new Color(0xECFDF5);
    protected static final Color GREEN_TEXT      = new Color(0x065F46);
    protected static final Color AMBER_BG        = new Color(0xFEF3C7);
    protected static final Color AMBER_TEXT      = new Color(0x92400E);
    protected static final Color RED_BG          = new Color(0xFEE2E2);
    protected static final Color RED_TEXT        = new Color(0x991B1B);
    protected static final Color TEXT_PRIMARY    = new Color(0x111110);
    protected static final Color TEXT_SECONDARY  = new Color(0x52525B);
    protected static final Color TEXT_TERTIARY   = new Color(0xA1A1AA);
    protected static final double SOGLIA_QUASI_PIENO = 0.35;

    // ── Font ──────────────────────────────────────────────────────────────────
    protected static final Font FONT_REGULAR;
    protected static final Font FONT_BOLD;

    /*
     * Carica i font Inter dalle risorse e li registra nell'ambiente grafico.
     * Se il caricamento fallisce (risorsa assente o illeggibile), ricade su un
     * font di sistema, così che la UI resti utilizzabile in ogni caso.
     */

    static {
        Font regular, bold;
        try {
            regular = Font.createFont(Font.TRUETYPE_FONT,
                            BaseForm.class.getResourceAsStream("/resources/Inter-Regular.ttf"))
                    .deriveFont(13f);
            bold = Font.createFont(Font.TRUETYPE_FONT,
                            BaseForm.class.getResourceAsStream("/resources/Inter-Bold.ttf"))
                    .deriveFont(Font.BOLD, 13f);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(regular);
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(bold);
        } catch (Exception e) {
            regular = new Font("Segoe UI", Font.PLAIN, 13);
            bold    = new Font("Segoe UI", Font.BOLD,  13);
        }
        FONT_REGULAR = regular;
        FONT_BOLD    = bold;
    }

    // ── Costruttore ───────────────────────────────────────────────────────────
    protected BaseForm() {

        java.net.URL urlIcona = getClass().getResource("/icons/logo.png");
        if (urlIcona != null) {
            setIconImage(new ImageIcon(urlIcona).getImage());
        }

        setTitle("Gestore sale studio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(BG_PAGE);
    }

        // ── Helper UI ─────────────────────────────────────────────────────────────

    protected JLabel buildSectionLabel(String testo) {
        JLabel lbl = new JLabel(testo.toUpperCase());
        lbl.setFont(FONT_REGULAR.deriveFont(10.5f));
        lbl.setForeground(TEXT_TERTIARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    protected JSeparator buildDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(BORDER_LIGHT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    protected JSeparator buildInternalDivider() {
        return buildDivider();
    }

    protected JTextField buildTextField(String placeholder) {
        JTextField tf = new JTextField();
        tf.setToolTipText(placeholder);
        styleTextField(tf);
        return tf;
    }

    protected void styleTextField(JTextField tf) {
        tf.setFont(FONT_REGULAR);
        tf.setForeground(TEXT_PRIMARY);
        tf.setBackground(BG_CARD);
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xCBCBCA), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        tf.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    /**
     * Costruisce l'etichetta di stato (pill) che riassume visivamente la
     * disponibilità di una sala o area in base al rapporto liberi/totale:
     * "esaurito" se non vi sono posti liberi, "quasi pieno" sotto la soglia
     * {@link #SOGLIA_QUASI_PIENO}, "disponibile" altrimenti.
     *
     * @param liberi posti attualmente liberi
     * @param totale posti complessivi
     * @return la pill colorata corrispondente allo stato di disponibilità
     */
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

    // ── Componenti custom ─────────────────────────────────────────────────────

    protected static class RoundedButton extends JButton {
        private boolean hovered = false;
        private final boolean filled;

        protected RoundedButton(String testo) {
            this(testo, false);
        }

        protected RoundedButton(String testo, boolean filled) {
            super(testo);
            this.filled = filled;
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(FONT_REGULAR);
            setBorder(new EmptyBorder(7, 16, 7, 16));
            if (filled) setForeground(Color.WHITE);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited (java.awt.event.MouseEvent e) { hovered = false; repaint(); }
            });
        }

        protected boolean isHovered() { return hovered; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color sfondo, bordo;
            if (filled) {
                sfondo = hovered ? new Color(0x4F46E5) : new Color(0x6366F1);
                bordo  = sfondo;
            } else {
                sfondo = hovered ? new Color(0xEEF2FF) : BG_CARD;
                bordo  = hovered ? new Color(0x6366F1) : new Color(0xCBCBCA);
            }
            g2.setColor(sfondo);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), getHeight(), getHeight()));
            g2.setColor(bordo);
            g2.setStroke(new BasicStroke(0.8f));
            g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, getHeight(), getHeight()));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    protected static class RoundedCard extends JPanel implements Scrollable {
        protected RoundedCard() {
            setOpaque(false);
            setBorder(new EmptyBorder(0, 0, 0, 0));
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle r, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle r, int orientation, int direction) {
            return 64;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true; // ← la card si adatta alla larghezza dello scrollPane
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false; // ← l'altezza cresce liberamente
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

        protected PillLabel(String testo, Color bg, Color fg) {
            super(testo);
            this.bg = bg;
            setForeground(fg);
            setFont(FONT_REGULAR.deriveFont(11f));
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

    protected static class DateButton extends JButton {
        private boolean selected = false;
        private boolean hovered  = false;
        private final String giorno;
        private final String numero;

        protected DateButton(String giorno, int numero) {
            super();
            this.giorno = giorno;
            this.numero = String.valueOf(numero);
            setPreferredSize(new Dimension(52, 60));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(FONT_REGULAR);

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true;  repaint(); }
                @Override public void mouseExited (java.awt.event.MouseEvent e) { hovered = false; repaint(); }
            });
        }

        public void setSelected(boolean sel) { this.selected = sel; repaint(); }

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
                Color sfondo = hovered ? new Color(0xEEF2FF) : BG_CARD;
                Color bordo  = hovered ? new Color(0x6366F1) : BORDER_LIGHT;
                g2.setColor(sfondo);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(bordo);
                g2.setStroke(new BasicStroke(0.8f));
                g2.draw(new RoundRectangle2D.Float(0.5f, 0.5f, getWidth() - 1, getHeight() - 1, 10, 10));
            }

            Color colTertiary = selected ? new Color(0x185FA5) : TEXT_TERTIARY;
            Color colPrimary  = selected ? BLUE_TEXT : TEXT_PRIMARY;
            int cx = getWidth() / 2;

            Font fontSmall = FONT_REGULAR.deriveFont(10.5f);
            Font fontBig   = FONT_BOLD.deriveFont(16f);
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

    protected static class SlimScrollBarUI extends BasicScrollBarUI {

        @Override
        protected void configureScrollBarColors() {
            thumbColor           = new Color(0x6366F1);
            thumbDarkShadowColor = new Color(0x6366F1);
            thumbHighlightColor  = new Color(0x6366F1);
            trackColor           = new Color(0xF0F0EF);
        }

        @Override
        protected JButton createDecreaseButton(int orientation) { return invisibleButton(); }

        @Override
        protected JButton createIncreaseButton(int orientation) { return invisibleButton(); }

        private JButton invisibleButton() {
            JButton btn = new JButton();
            btn.setPreferredSize(new Dimension(0, 0));
            btn.setMinimumSize(new Dimension(0, 0));
            btn.setMaximumSize(new Dimension(0, 0));
            return btn;
        }

        @Override
        protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
            if (thumbBounds.isEmpty()) return;
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(thumbColor);
            g2.fillRoundRect(thumbBounds.x + 2, thumbBounds.y + 2,
                    thumbBounds.width - 4, thumbBounds.height - 4, 8, 8);
            g2.dispose();
        }

        @Override
        protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(trackColor);
            g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
            g2.dispose();
        }
    }
}