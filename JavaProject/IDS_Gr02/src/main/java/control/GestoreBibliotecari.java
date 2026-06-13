package control;

import database.GestorePersistenza;
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

    public boolean creaBibliotecario(Long codiceInterno,
                                  String nome, String cognome,
                                  String email, String password) {

        Bibliotecario bib = new Bibliotecario(codiceInterno, nome, cognome, email, password);

        GestorePersistenza gp = new GestorePersistenza();
        try
        {
            gp.salva(bib);
            return true;
        }
        catch (RuntimeException e)
        {
            return false;
        }
    }
}