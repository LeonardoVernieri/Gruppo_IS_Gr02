package entity;

import database.GestorePersistenza;
import dto.FasciaOraria;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CatalogoSalaStudio {

    private final GestorePersistenza gestorePersistenza;

    public CatalogoSalaStudio() {
        gestorePersistenza = new GestorePersistenza();
    }

    public SalaStudio getSalaPerNome(String nome){
        return gestorePersistenza.cercaPrimoPerCampi(SalaStudio.class, Map.of("nome", nome)) ;
    }


    public Map<FasciaOraria, Integer> getDisponibilitaFasciaOrariaSalaPerData(String nomeSala, LocalDate date){

        Map<FasciaOraria, Integer> fascieOrarie =  new HashMap<FasciaOraria, Integer>();

        SalaStudio s = getSalaPerNome(nomeSala);

        for ( FasciaOraria fascia : s.getFasceOrarie()){
            fascieOrarie.put(fascia, s.getPostiLiberi(fascia, date));
        }
        return fascieOrarie;
    }

    public List<SalaStudio> getSale(){
        return gestorePersistenza.cercaPerCampi(SalaStudio.class, Map.of());
    }

    public int getPostazioniTotali(String sala){
        return getSalaPerNome(sala).getNumeroPostazioni();
    }


    public int getPostazioniLibere(FasciaOraria fascia, LocalDate date, String sala){
        SalaStudio s = getSalaPerNome(sala);
        return s.getPostiLiberi(fascia, date);
    }
}
