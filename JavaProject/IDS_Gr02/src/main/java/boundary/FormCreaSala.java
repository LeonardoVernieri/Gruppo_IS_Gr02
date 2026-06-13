package boundary;
import control.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class FormCreaSala extends JFrame{
    private JLabel lblTitoloCrea;
    private JTextField nomeSala;
    private JTextField numPostazioniTotali;
    private JTextField descrizione;
    private JLabel lblNome;
    private JLabel lblNumPostazioni;
    private JLabel lblDescrizione;
    private JButton btnSalva;
    private JPanel contentPane;
    private JButton btnAnnulla;
    private JComboBox comboApertura;
    private JComboBox comboChiusura;
    private JLabel lblOrarioA;
    private JLabel lblOrarioC;
    private JLabel areePresenza;
    private JCheckBox ckbPresenza;
    private JLabel lblAggiungiArea;
    private JLabel lblTipologia;
    private JLabel lblPostazioniArea;
    private JPanel panelAggiungiArea;
    private JButton btnAdd;
    private JSpinner spinnerPostazioni;
    private JComboBox comboTipologia;
    private JTable tabellaAree;

    DefaultTableModel tableModel = new DefaultTableModel(new String[]{"Tipologia", "Postazioni"}, 0);

    private FormBibliotecario parent;
    private Sessione session;

    public void salvaDati(){
        String nome = nomeSala.getText();
        String descrizione = this.descrizione.getText();
        int numeroPostazioniTotali = 0;
        LocalTime orarioApertura = LocalTime.parse((String) comboApertura.getSelectedItem());
        LocalTime orarioChiusura = LocalTime.parse((String)comboChiusura.getSelectedItem());
        boolean presenzaAree = ckbPresenza.isSelected();

        boolean sblocco = true;

        List<String> col1 = new ArrayList<>();
        List<Integer> col2 = new ArrayList<>();
        if (presenzaAree) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String tipologia = (String) tableModel.getValueAt(i, 0);
                int numPost = (Integer) tableModel.getValueAt(i, 1);
                col1.add(tipologia);
                col2.add(numPost);
            }
        }

        try{
            numeroPostazioniTotali = Integer.parseInt(numPostazioniTotali.getText());
            if (numeroPostazioniTotali <= 0) {
                throw new IllegalArgumentException("Il numero deve essere maggiore di zero");
            }
        }
        catch(NumberFormatException e){
            JOptionPane.showMessageDialog(null, "Il campo delle postazioni deve contenere un numero intero positivo");
            sblocco = false;
        }
        catch(IllegalArgumentException e){
            JOptionPane.showMessageDialog(null, e.getMessage());
            sblocco = false;
        }

        if(nome.isEmpty()){
            JOptionPane.showMessageDialog(null, "Il nome non può essere vuoto");
            sblocco = false;
        }
        if(descrizione.isEmpty()){
            JOptionPane.showMessageDialog(null, "Inserire una descrizione");
            sblocco = false;
        }

        if((Integer)spinnerPostazioni.getValue() < 0){
            JOptionPane.showMessageDialog(null, "Il numero di postazioni per area non possono essere negativi");
            sblocco = false;
        }
        if (col1.size() != col2.size()) {
            sblocco = false;
        }
        int s=0;
        for(Integer n : col2){
            s+=n;
        }
        if(s>Integer.parseInt(numPostazioniTotali.getText())){
            JOptionPane.showMessageDialog(null, "Le postazioni delle aree non possono essere più delle postazioni totali");
            sblocco = false;
        }


        //boolean esito1 = GestoreSaleStudio.(col1, col2, numeroPostazioniTotali);
        if(/*esito1&& */ sblocco){
            boolean esito = GestoreSaleStudio.aggiungiSalaStudio(nome, descrizione, numeroPostazioniTotali, orarioApertura, orarioChiusura, presenzaAree, col1, col2);
            if(esito){
                btnSalva.setForeground(Color.GREEN);
                JOptionPane.showMessageDialog(null, "Sala creata correttamente");
            }
        }
        else{
            btnSalva.setForeground(Color.RED);
            JOptionPane.showMessageDialog(null, "Sala non creata correttamente");
        }

        dispose();
        parent.setVisible(true);
    }

    public FormCreaSala(FormBibliotecario parent, Sessione session) {
        this.parent = parent;
        this.session = session;

        setTitle("Crea Sala");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        panelAggiungiArea.setVisible(false);


        tabellaAree.setModel(tableModel);

        tableModel = new DefaultTableModel(new String[]{"Tipologia", "Postazioni", ""}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // solo la colonna del bottone è cliccabile
            }
        };
        tabellaAree.setModel(tableModel);
        tabellaAree.getColumnModel().getColumn(2).setMaxWidth(40);
        tabellaAree.getColumnModel().getColumn(2).setCellRenderer(
                new TableCellRenderer() {
                    private final JButton btn = new JButton("-");
                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                        return btn;
                    }
                }
        );
        DefaultTableModel finalTableModel = tableModel;
        tabellaAree.getColumnModel().getColumn(2).setCellEditor(
                new DefaultCellEditor(new JCheckBox()) {
                    private final JButton btn = new JButton("-");
                    private int row;
                    { btn.addActionListener(ev -> finalTableModel.removeRow(row)); }
                    @Override
                    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int col) {
                        this.row = r;
                        return btn;
                    }
                    @Override
                    public Object getCellEditorValue() { return "-"; }
                }
        );

        btnSalva.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvaDati();
            }
        });

        btnAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.setVisible(true);
                dispose();
            }
        });
        ckbPresenza.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(ckbPresenza.isSelected()){
                    panelAggiungiArea.setVisible(true);
                }
                else{
                    panelAggiungiArea.setVisible(false);
                }
            }
        });
        DefaultTableModel finalTableModel1 = tableModel;
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String tipologia = (String) comboTipologia.getSelectedItem();
                int postazioni = (Integer) spinnerPostazioni.getValue();
                finalTableModel1.addRow(new Object[]{tipologia, postazioni});
            }
        });
    }
}
