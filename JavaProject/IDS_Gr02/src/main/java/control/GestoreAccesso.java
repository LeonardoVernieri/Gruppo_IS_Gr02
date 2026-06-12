package control;

import database.GestoreBibliotecari;
import database.GestoreStudenti;
import entity.Bibliotecario;
import entity.Studente;

public class GestoreAccesso {

    private static final int MAX_TENTATIVI = 3;
    private int contatore = 0;

    public Object loginUtente(String email, String password)
            throws Exception {
        if (contatore >= MAX_TENTATIVI) {
            throw new Exception(
                    "Account bloccato: troppi tentativi falliti.");
        }

        GestoreStudenti gestoreStudenti = new GestoreStudenti();

        Studente studente = gestoreStudenti.cercaStudente(email);

        if (studente == null) {
            GestoreBibliotecari gestoreBibliotecari = new GestoreBibliotecari();

            Bibliotecario bibliotecario = gestoreBibliotecari.cercaBibliotecario(email);

            if (bibliotecario == null || !bibliotecario.getPassword().equals(password)) {
                contatore++;
                throw new Exception("Credenziali errate/Tentativi Massimi Raggiunti");
            }
            contatore = 0;
            return bibliotecario;
        }

        if (!studente.getPassword().equals(password))
        {
            contatore++;
            throw new Exception("Credenziali errate/Tentativi Massimi Raggiunti");
        }
        contatore = 0;
        return studente;
    }

    public boolean isBloccato() {return contatore >= MAX_TENTATIVI;}



    public void registraStudente(Long matricola, String nome, String cognome, String email, String password)
            throws Exception {validaDati(nome, cognome, email, password);

        if (matricola == null) {
            throw new Exception("Matricola obbligatoria.");
        }
        GestoreStudenti gestoreStudenti = new GestoreStudenti();
        GestoreBibliotecari gestoreBibliotecari = new GestoreBibliotecari();


        if (gestoreStudenti.cercaStudente(email) != null || gestoreBibliotecari.cercaBibliotecario(email) != null) {
            throw new Exception("Email già associata a un altro Utente.");
        }

        if (gestoreStudenti.cercaPerMatricola(matricola) != null) {
            throw new Exception("Matricola già registrata.");
        }

        gestoreStudenti.creaStudente(matricola, nome, cognome, email, password);}

    public void registraBibliotecario(Long codiceInterno, String nome, String cognome, String email, String password)
            throws Exception {validaDati(nome, cognome, email, password);

        if (codiceInterno == null)
            throw new Exception("Codice identificativo obbligatorio.");

        GestoreStudenti gestoreStudenti = new GestoreStudenti();
        GestoreBibliotecari gestoreBibliotecari = new GestoreBibliotecari();

        if (gestoreStudenti.cercaStudente(email) != null || gestoreBibliotecari.cercaBibliotecario(email) != null)
        {
            throw new Exception("Email già registrata.");
        }
        gestoreBibliotecari.creaBibliotecario(codiceInterno, nome, cognome, email, password);
    }

    public void registraUtente(String ruolo, String nome, String cognome, String email, String password, Long extra)
            throws Exception
    {
        if ("STUDENTE".equals(ruolo)) {registraStudente(extra, nome, cognome, email, password);}
        else {registraBibliotecario(extra, nome, cognome, email, password);}
    }

    private void validaDati(String nome, String cognome, String email, String password)
            throws Exception {
        if (nome == null || nome.isBlank())
            throw new Exception("Nome obbligatorio.");
        if (cognome == null || cognome.isBlank())
            throw new Exception("Cognome obbligatorio.");
        if (email == null || !email.contains("@"))
            throw new Exception("Email non valida.");
    }
}