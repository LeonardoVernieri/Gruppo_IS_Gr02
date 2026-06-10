package control;

import entity.*;

import java.time.LocalTime;
import java.util.List;

public class GestoreSalaStudio {

    public GestoreSalaStudio() {

    }

    public static boolean aggiungiSalaStudio(String nome,
                                             String descrizione,
                                             int numeroPostazioniTotali,
                                             LocalTime orarioApertura,
                                             LocalTime orarioChiusura,
                                             boolean presenzaAree) {

        Bibliotecario bibliotecario = new Bibliotecario();
        return bibliotecario.creaSalaStudio(nome, descrizione, numeroPostazioniTotali, orarioApertura, orarioChiusura, presenzaAree);
    }

    public static boolean aggiungiArea(List<String> str, List<Integer> num) {
        if (str.size() != num.size()) {
            return false;
        }

        for (int i = 0; i < str.size(); i++) {
            Area area = new Area();
            area.creaArea(str.get(i), num.get(i));
        }

        return true;
    }
}