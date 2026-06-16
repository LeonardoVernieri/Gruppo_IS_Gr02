# Gruppo_IS_VernieriLeonardo
## Traccia esame
### Sistema di Prenotazione di Postazioni in Biblioteca Universitaria
Si desidera sviluppare un sistema software per la gestione della prenotazione di postazioni di studio all'interno di
una biblioteca universitaria. Il sistema dovrà consentire agli studenti di prenotare una postazione in specifiche
fasce orarie, visualizzare le proprie prenotazioni e accedere ai servizi della biblioteca in modo organizzato. Il
sistema coinvolge due tipologie di utenti: studenti e bibliotecari, ciascuna con funzionalità e permessi specifici.
### Descrizione del Sistema:
Il sistema consente la registrazione di utenti tramite autenticazione con credenziali personali. Al momento della
registrazione, ciascun utente deve specificare il proprio ruolo, scegliendo tra studente o bibliotecario, oltre a fornire
nome, cognome, email istituzionale e numero di matricola o codice identificativo interno.
Ogni bibliotecario ha la possibilità di creare e gestire una o più sale studio. Per ciascuna sala, mediante apposita
interfaccia grafica, è possibile specificare un nome, una descrizione, il numero totale di postazioni disponibili e
gli orari di apertura giornalieri. Ogni sala può essere suddivisa opzionalmente in aree differenti, ad esempio area
silenziosa, area consultazione o area lavoro di gruppo. Il bibliotecario può inoltre visualizzare in ogni momento
l'elenco delle prenotazioni effettuate per ciascuna sala e monitorarne lo stato.
Ogni studente autenticato dispone di una propria interfaccia nella quale può consultare le sale disponibili e
verificare, per ciascun giorno, le fasce orarie prenotabili. Una prenotazione viene effettuata selezionando una sala,
una data, una fascia oraria e, se prevista, una specifica area o una specifica postazione. Il sistema deve impedire
che una stessa postazione venga assegnata contemporaneamente a più studenti nella medesima fascia oraria. Una
volta completata la prenotazione, essa entra nello stato "attiva".
Lo studente può visualizzare all'interno del proprio profilo personale l'elenco delle prenotazioni effettuate, con le
relative informazioni di sala, data, orario e stato. Deve inoltre essere possibile annullare una prenotazione entro un
certo limite temporale precedente all'inizio della fascia prenotata. In caso di annullamento, la postazione torna
automaticamente disponibile per altri utenti.
Nel giorno della prenotazione, lo studente può effettuare il check-in tramite una apposita interfaccia grafica,
selezionando la prenotazione attiva e confermando la propria presenza. Per semplicità, si può supporre che il
check-in debba essere effettuato entro un intervallo di tempo limitato rispetto all'orario di inizio della prenotazione.
Se il check-in non viene effettuato entro tale intervallo, la prenotazione passa automaticamente nello stato
"scaduta" e la postazione viene resa nuovamente disponibile.
Il bibliotecario, attraverso la propria interfaccia, può consultare in tempo reale la situazione delle sale,
visualizzando il numero di postazioni occupate, il numero di postazioni libere e l'elenco delle prenotazioni attive
per la giornata corrente. Deve inoltre essere possibile accedere a uno storico delle prenotazioni per ciascuna sala
e per ciascuno studente.
Ogni studente ha accesso a un profilo personale che consente di consultare le prenotazioni future, quelle passate,
quelle annullate ed eventualmente il numero totale di accessi effettuati alla biblioteca. I bibliotecari, attraverso
un'interfaccia dedicata, possono monitorare l'andamento complessivo del servizio, visualizzando il tasso di
occupazione delle sale, il numero di prenotazioni giornaliere e il numero di prenotazioni non confermate tramite
check-in.
Il sistema dovrà essere accessibile via web sia da desktop che da dispositivi mobili, con un'interfaccia grafica
intuitiva e responsiva. È previsto un sistema di notifiche che avvisi gli studenti della conferma della prenotazione,
di eventuali annullamenti e dell'approssimarsi dell'orario prenotato. Il sistema dovrà inoltre garantire la protezione
dei dati personali e una corretta gestione dei permessi e delle visibilità tra studenti e bibliotecari.

Per tale sistema, il team realizzi un progetto secondo il facsimile fornito dal docente, progetti la base di dati in
maniera completa popolandola con dei dati d'esempio, ed implementi il progetto in linguaggio Java. L'analisi dei
requisiti e la progettazione di alto livello del sistema dovrà essere svolta dal team completo, mentre ogni membro
del team sarà poi responsabile della progettazione di dettaglio e dell'implementazione di uno dei casi d'uso a
scelta dell'applicazione.

---

## 🚀 Setup Rapido

### Requirements
- Java 26+ (openjdk)
- Maven 3.6+
- MySQL 8.0+ (running on localhost:3306)

### Installation

**1. Clone il repository:**
```bash
git clone https://github.com/LeonardoVernieri/Gruppo_IS_VernieriLeonardo.git
cd Gruppo_IS_VernieriLeonardo
```

**2. Configura il database:**

Apri `database.properties` e modifica con le tue credenziali MySQL:

```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://127.0.0.1:3306/biblioteca_db?createDatabaseIfNotExist=true
db.username=root
db.password=your_mysql_password
```

Il database viene creato automaticamente al primo avvio se non esiste.

**3. Build e Run:**
```bash
mvn clean package -f JavaProject/IDS_Gr02/pom.xml
java -jar JavaProject/IDS_Gr02/target/Locus-executable.jar
```

---

### Istruzioni per la consegna:
Il gruppo dovrà organizzare il proprio repository GitHub oppure preparare un archivio .zip denominato
Gruppo_IS_IDGruppo, contenente le seguenti directory e file:
  1. **Documentation/**: file della documentazione del progetto sia in formato.doc che .pdf, basati sul
template fornito dalla docente.
  2. **VisualParadigm/**: il file .vpp del progetto Visual Paradigm.
  3. **JavaProject/**: la directory contenente il progetto Java con l'implementazione delle funzionalità
richieste e i casi di test.

Per effettuare la consegna, lo studente responsabile del gruppo dovrà rispondere all'attività assegnata su Teams
consegnando 1) una cartella compressa denominata "Gruppo_IS_IDGruppo contenente tutti i file e 2) il link al
repository GitHub in cui è stato sviluppato il progetto.
La consegna dell'elaborato andrà effettuata entro il giorno 16/06/2026.
Per eventuali chiarimenti sulla traccia, è possibile contattare il docente tramite chat di Teams.
