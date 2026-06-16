package boundary;

import control.GestoreSaleStudio;
import control.Sessione;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Form (boundary) per la creazione di una nuova sala studio da parte del
 * bibliotecario.
 * <p>
 * Raccoglie i dati della sala (nome, descrizione, postazioni, orari ed eventuali
 * aree con relative postazioni), valida l'input e delega la creazione a
 * {@link GestoreSaleStudio}. Non contiene logica di dominio né accede alla
 * persistenza. Eredita aspetto e componenti grafici da {@link BaseForm}.
 */
public class FormCreaSala extends BaseForm {

    // ── Campi form ────────────────────────────────────────────────────────────
    private JTextField nomeSala;
    private JTextField numPostazioniTotali;
    private JTextField descrizione;
    private JComboBox<String> comboApertura;
    private JComboBox<String> comboChiusura;
    private JCheckBox ckbPresenza;
    private JComboBox<String> comboTipologia;
    private JSpinner spinnerPostazioni;
    private JTable tabellaAree;
    private JPanel panelAggiungiArea;
    private JButton btnSalva;

    private DefaultTableModel tableModel;


    public FormCreaSala() {
        super();

        // ── Root ─────────────────────────────────────────────────────────────
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG_PAGE);
        root.setBorder(new EmptyBorder(24, 24, 24, 24));
        setContentPane(root);

        // ── Card con ScrollPane ───────────────────────────────────────────────
        RoundedCard card = new RoundedCard();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(28, 28, 28, 28));
        card.setMinimumSize(new Dimension(420, 100));

        // Titolo
        JLabel titolo = new JLabel("Crea nuova sala");
        titolo.setFont(FONT_BOLD.deriveFont(20f));
        titolo.setForeground(TEXT_PRIMARY);
        titolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(titolo);

        JLabel sottotitolo = new JLabel("Compila i campi per aggiungere una sala studio");
        sottotitolo.setFont(FONT_REGULAR.deriveFont(12f));
        sottotitolo.setForeground(TEXT_TERTIARY);
        sottotitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sottotitolo);
        card.add(Box.createVerticalStrut(24));

        // Nome
        card.add(buildSectionLabel("Nome sala"));
        card.add(Box.createVerticalStrut(6));
        nomeSala = buildTextField("es. Sala A");
        card.add(nomeSala);
        card.add(Box.createVerticalStrut(12));

        // Descrizione
        card.add(buildSectionLabel("Descrizione"));
        card.add(Box.createVerticalStrut(6));
        descrizione = buildTextField("es. Sala silenziosa al primo piano");
        card.add(descrizione);
        card.add(Box.createVerticalStrut(12));

        // Numero postazioni totali
        card.add(buildSectionLabel("Numero postazioni totali"));
        card.add(Box.createVerticalStrut(6));
        numPostazioniTotali = buildTextField("es. 20");
        card.add(numPostazioniTotali);
        card.add(Box.createVerticalStrut(12));

        // Orario apertura
        card.add(buildSectionLabel("Orario apertura"));
        card.add(Box.createVerticalStrut(6));
        comboApertura = buildComboOrari();
        card.add(comboApertura);
        card.add(Box.createVerticalStrut(12));

        // Orario chiusura
        card.add(buildSectionLabel("Orario chiusura"));
        card.add(Box.createVerticalStrut(6));
        comboChiusura = buildComboOrari();
        comboChiusura.setSelectedIndex(Math.min(8, comboChiusura.getItemCount() - 1));
        card.add(comboChiusura);
        card.add(Box.createVerticalStrut(16));

        // Checkbox aree
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(16));

        ckbPresenza = new JCheckBox("La sala è suddivisa in aree?");
        ckbPresenza.setFont(FONT_REGULAR);
        ckbPresenza.setForeground(TEXT_PRIMARY);
        ckbPresenza.setOpaque(false);
        ckbPresenza.setAlignmentX(Component.LEFT_ALIGNMENT);
        ckbPresenza.addActionListener(e -> {
                panelAggiungiArea.setVisible(ckbPresenza.isSelected());
                card.revalidate();
                card.repaint();
        });
        card.add(ckbPresenza);
        card.add(Box.createVerticalStrut(12));

        // Pannello aree (visibile solo se checkbox selezionata)
        panelAggiungiArea = buildPanelAree();
        panelAggiungiArea.setVisible(false);
        card.add(panelAggiungiArea);
        card.add(Box.createVerticalStrut(24));

        // Bottoni
        card.add(buildDivider());
        card.add(Box.createVerticalStrut(16));

        btnSalva = new RoundedButton("Salva sala", true);
        btnSalva.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnSalva.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnSalva.addActionListener(e -> salvaDati());
        card.add(btnSalva);
        card.add(Box.createVerticalStrut(10));

        RoundedButton btnAnnulla = new RoundedButton("Annulla", false);
        btnAnnulla.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnAnnulla.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btnAnnulla.addActionListener(e -> { new FormBibliotecario().setVisible(true); dispose(); });
        card.add(btnAnnulla);

        // ScrollPane sulla card
        JScrollPane scrollPane = new JScrollPane(card,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(480, 620));

        JScrollBar vsb = scrollPane.getVerticalScrollBar();
        vsb.setPreferredSize(new Dimension(6, 0));
        vsb.setUI(new SlimScrollBarUI());

        root.add(scrollPane);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ── Pannello aree ─────────────────────────────────────────────────────────

    /** Costruisce il pannello di gestione aree (aggiunta, tabella e rimozione righe). */
    private JPanel buildPanelAree() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Riga aggiunta area
        panel.add(buildSectionLabel("Aggiungi area"));
        panel.add(Box.createVerticalStrut(8));

        JPanel rigaAdd = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        rigaAdd.setOpaque(false);
        rigaAdd.setAlignmentX(Component.LEFT_ALIGNMENT);

        comboTipologia = new JComboBox<>(new String[]{"Silenzio", "Gruppo", "Consultazione"});
        comboTipologia.setFont(FONT_REGULAR);
        comboTipologia.setBackground(BG_CARD);
        comboTipologia.setPreferredSize(new Dimension(160, 34));

        spinnerPostazioni = new JSpinner(new SpinnerNumberModel(1, 0, 999, 1));
        spinnerPostazioni.setFont(FONT_REGULAR);
        spinnerPostazioni.setPreferredSize(new Dimension(70, 34));

        RoundedButton btnAdd = new RoundedButton("+ Aggiungi", false);
        btnAdd.setPreferredSize(new Dimension(110, 34));
        btnAdd.addActionListener(e -> {
            String tipologia  = (String) comboTipologia.getSelectedItem();
            int    postazioni = (Integer) spinnerPostazioni.getValue();
            tableModel.addRow(new Object[]{tipologia, postazioni, "-"});
        });

        rigaAdd.add(comboTipologia);
        rigaAdd.add(spinnerPostazioni);
        rigaAdd.add(btnAdd);
        panel.add(rigaAdd);
        panel.add(Box.createVerticalStrut(12));

        // Tabella aree
        tableModel = new DefaultTableModel(new String[]{"Tipologia", "Postazioni", ""}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };

        tabellaAree = new JTable(tableModel);
        tabellaAree.setFont(FONT_REGULAR);
        tabellaAree.setRowHeight(32);
        tabellaAree.setShowGrid(false);
        tabellaAree.setIntercellSpacing(new Dimension(0, 0));
        tabellaAree.setBackground(BG_CARD);
        tabellaAree.setForeground(TEXT_PRIMARY);
        tabellaAree.getTableHeader().setFont(FONT_REGULAR.deriveFont(11f));
        tabellaAree.getTableHeader().setForeground(TEXT_TERTIARY);
        tabellaAree.getTableHeader().setBackground(BG_CARD);
        tabellaAree.getColumnModel().getColumn(2).setMaxWidth(40);

        // Renderer bottone rimozione
        tabellaAree.getColumnModel().getColumn(2).setCellRenderer(
                (TableCellRenderer) (table, value, isSelected, hasFocus, row, col) -> {
                    JButton btn = new JButton("−");
                    btn.setFont(FONT_REGULAR);
                    btn.setForeground(RED_TEXT);
                    btn.setBackground(BG_CARD);
                    btn.setBorderPainted(false);
                    return btn;
                }
        );

        // Editor bottone rimozione
        tabellaAree.getColumnModel().getColumn(2).setCellEditor(
                new DefaultCellEditor(new JCheckBox()) {
                    private final JButton btn = new JButton("−");
                    private int editRow;
                    {
                        btn.setFont(FONT_REGULAR);
                        btn.setForeground(RED_TEXT);
                        btn.setBackground(BG_CARD);
                        btn.setBorderPainted(false);
                        btn.addActionListener(ev -> tableModel.removeRow(editRow));
                    }
                    @Override
                    public Component getTableCellEditorComponent(JTable table, Object value,
                                                                 boolean isSelected, int r, int col) {
                        this.editRow = r;
                        return btn;
                    }
                    @Override
                    public Object getCellEditorValue() { return "−"; }
                }
        );

        JScrollPane tabellaScroll = new JScrollPane(tabellaAree);
        tabellaScroll.setBorder(BorderFactory.createLineBorder(BORDER_LIGHT, 1));
        tabellaScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        tabellaScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        tabellaScroll.setPreferredSize(new Dimension(Integer.MAX_VALUE, 160));

        panel.add(tabellaScroll);
        return panel;
    }

    // ── Logica (invariata) ────────────────────────────────────────────────────

    /**
     * Valida i dati inseriti e, se corretti, crea la sala studio.
     * <p>
     * Esegue i controlli di formato lato boundary: numero di postazioni intero e
     * positivo, nome e descrizione non vuoti, postazioni per area non negative e
     * somma delle postazioni delle aree non superiore al totale della sala. Se
     * tutti i controlli passano, delega la creazione a
     * {@link GestoreSaleStudio#aggiungiSalaStudio}. Al termine torna al menu
     * bibliotecario.
     */
    public void salvaDati() {
        String nome        = nomeSala.getText();
        String desc        = descrizione.getText();
        int    numPost     = 0;
        LocalTime apertura = LocalTime.parse((String) comboApertura.getSelectedItem());
        LocalTime chiusura = LocalTime.parse((String) comboChiusura.getSelectedItem());
        boolean presenzaAree = ckbPresenza.isSelected();

        boolean sblocco = true;

        List<String>  col1 = new ArrayList<>();
        List<Integer> col2 = new ArrayList<>();
        if (presenzaAree) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                col1.add((String)  tableModel.getValueAt(i, 0));
                col2.add((Integer) tableModel.getValueAt(i, 1));
            }
        }

        try {
            numPost = Integer.parseInt(numPostazioniTotali.getText());
            if (numPost <= 0) throw new IllegalArgumentException("Il numero deve essere maggiore di zero");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Il campo delle postazioni deve contenere un numero intero positivo");
            sblocco = false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            sblocco = false;
        }

        if (nome.isEmpty())  { JOptionPane.showMessageDialog(null, "Il nome non può essere vuoto");  sblocco = false; }
        if (desc.isEmpty())  { JOptionPane.showMessageDialog(null, "Inserire una descrizione");       sblocco = false; }
        if ((Integer) spinnerPostazioni.getValue() < 0) {
            JOptionPane.showMessageDialog(null, "Il numero di postazioni per area non può essere negativo");
            sblocco = false;
        }
        if (col1.size() != col2.size()) sblocco = false;

        int somma = col2.stream().mapToInt(Integer::intValue).sum();
        if (somma > numPost) {
            JOptionPane.showMessageDialog(null, "Le postazioni delle aree non possono superare quelle totali");
            sblocco = false;
        }

        if (sblocco) {
            boolean esito = GestoreSaleStudio.aggiungiSalaStudio(
                    nome, desc, numPost, apertura, chiusura, presenzaAree, col1, col2);
            if (esito) JOptionPane.showMessageDialog(null, "Sala creata correttamente");
            else       JOptionPane.showMessageDialog(null, "Sala non creata correttamente");
        } else {
            JOptionPane.showMessageDialog(null, "Sala non creata correttamente");
        }

        new FormBibliotecario().setVisible(true);
        dispose();
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /** Crea una combo di orari a intervalli di 30 minuti dalle 06:00 alle 22:30. */
    private JComboBox<String> buildComboOrari() {
        JComboBox<String> combo = new JComboBox<>();
        for (int h = 6; h <= 22; h++) {
            combo.addItem(String.format("%02d:00", h));
            combo.addItem(String.format("%02d:30", h));
        }
        combo.setFont(FONT_REGULAR);
        combo.setBackground(BG_CARD);
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
        return combo;
    }
}