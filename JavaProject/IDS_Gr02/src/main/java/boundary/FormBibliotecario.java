package boundary;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormBibliotecario extends JFrame{
    private JPanel contentPane;
    private JLabel titoloBibliotecario;
    private JButton btnCrea;
    private JButton btnModifica;
    private JButton btnElimina;
    private JButton monitoraggioAnadamentoSaleButton;
    private JButton visualizzaStoricoPrenotazioniButton;
//    public FormBibliotecario(BibliotecarioStub b) {
//        setTitle("Bibliotecario");
//        setContentPane(contentPane);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setResizable(false);
//        pack();
//
//        setLocationRelativeTo(null);
//
//        btnCrea.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                FormCreaSala creaSala = new FormCreaSala(FormBibliotecario.this, b);
//                creaSala.setVisible(true);
//                setVisible(false);
//            }
//        });
//    }
//
//    public String nomeBib(BibliotecarioStub b){
//        return b.getNome();
//    }
//
//    public String cognomeBib(BibliotecarioStub b){
//        return b.getCognome();
//    }
//
//    public static void main(String[] args){
//        BibliotecarioStub bib = new BibliotecarioStub("Mario", "Rossi");
//        FormBibliotecario form = new FormBibliotecario(bib);
//        form.setVisible(true);
//    }
}
