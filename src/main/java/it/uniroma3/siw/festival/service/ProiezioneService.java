package it.uniroma3.siw.festival.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festival.exception.OperazioneNonPermessaException;
import it.uniroma3.siw.festival.exception.SalaOccupataException;
import it.uniroma3.siw.festival.model.Festival;
import it.uniroma3.siw.festival.model.Film;
import it.uniroma3.siw.festival.model.Proiezione;
import it.uniroma3.siw.festival.model.Sala;
import it.uniroma3.siw.festival.model.StatoProiezione;
import it.uniroma3.siw.festival.repository.FestivalRepository;
import it.uniroma3.siw.festival.repository.FilmRepository;
import it.uniroma3.siw.festival.repository.ProiezioneRepository;
import it.uniroma3.siw.festival.repository.SalaRepository;

@Service
public class ProiezioneService {

	private static final int MINUTI_PAUSA = 30;   // pulizia sala fra due proiezioni

	private final ProiezioneRepository proiezioneRepository;
	private final FestivalRepository festivalRepository;
	private final FilmRepository filmRepository;
	private final SalaRepository salaRepository;

	public ProiezioneService(ProiezioneRepository proiezioneRepository, FestivalRepository festivalRepository,
			FilmRepository filmRepository, SalaRepository salaRepository) {
		this.proiezioneRepository = proiezioneRepository;
		this.festivalRepository = festivalRepository;
		this.filmRepository = filmRepository;
		this.salaRepository = salaRepository;
	}

	@Transactional(readOnly = true)
	public List<Proiezione> findAll() {
		return proiezioneRepository.findAllConFilmSalaEFestival();
	}

	@Transactional(readOnly = true)
	public Optional<Proiezione> findById(Long id) {
		return proiezioneRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public List<Proiezione> programmaDelFestival(Long festivalId) {
		return proiezioneRepository.findByFestivalIdConFilmESala(festivalId);
	}

	// SERIALIZABLE perche' questo metodo e' un check-then-act: al passo 5 legge le
	// proiezioni della sala per decidere se e' libera, al passo 6 ne inserisce una nuova.
	// Con READ_COMMITTED due richieste concorrenti leggerebbero entrambe "sala libera" e
	// inserirebbero due proiezioni sovrapposte (phantom read / write skew): ogni singola
	// transazione sarebbe corretta ma il vincolo di business risulterebbe violato.
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Proiezione programma(Long festivalId, Long filmId, Long salaId,
			LocalDate data, LocalTime ora) {
		// 1. recupero del festival
		Festival festival = festivalRepository.findById(festivalId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Festival non trovato"));
		// 2. recupero del film
		Film film = filmRepository.findById(filmId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Film non trovato"));
		// 3. recupero della sala
		Sala sala = salaRepository.findById(salaId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Sala non trovata"));
		// 4. il film deve partecipare al festival
		if (!film.getFestivalPartecipati().contains(festival)) {
			throw new OperazioneNonPermessaException(
				"Il film '" + film.getTitolo() + "' non partecipa al festival '" + festival.getNome() + "'");
		}
		// 5. verifica disponibilita' della sala: nessuna sovrapposizione temporale
		verificaSalaLibera(sala, data, ora, film.getDurata(), null);
		// 6. creazione e salvataggio della Proiezione con stato SCHEDULED
		Proiezione proiezione = new Proiezione();
		proiezione.setFestival(festival);
		proiezione.setFilm(film);
		proiezione.setSala(sala);
		proiezione.setData(data);
		proiezione.setOra(ora);
		proiezione.setStato(StatoProiezione.SCHEDULED);
		return proiezioneRepository.save(proiezione);
	}

	// Stesso check-then-act di programma(...): stesso livello di isolamento.
	@Transactional(isolation = Isolation.SERIALIZABLE)
	public Proiezione modifica(Long id, Long salaId, LocalDate data, LocalTime ora) {
		Proiezione proiezione = proiezioneRepository.findById(id)
			.orElseThrow(() -> new OperazioneNonPermessaException("Proiezione non trovata"));
		Sala sala = salaRepository.findById(salaId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Sala non trovata"));
		verificaSalaLibera(sala, data, ora, proiezione.getFilm().getDurata(), proiezione.getId());
		proiezione.setSala(sala);
		proiezione.setData(data);
		proiezione.setOra(ora);
		return proiezioneRepository.save(proiezione);
	}

	@Transactional
	public void annulla(Long id) {
		Proiezione proiezione = proiezioneRepository.findById(id)
			.orElseThrow(() -> new OperazioneNonPermessaException("Proiezione non trovata"));
		proiezione.setStato(StatoProiezione.CANCELLED);
		proiezioneRepository.save(proiezione);
	}

	@Transactional
	public void deleteById(Long id) {
		proiezioneRepository.deleteById(id);
	}

	private void verificaSalaLibera(Sala sala, LocalDate data, LocalTime ora, int durata, Long idDaEscludere) {
		LocalDateTime inizio = LocalDateTime.of(data, ora);
		for (Proiezione altra : proiezioneRepository.findBySalaIdAndData(sala.getId(), data)) {
			if (altra.getStato() == StatoProiezione.CANCELLED) {
				continue;   // una proiezione annullata non occupa la sala
			}
			if (altra.getId().equals(idDaEscludere)) {
				continue;   // e' la proiezione che stiamo modificando
			}
			if (siSovrappone(inizio, durata, altra.getInizio(), altra.getFilm().getDurata())) {
				throw new SalaOccupataException(
					"La sala '" + sala.getNome() + "' è già occupata il " + data
					+ " alle " + altra.getOra() + " dalla proiezione di '" + altra.getFilm().getTitolo() + "'");
			}
		}
	}

	private boolean siSovrappone(LocalDateTime inizioA, int durataA,
			LocalDateTime inizioB, int durataB) {
		LocalDateTime fineA = inizioA.plusMinutes(durataA + MINUTI_PAUSA);
		LocalDateTime fineB = inizioB.plusMinutes(durataB + MINUTI_PAUSA);
		return inizioA.isBefore(fineB) && inizioB.isBefore(fineA);
	}
}
