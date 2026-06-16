package entity;

import database.GestorePersistenza;

import java.util.Map;

public class RegistroBibliotecari {

    GestorePersistenza gestorePersistenza = new GestorePersistenza();

    public Bibliotecario cercaBibliotecarioPerEmail(String email) {
        return gestorePersistenza.cercaPrimoPerCampi(
                Bibliotecario.class,
                Map.of("email", email)
        );
    }

    public Bibliotecario cercaBibliotecarioPerCodiceInterno(Long codice) {
        return gestorePersistenza.cercaPrimoPerCampi(
                Bibliotecario.class,
                Map.of("codiceInterno", codice)
        );
    }

    public boolean creaBibliotecario(Long codiceInterno,
                                  String nome, String cognome,
                                  String email, String password) {

        Bibliotecario bib = new Bibliotecario(codiceInterno, nome, cognome, email, password);

        gestorePersistenza.salva(bib);
        return true;
    }
}