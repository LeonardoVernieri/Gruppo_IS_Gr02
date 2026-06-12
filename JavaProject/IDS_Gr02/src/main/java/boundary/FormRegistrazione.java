package boundary;

import control.GestoreAccesso;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormRegistrazione extends JFrame {

    private JPanel contentPane;
    private JTextField nomeField;
    private JTextField cognomeField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> ruoloComboBox;
    private JLabel campoAggiuntivoLabel;
    private JTextField campoAggiuntivoField;
    private JButton registratiButton;
    private JButton annullaButton;
    private JLabel messaggioLabel;

    private FormLogin parent;

    public FormRegistrazione(FormLogin parent) {
        this.parent = parent;
        setTitle("Registrazione");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        ruoloComboBox.addItem("STUDENTE");
        ruoloComboBox.addItem("BIBLIOTECARIO");

        ruoloComboBox.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        aggiornaCampoAggiuntivo();
                    }
                });

        registratiButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        eseguiRegistrazione();
                    }
                });

        annullaButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (parent != null) {
                            parent.setVisible(true);
                        }
                        dispose();
                    }
                });
    }


    private void aggiornaCampoAggiuntivo() {
        String ruolo = (String) ruoloComboBox
                .getSelectedItem();
        if ("STUDENTE".equals(ruolo))
        {
            campoAggiuntivoLabel.setText("Matricola:");
        } else {
            campoAggiuntivoLabel.setText("Codice Identificativo:");
        }
        campoAggiuntivoField.setText("");
    }

    private void eseguiRegistrazione() {
        String nome = nomeField.getText().trim();
        String cognome = cognomeField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String ruolo = (String) ruoloComboBox.getSelectedItem();
        Long extra = Long.valueOf(campoAggiuntivoField.getText().trim());
        boolean esito;

        if ("STUDENTE".equals(ruolo)){
            esito = GestoreAccesso.registraStudente(extra, nome, cognome, email, password);
            if(esito){
                JOptionPane.showMessageDialog(null, "Registrazione studente completata");
            }
        }
        else{
            esito = GestoreAccesso.registraBibliotecario(extra, nome, cognome, email, password);
            if(esito){
                JOptionPane.showMessageDialog(null, "Registrazione bibliotecario completata");
            }
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FormRegistrazione form = new FormRegistrazione(null);
            form.setVisible(true);
        });
    }
}