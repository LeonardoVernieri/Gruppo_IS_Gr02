package database;

import entity.Bibliotecario;
import java.util.Map;

public class GestoreBibliotecari {

    public Bibliotecario cercaBibliotecario(String email) {
        GestorePersistenza gp = new GestorePersistenza();
        return gp.cercaPrimoPerCampi(
                Bibliotecario.class,
                Map.of("email", email)
        );
    }

    public void creaBibliotecario(Long codiceInterno,
                                  String nome, String cognome,
                                  String email, String password) {

        Bibliotecario bib = new Bibliotecario(codiceInterno, nome, cognome, email, password);

        GestorePersistenza gp = new GestorePersistenza();
        gp.salva(bib);
    }
}