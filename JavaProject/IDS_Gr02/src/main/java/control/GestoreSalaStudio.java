package control;

import entity.*;

import javax.swing.*;
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
        /*bibliotecario = Sessione.getInstance().getBibliotecarioCorrente();
        if (bibliotecario == null) return false; // sessione non valida*/
        return bibliotecario.creaSalaStudio(nome, descrizione, numeroPostazioniTotali, orarioApertura, orarioChiusura, presenzaAree);
    }

    public static boolean aggiungiArea(List<String> str, List<Integer> num, int numPostazioniTotali) {
        if (str.size() != num.size()) {
            return false;
        }
        int s=0;
        for(Integer n : num){
            s+=n;
        }
        if(s>numPostazioniTotali){
            JOptionPane.showMessageDialog(null, "Le postazioni delle aree non possono essere più delle postazioni totali");
            return false;
        }
        else{
            for (int i = 0; i < str.size(); i++) {
                Area area = new Area();
                area.creaArea(str.get(i), num.get(i));
            }
            return true;
        }
    }
}