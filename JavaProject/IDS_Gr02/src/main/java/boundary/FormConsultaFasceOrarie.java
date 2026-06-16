package boundary;

import control.GestoreSaleStudio;
import dto.FasciaOraria;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;
import java.util.List;

/**
 * Form (boundary) per la consultazione delle fasce orarie disponibili di una
 * sala studio in una data, con eventuale filtro per area.
 * <p>
 * Realizza il caso d'uso di visualizzazione della disponibilità: lo studente
 * sceglie sala, giorno ed eventuale area, e il form mostra per ogni fascia il
 * numero di postazioni libere. Delega ogni dato a {@link GestoreSaleStudio};
 * non contiene logica di dominio né accede alla persistenza. Eredita aspetto e
 * componenti grafici da {@link BaseForm} ed è pensato per essere esteso (es.
 * {@code FormEffettuaPrenotazione}) tramite override di
 * {@link #buildSlotRow(FasciaOraria, int, int)}.
 */
public class FormConsultaFasceOrarie extends BaseForm {

    // ── Stato (protected per consentire l'override di buildSlotRow) ───────────
    protected final GestoreSaleStudio gestoreSaleStudio;
    protected JComboBox<String> comboSale;
    protected JPanel datePanelInner;
    protected JScrollPane dateScrollPane;
    protected JPanel fascePanelInner;
    protected JScrollPane fasceScrollPane;
    protected JLabel fasceHeaderLabel;
    protected JLabel hintLabel;
    protected JPanel panelConsultaFasceOrarie;
    protected JLabel labelTitolo;
    protected JComboBox<String> comboArea;
    protected JPanel areaSection;

    protected final List<DateButton> dateButtons = new ArrayList<>();
    protected LocalDate dataSelezionata = null;
    protected String nomeSala = null;

    /** Costruisce il form, istanzia il controller e monta i pannelli (sala, data, area, fasce). */
    public FormConsultaFasceOrarie() {
        super();
        gestoreSaleStudio = new GestoreSaleStudio();

        setSize(660, 700);
        setLocationRelativeTo(null);

        panelConsultaFasceOrarie = new JPanel();
        panelConsultaFasceOrarie.setLayout(new BoxLayout(panelConsultaFasceOrarie, BoxLayout.Y_AXIS));
        panelConsultaFasceOrarie.setBackground(BG_PAGE);
        panelConsultaFasceOrarie.setBorder(new EmptyBorder(24, 28, 24, 28));

        // Titolo
        labelTitolo = new JLabel("Consulta fasce orarie disponibili");
        labelTitolo.setFont(FONT_BOLD.deriveFont(18f));
        labelTitolo.setForeground(TEXT_PRIMARY);
        labelTitolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelConsultaFasceOrarie.add(labelTitolo);
        panelConsultaFasceOrarie.add(Box.createVerticalStrut(20));

        // Bottone torna
        RoundedButton btnTorna = new RoundedButton("← Torna al menu", true);
        btnTorna.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnTorna.addActionListener(e -> { new FormStudente(); dispose(); });
        panelConsultaFasceOrarie.add(btnTorna);
        panelConsultaFasceOrarie.add(Box.createVerticalStrut(20));

        // Sala
        panelConsultaFasceOrarie.add(buildSectionLabel("Sala"));
        panelConsultaFasceOrarie.add(Box.createVerticalStrut(8));
        panelConsultaFasceOrarie.add(buildComboSala());
        panelConsultaFasceOrarie.add(Box.createVerticalStrut(20));

        // Data
        panelConsultaFasceOrarie.add(buildSectionLabel("Data"));
        panelConsultaFasceOrarie.add(Box.createVerticalStrut(8));
        panelConsultaFasceOrarie.add(buildDateScrollPane());
        panelConsultaFasceOrarie.add(Box.createVerticalStrut(20));

        // Separatore
        panelConsultaFasceOrarie.add(buildDivider());
        panelConsultaFasceOrarie.add(Box.createVerticalStrut(20));

        // Aree
        panelConsultaFasceOrarie.add(buildAreaSection());
        panelConsultaFasceOrarie.add(Box.createVerticalStrut(5));

        // Fasce orarie
        fasceHeaderLabel = buildSectionLabel("Fasce orarie");
        fasceHeaderLabel.setVisible(false);
        panelConsultaFasceOrarie.add(fasceHeaderLabel);
        panelConsultaFasceOrarie.add(Box.createVerticalStrut(8));
        panelConsultaFasceOrarie.add(buildFasceScrollPane());

        // Hint iniziale
        hintLabel = new JLabel("Seleziona una sala per vedere le date disponibili.");
        hintLabel.setFont(FONT_REGULAR);
        hintLabel.setForeground(TEXT_TERTIARY);
        hintLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelConsultaFasceOrarie.add(hintLabel);

        setContentPane(panelConsultaFasceOrarie);
        getContentPane().setBackground(BG_PAGE);
    }

    // ── Builder sezioni ───────────────────────────────────────────────────────

    private JComboBox<String> buildComboSala() {
        comboSale = new JComboBox<>();
        comboSale.addItem("Seleziona una sala...");
        for (String nome : gestoreSaleStudio.getNomiSale()) comboSale.addItem(nome);
        comboSale.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comboSale.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboSale.setFont(FONT_REGULAR);
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

    private JPanel buildAreaSection(){
        areaSection = new JPanel();
        areaSection.setLayout(new BoxLayout(areaSection, BoxLayout.Y_AXIS));
        areaSection.setOpaque(false);
        areaSection.setAlignmentX(Component.LEFT_ALIGNMENT);
        areaSection.setVisible(false);

        areaSection.add(buildSectionLabel("Area (opzionale)"));
        areaSection.add(Box.createVerticalStrut(8));

        comboArea = new JComboBox<>();
        comboArea.addItem("Nessuna preferenza");
        comboArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        comboArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboArea.setFont(FONT_REGULAR);
        comboArea.setBackground(BG_CARD);
        comboArea.addActionListener(e -> {
            if (dataSelezionata != null) aggiornaFasce();
        });

        areaSection.add(comboArea);
        areaSection.add(Box.createVerticalStrut(16));

        return areaSection;
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

    /**
     * Gestisce la selezione di una sala dalla combo.
     * <p>
     * Se è selezionato il placeholder, nasconde le sezioni dipendenti e mostra
     * l'hint iniziale. Altrimenti memorizza la sala scelta, genera i pulsanti
     * data, rende visibile la sezione area e ne ricarica le voci.
     */
    protected void onSalaSelezionata() {
        if (comboSale.getSelectedIndex() == 0) {
            dateScrollPane.setVisible(false);
            fasceScrollPane.setVisible(false);
            fasceHeaderLabel.setVisible(false);
            areaSection.setVisible(false);
            hintLabel.setVisible(true);
            revalidate(); repaint();
            return;
        }
        nomeSala = (String) comboSale.getSelectedItem();
        hintLabel.setVisible(false);
        buildDateButtons();
        dateScrollPane.setVisible(true);
        areaSection.setVisible(true);
        fasceScrollPane.setVisible(false);
        fasceHeaderLabel.setVisible(false);
        dataSelezionata = null;
        revalidate(); repaint();

        aggiornaComboAree();
    }

    /**
     * Genera i pulsanti dei dieci giorni a partire da oggi.
     * <p>
     * Alla pressione di un giorno, deseleziona gli altri, memorizza la data
     * scelta e ricarica le fasce orarie corrispondenti.
     */
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

    /**
     * Ricarica l'elenco delle fasce orarie per la sala, la data e l'area
     * correntemente selezionate, interrogando {@link GestoreSaleStudio}.
     * <p>
     * Per ogni fascia mostra i posti liberi sul totale (dell'area se selezionata,
     * altrimenti dell'intera sala) tramite {@link #buildSlotRow}. Se non vi sono
     * fasce, mostra un messaggio di assenza disponibilità.
     */
    protected void aggiornaFasce() {
        fascePanelInner.removeAll();

        // Area selezionata (null = nessuna preferenza)
        String areaSelezionata = (comboArea != null && comboArea.getSelectedIndex() > 0)
                ? (String) comboArea.getSelectedItem()
                : null;

        Map<FasciaOraria, Integer> fasceDisponibili = new TreeMap<>(
                Comparator.comparing(FasciaOraria::getOraInizio)
        );
        fasceDisponibili.putAll(gestoreSaleStudio.getNumPostazioniDisponibili(nomeSala, dataSelezionata, areaSelezionata));

        // Totale: dell'area se selezionata, altrimenti della sala
        int totale = (areaSelezionata != null)
                ? gestoreSaleStudio.getNumPostazioniArea(nomeSala, areaSelezionata)
                : gestoreSaleStudio.getNumPostazioniSala(nomeSala);

        if (fasceDisponibili.isEmpty()) {
            JLabel vuoto = new JLabel("Nessuna fascia oraria disponibile per questa data.");
            vuoto.setFont(FONT_REGULAR);
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

    /** Ricarica le voci della combo aree in base alla sala selezionata. */
    private void aggiornaComboAree() {
        if (comboArea == null) return;
        comboArea.removeAllItems();
        comboArea.addItem("Nessuna preferenza");
        List<String> aree = gestoreSaleStudio.getAreeSala(nomeSala);
        for (String area : aree) comboArea.addItem(area);
    }

    // ── Riga fascia (override nella sottoclasse) ──────────────────────────────

    /**
     * Costruisce la riga di una fascia oraria: orario a sinistra, conteggio posti
     * e pill di stato a destra. È {@code protected} per consentire alla
     * sottoclasse di estenderla aggiungendo il controllo di selezione.
     *
     * @param fascia fascia oraria da rappresentare
     * @param liberi posti liberi nella fascia
     * @param totale posti complessivi di riferimento
     * @return il pannello-riga pronto da inserire nella card
     */
    protected JPanel buildSlotRow(FasciaOraria fascia, int liberi, int totale) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(12, 16, 12, 16));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JLabel orario = new JLabel(fascia.toString());
        orario.setFont(FONT_REGULAR.deriveFont(14f));
        orario.setForeground(TEXT_PRIMARY);
        row.add(orario, BorderLayout.WEST);

        JPanel destra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        destra.setOpaque(false);

        JLabel postiLabel = new JLabel(liberi + " / " + totale + " postazioni libere");
        postiLabel.setFont(FONT_REGULAR.deriveFont(12f));
        postiLabel.setForeground(TEXT_SECONDARY);
        destra.add(postiLabel);
        destra.add(buildBadgePill(liberi, totale));

        row.add(destra, BorderLayout.EAST);
        return row;
    }
}