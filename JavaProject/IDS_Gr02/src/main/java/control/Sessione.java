package control;

import entity.*;

// control/Sessione.java
public class Sessione {

    private static Sessione instance;
    private Bibliotecario bibliotecarioCorrente;
    private Studente studenteCorrente;

    private Sessione() {}

    public static Sessione getInstance() {
        if (instance == null) {
            instance = new Sessione();
        }
        return instance;
    }

    // apertura sessione bibliotecario
    public void apriSessioneBibliotecario(Bibliotecario b) {
        this.bibliotecarioCorrente = b;
        this.studenteCorrente = null;
    }

    // apertura sessione studente
    public void apriSessioneStudente(Studente s) {
        this.studenteCorrente = s;
        this.bibliotecarioCorrente = null;
    }

    public Bibliotecario getBibliotecarioCorrente() {
        return bibliotecarioCorrente;
    }

    public Studente getStudenteCorrente() {
        return studenteCorrente;
    }

    public boolean isBibliotecario() {
        return bibliotecarioCorrente != null;
    }

    public boolean isStudente() {
        return studenteCorrente != null;
    }

    public void chiudiSessione() {
        this.bibliotecarioCorrente = null;
        this.studenteCorrente = null;
    }
}