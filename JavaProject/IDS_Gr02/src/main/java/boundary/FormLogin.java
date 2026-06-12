package boundary;

import control.GestoreAccesso;
import entity.Bibliotecario;
import entity.Studente;
import stub.BibliotecarioStub;

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

        loginButton.addActionListener
                (
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        eseguiLogin();
                    }
                }
                );

        registratiButton.addActionListener
                (
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
            messaggioLabel.setForeground(Color.RED);
            messaggioLabel.setText("Tentativi massimi raggiunti.");
            loginButton.setEnabled(false);
            return;
        }

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            Object utente = gestoreAccesso.loginUtente(email, password);
            messaggioLabel.setForeground(new Color(0, 128, 0));
            if (utente instanceof Studente s)
            {
                messaggioLabel.setText("Benvenuto " + s.getNome() + "!");
                JOptionPane.showMessageDialog(this, "Login eseguito come Studente.", "Accesso eseguito", JOptionPane.INFORMATION_MESSAGE);
            }
            else if (utente instanceof Bibliotecario b) {
                messaggioLabel.setText("Benvenuto " + b.getNome() + "!");
                BibliotecarioStub stub = new BibliotecarioStub(b.getNome(), b.getCognome());
                FormBibliotecario formBib = new FormBibliotecario();
                formBib.setVisible(true);
                this.dispose();
            }

        }
        catch (Exception ex) {
            messaggioLabel.setForeground(Color.RED);
            messaggioLabel.setText(ex.getMessage());
        }
    }

    private void apriRegistrazione()
    {
        FormRegistrazione formReg = new FormRegistrazione(this);
        formReg.setVisible(true);
        this.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FormLogin form = new FormLogin();
            form.setVisible(true);
        });
    }
}


