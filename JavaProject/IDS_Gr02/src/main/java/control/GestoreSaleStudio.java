package control;

import dto.FasciaOraria;
import entity.CatalogoSalaStudio;
import entity.SalaStudio;

import java.time.LocalDate;
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

        areeSale.add("Area1");
        areeSale.add("Area2");
        return areeSale;
    }
}
