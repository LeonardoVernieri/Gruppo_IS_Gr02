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


    public static boolean aggiungiSalaStudio(String nome,
                                             String descrizione,
                                             int numeroPostazioniTotali,
                                             LocalTime orarioApertura,
                                             LocalTime orarioChiusura,
                                             boolean presenzaAree,
                                             List<String> col1,
                                             List<Integer> col2) {

        Bibliotecario bibliotecario = new Bibliotecario();
        return bibliotecario.creaSalaStudio(nome, descrizione, numeroPostazioniTotali, orarioApertura, orarioChiusura, presenzaAree, col1, col2);
    }
}
