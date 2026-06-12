package boundary;

import  javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormBibliotecario extends JFrame{
    private JPanel contentPane;
    private JLabel titoloBibliotecario;
    private JButton btnCrea;
    private JButton btnModifica;
    private JButton btnElimina;


    public FormBibliotecario() {
        setTitle("Bibliotecario");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        
        setLocationRelativeTo(null);

        btnCrea.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FormCreaSala creaSala = new FormCreaSala(FormBibliotecario.this);
                creaSala.setVisible(true);
                setVisible(false);
            }
        });
    }

    public static void main(String[] args){
        FormBibliotecario form = new FormBibliotecario();
        form.setVisible(true);
    }
}
