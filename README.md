# SiwFestival

Sistema informativo su Web per la gestione di festival cinematografici.
Progetto assegnato dal docente, appello di Settembre 2026 — Sistemi Informativi su Web.

Spring Boot 4 · JPA/Hibernate · PostgreSQL · Spring Security · Thymeleaf · React

---

## Prerequisiti

- **Java 17+** (il progetto compila con `<java.version>17</java.version>`)
- **PostgreSQL** in ascolto su `localhost:5432`, utente `postgres` con password `postgres`
- **Node.js** — serve **solo** per ricompilare il frontend React. Non serve per eseguire il
  progetto: il bundle è già committato in [`src/main/resources/static/react/`](src/main/resources/static/react/)

Maven non va installato: si usa il wrapper `./mvnw` (`mvnw.cmd` su Windows).

## Creazione dei database

Servono due database: uno di sviluppo e uno usato solo dallo script di analisi.

```bash
createdb -U postgres siwfestival
```

```bash
createdb -U postgres siwfestival_test
```

Su Windows `createdb` si trova in `C:\Program Files\PostgreSQL\18\bin`.

Le credenziali sono in [`src/main/resources/application.properties`](src/main/resources/application.properties).
Lo schema si crea da solo: `ddl-auto=create` ricostruisce le tabelle a ogni avvio e
[`import.sql`](src/main/resources/import.sql) carica i dati di esempio
(2 festival, 5 registi, 8 film, 3 sale, 8 proiezioni, 3 recensioni).

## Avvio

```bash
./mvnw spring-boot:run
```

L'applicazione risponde su <http://localhost:8080>.

### Utenze di prova

| username | password | ruolo | cosa può fare |
|---|---|---|---|
| `paolo` | `paolo` | `ADMIN` | tutta l'amministrazione, da `/admin/index` |
| `mario` | `mario` | `DEFAULT` | scrivere, modificare ed eliminare le proprie recensioni |

Ci si registra anche da `/register`; i nuovi utenti nascono con ruolo `DEFAULT`.

## Script di analisi dell'accesso ai dati

Confronta tre strategie di fetch sullo stesso caso d'uso — caricare tutti i film di un
festival con i relativi registi — sugli stessi dati.

```bash
./mvnw test -Dtest=StrategieFetchTest
```

Lavora sul database `siwfestival_test` (`create-drop`), quindi non tocca i dati di
sviluppo. Il numero di film è la costante `NUM_FILM` in cima a
[`StrategieFetchTest.java`](src/test/java/it/uniroma3/siw/festival/performance/StrategieFetchTest.java):
cambiandola e rieseguendo si vede come scala il problema.

Output ottenuto con `NUM_FILM = 100`:

```
=== Test accesso ai film del festival ===

Strategia 1: LAZY
Film caricati: 100
Query SQL: 102
Tempo: 105 ms

Strategia 2: JOIN FETCH
Film caricati: 100
Query SQL: 1
Tempo: 81 ms

Strategia 3: ENTITY GRAPH
Film caricati: 100
Query SQL: 1
Tempo: 30 ms
```

Le **102** query della prima strategia sono il problema N+1: 1 per il festival, 1 per
inizializzare la collezione dei film, e **una per ogni regista**. Le altre due strategie
caricano tutto con una sola query. I conteggi sono stabili fra esecuzioni; i tempi no, ed è
sul numero di query che va fatto il ragionamento.

## Ricompilare il frontend React

**Non è necessario per eseguire il progetto**: il bundle prodotto è già nel repository.
Serve solo se si modificano i sorgenti in [`frontend/src/`](frontend/src/).

```bash
cd frontend && npm install && npm run build
```

La build scrive `programma.js` in `src/main/resources/static/react/`, con nome fisso senza
hash così la pagina Thymeleaf può referenziarlo stabilmente.

> **L'ordine conta.** `spring-boot:run` copia `src/main/resources` in `target/classes`
> all'avvio: se si ricompila il frontend con l'applicazione già in esecuzione, viene servito
> il bundle vecchio. Prima la build, poi l'avvio.

---

## Copertura dei requisiti

| Sezione della traccia | Dove |
|---|---|
| **§4.1** Funzionalità pubbliche | [`FestivalController`](src/main/java/it/uniroma3/siw/festival/controller/FestivalController.java), [`FilmController`](src/main/java/it/uniroma3/siw/festival/controller/FilmController.java), [`RegistaController`](src/main/java/it/uniroma3/siw/festival/controller/RegistaController.java) — viste in [`templates/festival/`](src/main/resources/templates/festival/), [`templates/film/`](src/main/resources/templates/film/), [`templates/registi/`](src/main/resources/templates/registi/) |
| **§4.2** Utenti registrati | [`RecensioneController`](src/main/java/it/uniroma3/siw/festival/controller/RecensioneController.java), [`RecensioneService`](src/main/java/it/uniroma3/siw/festival/service/RecensioneService.java) — form in [`templates/recensioni/form.html`](src/main/resources/templates/recensioni/form.html) |
| **§4.3** Amministrazione | rotte `/admin/**` nei cinque controller di dominio, più [`SalaController`](src/main/java/it/uniroma3/siw/festival/controller/SalaController.java) e [`ProiezioneController`](src/main/java/it/uniroma3/siw/festival/controller/ProiezioneController.java) — viste in [`templates/admin/`](src/main/resources/templates/admin/) |
| **§5** Sicurezza | [`SecurityConfiguration`](src/main/java/it/uniroma3/siw/festival/authentication/SecurityConfiguration.java), [`GlobalController`](src/main/java/it/uniroma3/siw/festival/controller/GlobalController.java), proprietà delle recensioni in [`RecensioneService.verificaProprietario`](src/main/java/it/uniroma3/siw/festival/service/RecensioneService.java) |
| **§6** Architettura a livelli | [`model/`](src/main/java/it/uniroma3/siw/festival/model/) e [`repository/`](src/main/java/it/uniroma3/siw/festival/repository/) (persistenza), [`service/`](src/main/java/it/uniroma3/siw/festival/service/) (casi d'uso), [`controller/`](src/main/java/it/uniroma3/siw/festival/controller/) (HTTP) |
| **§7** Transazioni | `@Transactional` in tutti i service; il caso d'uso multi-entità è [`ProiezioneService.programma`](src/main/java/it/uniroma3/siw/festival/service/ProiezioneService.java) |
| **§8** Prestazioni e fetch | `fetch = LAZY` esplicito in [`model/`](src/main/java/it/uniroma3/siw/festival/model/), `join fetch` e `@EntityGraph` in [`repository/`](src/main/java/it/uniroma3/siw/festival/repository/), analisi in [`StrategieFetchTest`](src/test/java/it/uniroma3/siw/festival/performance/StrategieFetchTest.java) |
| **§9** Frontend React | [`frontend/src/ProgrammaProiezioni.tsx`](frontend/src/ProgrammaProiezioni.tsx), innestato da [`templates/festival/programma.html`](src/main/resources/templates/festival/programma.html) |
| **§10** API REST | [`RestFestivalController`](src/main/java/it/uniroma3/siw/festival/controller/rest/RestFestivalController.java), DTO in [`dto/`](src/main/java/it/uniroma3/siw/festival/dto/) |
| **§11** Requisiti minimi | 8 entità; 1-N, N-1 e la molti-a-molti `Film ↔ Festival`; 7 casi d'uso pubblici, 3 per utenti registrati, 10 di amministrazione; validazione Jakarta più [`@AnnoNonFuturo`](src/main/java/it/uniroma3/siw/festival/validation/AnnoNonFuturo.java); errori in [`GlobalExceptionHandler`](src/main/java/it/uniroma3/siw/festival/exception/GlobalExceptionHandler.java) |

### Endpoint REST

| Metodo | URL | Risposta |
|---|---|---|
| GET | `/api/festival` | `200` + elenco dei festival |
| GET | `/api/festival/{id}` | `200` + festival, `404` se non esiste |
| GET | `/api/festival/{id}/proiezioni` | `200` + programma, `404` se il festival non esiste |

Consumati da `ProgrammaProiezioni.tsx` sulla pagina `/festival/{id}/programma`.

---

## Scelte progettuali

**Architettura a livelli.** Il Controller Layer riceve la richiesta HTTP, valida l'input con
Jakarta Validation e delega; il Service Layer contiene tutta la logica di business e apre le
transazioni; il Persistence Layer sono entità e repository. Nei controller non c'è nessuna
regola di dominio: anche il controllo "puoi modificare solo le tue recensioni" vive nel
service, perché è una regola di business e non di presentazione.

**LAZY ovunque.** Tutte le `@ManyToOne` sono dichiarate esplicitamente `fetch = LAZY`, contro
il default JPA che è EAGER. EAGER genera join non richiesti a ogni caricamento e rende
imprevedibile il numero di query. Con LAZY il caricamento si decide caso d'uso per caso
d'uso, con `join fetch` o `@EntityGraph` nel repository dove serve davvero — ed è quello che
lo script del §8.2 rende misurabile.

**`SERIALIZABLE` su `programma(...)`.** Programmare una proiezione è un *check-then-act*:
si legge lo stato della sala per decidere se è libera, poi si inserisce. Con `READ_COMMITTED`
due richieste concorrenti possono leggere entrambe "sala libera" e inserire due proiezioni
sovrapposte — ogni transazione corretta da sola, vincolo di business violato. `SERIALIZABLE`
è l'unico livello che in PostgreSQL lo impedisce. Alternative possibili: vincolo di
esclusione sul database, oppure lock pessimistico sulla sala.

**DTO nelle API REST.** Il modello ha cicli bidirezionali (`Film ↔ Festival`,
`Film ↔ Proiezione`) che manderebbero Jackson in ricorsione infinita, e i proxy LAZY
farebbero fallire la serializzazione. I due `record` in `dto/` rompono i cicli, disaccoppiano
l'API dal modello ed espongono solo i campi che servono al frontend. È l'alternativa pulita a
`@JsonIgnore` e `@JsonIdentityInfo`.

**`Utente` e `Credenziali` separate.** I dati anagrafici servono al dominio — `Recensione`
punta a `Utente` — mentre username, password e ruolo servono solo a Spring Security, che li
legge via `JdbcUserDetailsManager`. Tenerli distinti evita di mescolare identità di dominio e
identità di accesso. `Credenziali` copre comunque tutti gli attributi minimi che la traccia
chiede per `Utente`.

**`open-in-view` attivo in produzione, disattivo nei test.** In produzione resta a `true`
(scritto esplicitamente, non lasciato al default) perché evita `LazyInitializationException`
durante il rendering Thymeleaf. Ha però un costo: maschera le query N+1, perché la sessione
resta aperta fino alla vista. Nei test è a `false` proprio per non nascondere ciò che lo
script deve mostrare.

**`FilmService.save` e `FestivalService.save` ricaricano l'entità.** L'oggetto che arriva da
un form è *detached* e ha le collezioni a `null`, perché il form non le contiene. Passarlo a
`repository.save(...)` sarebbe una `merge`, e la merge di una collezione `null` la
dereferenzia: dove c'è `orphanRemoval = true` o il lato owner di una molti-a-molti, questo
**cancella righe dal database** — le recensioni del film, le righe di `film_festival`, le
proiezioni del festival. Per questo i due metodi ricaricano l'entità gestita e vi copiano i
soli campi scalari. `RegistaService` e `SalaService` non ne hanno bisogno: le loro collezioni
sono lati inversi senza cascade.
