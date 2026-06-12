package entity;

import database.GestorePersistenza;

import java.util.Map;

public class GestoreStudenti {

    public Studente cercaStudente(String email) {
        GestorePersistenza gp = new GestorePersistenza();
        return gp.cercaPrimoPerCampi(
                Studente.class,
                Map.of("email", email)
        );
    }

    public Studente cercaPerMatricola(Long matricola) {
        GestorePersistenza gp = new GestorePersistenza();
        return gp.cercaPrimoPerCampi(
                Studente.class,
                Map.of("matricola", matricola)
        );
    }
    public boolean creaStudente(Long matricola, String nome,
                             String cognome, String email,
                             String password) {

        Studente studente = new Studente(matricola, nome, cognome, email, password);

        GestorePersistenza gp = new GestorePersistenza();
        gp.salva(studente);
        return true;
    }
}