package control;

import dto.FasciaOraria;
import entity.Area;
import entity.Bibliotecario;
import entity.CatalogoSalaStudio;
import entity.SalaStudio;

import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GestoreSaleStudio {

    private CatalogoSalaStudio catalogoSala;

    public GestoreSaleStudio() {
        catalogoSala = new CatalogoSalaStudio();
    }

    public Map<FasciaOraria, Integer> getFascieOrarieDisponibili(String nomeSala, LocalDate data) {
        return catalogoSala.getDisponibilitaFasciaOrariaSalaPerData(nomeSala, data);
    }

    public List<String> getNomiSale() {

        List<String> nomiSale = new ArrayList<>();

        for (SalaStudio s : catalogoSala.getSale()) {
            nomiSale.add(s.getNome());
        }

        return nomiSale;
    }

    public int getNumPostazioniSala(String nomeSala){
        return catalogoSala.getPostazioniTotali(nomeSala);
    }

    public int getNumPostazioniLibere(FasciaOraria fascia, LocalDate date, String nomeSala) {
        return catalogoSala.getPostazioniLibere(fascia, date, nomeSala);
    }

    public List<String> getAreeSala(String nomeSala){
        List<String> areeSale = new ArrayList<>();

        // TODO

        return areeSale;
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
}
