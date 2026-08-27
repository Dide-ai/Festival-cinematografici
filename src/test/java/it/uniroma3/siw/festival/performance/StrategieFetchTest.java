package it.uniroma3.siw.festival.performance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import it.uniroma3.siw.festival.model.Festival;
import it.uniroma3.siw.festival.model.Film;
import it.uniroma3.siw.festival.model.Regista;
import it.uniroma3.siw.festival.repository.FestivalRepository;
import it.uniroma3.siw.festival.repository.FilmRepository;
import it.uniroma3.siw.festival.repository.RegistaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

/**
 * Analisi sperimentale dell'accesso ai dati (traccia §8.2).
 *
 * Stesso caso d'uso — caricare tutti i film di un festival con i relativi registi — sugli
 * stessi dati, con tre strategie di fetch diverse. Il numero di query non e' scritto da
 * nessuna parte: lo si legge dalle statistiche di Hibernate.
 *
 * Niente @Transactional sulla classe: ogni misura apre la propria transazione, cosi' la
 * cache di primo livello e i contatori partono puliti e l'N+1 della strategia 1 si vede.
 */
@SpringBootTest
class StrategieFetchTest {

	private static final int NUM_FILM = 100;

	@Autowired
	private FestivalRepository festivalRepository;

	@Autowired
	private FilmRepository filmRepository;

	@Autowired
	private RegistaRepository registaRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Autowired
	private PlatformTransactionManager transactionManager;

	private TransactionTemplate transactionTemplate;
	private Statistics statistics;
	private Long festivalId;

	@BeforeEach
	void preparaIDati() {
		// Spring Boot non espone un bean TransactionTemplate: si costruisce dal gestore
		this.transactionTemplate = new TransactionTemplate(transactionManager);
		this.statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		this.statistics.setStatisticsEnabled(true);

		transactionTemplate.execute(status -> {
			Festival festival = new Festival();
			festival.setNome("Festival Benchmark");
			festival.setAnno(LocalDate.now().getYear());
			festival.setCitta("Roma");
			festival.setDataInizio(LocalDate.of(2026, 1, 1));
			festival.setDataFine(LocalDate.of(2026, 1, 31));
			festival.setDescrizione("Festival generato per misurare le strategie di fetch");
			festival.setFilmPartecipanti(new ArrayList<>());
			festivalRepository.save(festival);

			for (int i = 0; i < NUM_FILM; i++) {
				Regista regista = new Regista();
				regista.setNome("Nome" + i);
				regista.setCognome("Cognome" + i);
				regista.setDataNascita(LocalDate.of(1950, 1, 1).plusDays(i));
				regista.setNazionalita("Italiana");
				registaRepository.save(regista);

				Film film = new Film();
				film.setTitolo("Film " + i);
				film.setAnno(2020);
				film.setDurata(100);
				film.setGenere("Drammatico");
				film.setPaeseProduzione("Italia");
				film.setRegista(regista);
				// Film e' il lato owner della molti-a-molti: e' questa collezione a
				// scrivere le righe di film_festival
				film.setFestivalPartecipati(new ArrayList<>(List.of(festival)));
				filmRepository.save(film);
				// lato inverso, tenuto coerente in memoria
				festival.getFilmPartecipanti().add(film);
			}

			// l'id non e' 1: import.sql gira anche sul database di test
			this.festivalId = festival.getId();
			return null;
		});
	}

	@Test
	void confrontaStrategie() {
		System.out.println();
		System.out.println("=== Test accesso ai film del festival ===");

		misura("Strategia 1: LAZY", id -> festivalRepository.findById(id));
		misura("Strategia 2: JOIN FETCH", id -> festivalRepository.findByIdConFilmERegistiJoinFetch(id));
		misura("Strategia 3: ENTITY GRAPH", id -> festivalRepository.findByIdConFilmERegistiEntityGraph(id));
	}

	/**
	 * Esegue una strategia e ne stampa la misura. Il ciclo che tocca il cognome del regista
	 * e' scritto una volta sola: e' cio' che rende onesto il confronto, perche' le tre
	 * strategie fanno esattamente lo stesso lavoro e cambia solo come caricano i dati.
	 */
	private void misura(String etichetta, Function<Long, Optional<Festival>> strategia) {
		transactionTemplate.execute(status -> {
			entityManager.clear();
			statistics.clear();

			long inizio = System.nanoTime();

			Festival festival = strategia.apply(festivalId).orElseThrow();
			int filmCaricati = 0;
			for (Film film : festival.getFilmPartecipanti()) {
				film.getRegista().getCognome();   // forza il caricamento del regista
				filmCaricati++;
			}

			long millisecondi = (System.nanoTime() - inizio) / 1_000_000;
			long query = statistics.getPrepareStatementCount();

			System.out.println();
			System.out.println(etichetta);
			System.out.println("Film caricati: " + filmCaricati);
			System.out.println("Query SQL: " + query);
			System.out.println("Tempo: " + millisecondi + " ms");
			return null;
		});
	}
}
