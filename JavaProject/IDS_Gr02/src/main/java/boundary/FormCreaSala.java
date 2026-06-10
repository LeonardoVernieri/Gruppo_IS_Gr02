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

    //GestoreSalaStudio ges = new GestoreSalaStudio(); /*Dato che non è un metodo static non posso chiamarlo come ho
    // fatto io. Domanda: dovrei impostarlo come static?*/

    public void salvaDati(){
        String nome = nomeSala.getText();
        String descrizione = this.descrizione.getText();
        int numeroPostazioniTotali = Integer.parseInt(numPostazioniTotali.getText());
        LocalTime orarioApertura = LocalTime.parse((String) comboApertura.getSelectedItem());
        LocalTime orarioChiusura = LocalTime.parse((String)comboChiusura.getSelectedItem());
        boolean presenzaAree = ckbPresenza.isSelected();


        //List<Object[]> aree = new ArrayList<>();
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

        boolean esito = GestoreSalaStudio.aggiungiSalaStudio(nome, descrizione, numeroPostazioniTotali, orarioApertura, orarioChiusura, presenzaAree);
        boolean esito1 = GestoreSalaStudio.aggiungiArea(col1, col2);
        if(esito && esito1){
            btnSalva.setForeground(Color.GREEN);
            JOptionPane.showMessageDialog(null, "Sala creata correttamente");
            dispose();
            parent.setVisible(true);
        }
        else{
            btnSalva.setForeground(Color.RED);
        }
    }

    public FormCreaSala(FormBibliotecario parent) {
        this.parent = parent;

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
