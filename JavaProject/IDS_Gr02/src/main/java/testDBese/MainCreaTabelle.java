package testDBese;

import entity.*;
import boundary.FormStudente;
import database.GestorePersistenza;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.internal.build.AllowSysOut;
import entity.Prenotazione;
import entity.StatoPrenotazioneEnum;
import java.time.LocalDate;
import java.time.LocalTime;

import java.time.LocalDate;
import java.time.LocalTime;

public class MainCreaTabelle {

    public static void inizializzaDb(){

        GestorePersistenza gp = new  GestorePersistenza();

        LocalTime orarioApertura = LocalTime.of(8, 0);
        LocalTime orarioChiusura = LocalTime.of(18, 0);


        SalaStudio s = new SalaStudio("Sala1", "descrizione scritta...", 10, orarioApertura, orarioChiusura);
        gp.salva(s);

        for (int i=0; i<10; i++){
            Postazione p = new Postazione(s);
            gp.salva(p);
        }
    }

    /*public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("bibliotecaSys");
        emf.close();
        System.out.println("Avvio di Hibernate completato.");

        // new FormStudente();

        MainCreaTabelle.inizializzaDb();

        Bibliotecario b = new Bibliotecario();

        SalaStudio s = b.getSaleStudioPerNome("Sala1");

        for(FasciaOraria f : s.getFasceOrarie()){
            System.out.println(f);
        }
    }

    // prova
*/

    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("bibliotecaSys");
        emf.close();
        System.out.println("Avvio di Hibernate completato.");

        // Crea uno studente di prova
        Studente studente = new Studente();
        GestorePersistenza gp = new GestorePersistenza();
        gp.salva(studente);

        // Lancia il FormStudente
        new FormStudente(studente); //prova

        // Crea una prenotazione di test per oggi
        Prenotazione prenotazione = new Prenotazione();
        prenotazione.setStudente(studente);
        prenotazione.setStato(StatoPrenotazioneEnum.ATTIVA);
        prenotazione.setDataCheckIn(LocalDate.now());
        prenotazione.setInizioTempo(LocalTime.of(9, 0));
        prenotazione.setFineTempo(LocalTime.of(10, 0));
        gp.salva(prenotazione);
    }

}

