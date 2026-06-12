package boundary;

import control.GestoreAccesso;
import control.Sessione;
import entity.Bibliotecario;
import entity.Studente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FormLogin extends JFrame {
    private JPanel panel1;
    private JPanel contentPane;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registratiButton;
    private JLabel messaggioLabel;

    private GestoreAccesso gestoreAccesso = new GestoreAccesso();

    public FormLogin() {

        setTitle("Login");
        setContentPane(contentPane);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        setLocationRelativeTo(null);

        loginButton.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                eseguiLogin();
                            }
                        }
                );

        registratiButton.addActionListener(
                        new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                apriRegistrazione();
                            }
                        }
                );
    }

    private void eseguiLogin() {
        if (gestoreAccesso.isBloccato()) {
            JOptionPane.showMessageDialog(null, "Tentativi massimi raggiunti");
            dispose();
            return;
        }

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        Object utente = gestoreAccesso.loginUtente(email, password);
        if (utente == null) {
            if (gestoreAccesso.isBloccato()){
                JOptionPane.showMessageDialog(this, "Account bloccato: troppi tentativi falliti.", "Errore", JOptionPane.ERROR_MESSAGE);
                messaggioLabel.setText("Tentativi massimi raggiunti.");
                loginButton.setEnabled(false);
            }
            else{
                JOptionPane.showMessageDialog(this, "Credenziali errate, o registrazione non effettuata", "Errore di Accesso", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }

        messaggioLabel.setForeground(new Color(0, 128, 0));

        if (utente instanceof Studente s) {
            messaggioLabel.setText("Benvenuto " + s.getNome() + "!");
            JOptionPane.showMessageDialog(this, "Login eseguito come Studente.", "Accesso eseguito", JOptionPane.INFORMATION_MESSAGE);
            Sessione session = Sessione.getInstance();
            session.apriSessioneStudente(s);
            FormStudente formStudente = new FormStudente(session);
            formStudente.setVisible(true);
            dispose();
        }
        else if (utente instanceof Bibliotecario b) {
            messaggioLabel.setText("Benvenuto " + b.getNome() + "!");
            JOptionPane.showMessageDialog(this, "Login eseguito come Bibliotecario.", "Accesso eseguito", JOptionPane.INFORMATION_MESSAGE);
            Sessione session = Sessione.getInstance();
            session.apriSessioneBibliotecario(b);
            FormBibliotecario formBib = new FormBibliotecario(session);
            formBib.setVisible(true);
            dispose();
        }
    }


    private void apriRegistrazione(){
        FormRegistrazione formReg = new FormRegistrazione(this);
        formReg.setVisible(true);
        dispose();
    }
}

