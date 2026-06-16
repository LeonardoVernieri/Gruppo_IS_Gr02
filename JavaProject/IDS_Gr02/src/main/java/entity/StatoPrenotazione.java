package entity;

/**
 * Stato comportamentale di una {@link Prenotazione} nel pattern State.
 * <p>
 * Definisce il contratto delle transizioni dipendenti dallo stato: ogni
 * implementazione concreta ({@link StatoPAttiva}, {@link StatoPConfermata},
 * {@link StatoPScaduta}, {@link StatoPAnnullata}) decide se e come la
 * prenotazione possa evolvere a partire dallo stato che rappresenta. In questo
 * modo la logica di transizione è distribuita sugli oggetti-stato invece di
 * concentrarsi in condizionali sull'enum.
 */
public interface StatoPrenotazione {

    /**
     * Tenta la transizione verso lo stato {@code CONFERMATA} a seguito del
     * check-in.
     * <p>
     * Il comportamento dipende dallo stato concreto: solo gli stati per cui la
     * conferma è ammessa effettuano il passaggio (tipicamente da {@code ATTIVA}),
     * mentre gli altri la rifiutano o la ignorano secondo le regole del dominio.
     *
     * @param prenotazione prenotazione su cui applicare la transizione
     */
    public void conferma(Prenotazione prenotazione);
}