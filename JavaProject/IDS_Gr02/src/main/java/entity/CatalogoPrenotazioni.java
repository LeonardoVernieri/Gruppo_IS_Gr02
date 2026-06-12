package entity;

import database.GestorePersistenza;

public class CatalogoPrenotazioni {

    private GestorePersistenza gestorePersistenza;

    public CatalogoPrenotazioni(){
        gestorePersistenza = new GestorePersistenza();
    }

    public void registraPrenotazione(Prenotazione prenotazione){
        gestorePersistenza.salva(prenotazione);
    }

}
