package dto;

import java.time.LocalTime;

public class FasciaOraria {
    private final LocalTime oraInizio;
    private final LocalTime oraFine;

    public FasciaOraria(LocalTime oraInizio, LocalTime oraFine) {
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
    }



    @Override
    public String toString() {
        return oraInizio + " - " + oraFine;
    }

    public LocalTime getInizio() { return oraInizio; }
    public LocalTime getFine() { return oraFine; }
    public LocalTime getOraInizio() { return oraInizio; }
    public LocalTime getOraFine() { return oraFine; }
}
