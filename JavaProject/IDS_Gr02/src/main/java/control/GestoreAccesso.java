package control;

import entity.GestoreBibliotecari;
import entity.GestoreStudenti;
import entity.Bibliotecario;
import entity.Studente;
import org.hibernate.sql.exec.spi.JdbcOperation;

import javax.swing.*;

public class GestoreAccesso {

    private static final int MAX_TENTATIVI = 3;
    private int contatore = 0;

    public Object loginUtente(String email, String password) {

        if (contatore >= MAX_TENTATIVI)
            return null;

        GestoreStudenti gestoreStudenti = new GestoreStudenti();
        Studente studente = gestoreStudenti.cercaStudente(email);

        if (studente == null) {
            GestoreBibliotecari gestoreBibliotecari = new GestoreBibliotecari();
            Bibliotecario bibliotecario = gestoreBibliotecari.cercaBibliotecario(email);

            if (bibliotecario == null || !bibliotecario.getPassword().equals(password)) {
                contatore++;
                return null;
            }
            contatore = 0;
            return bibliotecario;
        }

        if (!studente.getPassword().equals(password)) {
            contatore++;
            return null;
        }

        contatore = 0;
        return studente;
    }

    public boolean isBloccato() {return contatore >= MAX_TENTATIVI;}



    public static boolean registraStudente(Long matricola, String nome, String cognome, String email, String password){

        if (!validaDati(nome, cognome, email, password)) {
            return false;
        }

        if (matricola == null) {
            JOptionPane.showMessageDialog(null, "Inserire matricola");
            return false;
        }
        GestoreStudenti gestoreStudenti = new GestoreStudenti();


        if (gestoreStudenti.cercaStudente(email) != null) {
            JOptionPane.showMessageDialog(null, "Email già associata ad un altro studente");
            return false;
        }

        if (gestoreStudenti.cercaPerMatricola(matricola) != null) {
            JOptionPane.showMessageDialog(null, "Matricola già registrata");
            return false;
        }

        return gestoreStudenti.creaStudente(matricola, nome, cognome, email, password);
    }

    public static boolean registraBibliotecario(Long codiceInterno, String nome, String cognome, String email, String password){

        if (!validaDati(nome, cognome, email, password)) {
            return false;
        }

        if (codiceInterno == null){
            JOptionPane.showMessageDialog(null, "Codice identificativo obbligatorio.");
            return false;
        }
        GestoreBibliotecari gestoreBibliotecari = new GestoreBibliotecari();

        if (gestoreBibliotecari.cercaBibliotecario(email) != null){
            JOptionPane.showMessageDialog(null, "Email già registrata.");
            return false;
        }
        return gestoreBibliotecari.creaBibliotecario(codiceInterno, nome, cognome, email, password);
    }

    public void registraUtente(String ruolo, String nome, String cognome, String email, String password, Long extra)
            throws Exception
    {
        if ("STUDENTE".equals(ruolo)) {registraStudente(extra, nome, cognome, email, password);}
        else {registraBibliotecario(extra, nome, cognome, email, password);}
    }

    public static boolean validaDati(String nome, String cognome, String email, String password) {
        if (nome == null || nome.isBlank()) {
            JOptionPane.showMessageDialog(null, "Nome obbligatorio");
            return false;
        }
        if (cognome == null || cognome.isBlank()) {
            JOptionPane.showMessageDialog(null, "Cognome obbligatorio");
            return false;
        }
        if (email == null || !email.contains("@")) {
            JOptionPane.showMessageDialog(null, "Email non valida");
            return false;
        }
        return true;
    }
}