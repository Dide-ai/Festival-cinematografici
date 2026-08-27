package it.uniroma3.siw.festival.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festival.exception.EntitaDuplicataException;
import it.uniroma3.siw.festival.exception.OperazioneNonPermessaException;
import it.uniroma3.siw.festival.model.Festival;
import it.uniroma3.siw.festival.model.Film;
import it.uniroma3.siw.festival.repository.FestivalRepository;
import it.uniroma3.siw.festival.repository.FilmRepository;
import it.uniroma3.siw.festival.repository.ProiezioneRepository;

@Service
public class FestivalService {

	private final FestivalRepository festivalRepository;
	private final FilmRepository filmRepository;
	private final ProiezioneRepository proiezioneRepository;

	public FestivalService(FestivalRepository festivalRepository, FilmRepository filmRepository,
			ProiezioneRepository proiezioneRepository) {
		this.festivalRepository = festivalRepository;
		this.filmRepository = filmRepository;
		this.proiezioneRepository = proiezioneRepository;
	}

	@Transactional(readOnly = true)
	public List<Festival> findAll() {
		return festivalRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<Festival> findById(Long id) {
		return festivalRepository.findById(id);
	}

	@Transactional
	public Festival save(Festival festival) {
		if (festival.getDataFine().isBefore(festival.getDataInizio())) {
			throw new OperazioneNonPermessaException(
				"La data di fine non può precedere la data di inizio");
		}
		boolean duplicato = festival.getId() == null
			? festivalRepository.existsByNomeAndAnno(festival.getNome(), festival.getAnno())
			: festivalRepository.existsByNomeAndAnnoAndIdNot(festival.getNome(), festival.getAnno(), festival.getId());
		if (duplicato) {
			throw new EntitaDuplicataException(
				"Il festival '" + festival.getNome() + "' (" + festival.getAnno() + ") è già presente nel sistema");
		}
		if (festival.getId() == null) {
			return festivalRepository.save(festival);
		}
		// Stesso motivo di FilmService.save: la merge dell'oggetto detached arrivato dal
		// form dereferenzierebbe la collezione proiezioni (orphanRemoval), cancellando
		// l'intero programma del festival. Si copiano i soli campi scalari.
		Festival esistente = festivalRepository.findById(festival.getId())
			.orElseThrow(() -> new OperazioneNonPermessaException("Festival non trovato"));
		esistente.setNome(festival.getNome());
		esistente.setAnno(festival.getAnno());
		esistente.setCitta(festival.getCitta());
		esistente.setDataInizio(festival.getDataInizio());
		esistente.setDataFine(festival.getDataFine());
		esistente.setDescrizione(festival.getDescrizione());
		return festivalRepository.save(esistente);
	}

	@Transactional
	public void deleteById(Long id) {
		Festival festival = festivalRepository.findById(id)
			.orElseThrow(() -> new OperazioneNonPermessaException("Festival non trovato"));
		long numeroProiezioni = proiezioneRepository.countByFestivalId(id);
		if (numeroProiezioni > 0) {
			throw new OperazioneNonPermessaException(
				"Impossibile eliminare il festival: ha " + numeroProiezioni + " proiezioni programmate");
		}
		// Festival e' il lato inverso della molti-a-molti: Hibernate non ripulisce
		// film_festival cancellando da qui, va svuotato il lato owner.
		for (Film film : festival.getFilmPartecipanti()) {
			film.getFestivalPartecipati().remove(festival);
		}
		festivalRepository.delete(festival);
	}

	@Transactional
	public void aggiungiFilm(Long festivalId, Long filmId) {
		Festival festival = festivalRepository.findById(festivalId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Festival non trovato"));
		Film film = filmRepository.findById(filmId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Film non trovato"));
		if (!film.getFestivalPartecipati().contains(festival)) {
			film.getFestivalPartecipati().add(festival);
			festival.getFilmPartecipanti().add(film);
		}
	}

	@Transactional
	public void rimuoviFilm(Long festivalId, Long filmId) {
		Festival festival = festivalRepository.findById(festivalId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Festival non trovato"));
		Film film = filmRepository.findById(filmId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Film non trovato"));
		long numeroProiezioni = proiezioneRepository.countByFestivalIdAndFilmId(festivalId, filmId);
		if (numeroProiezioni > 0) {
			throw new OperazioneNonPermessaException(
				"Impossibile rimuovere il film dal festival: ha " + numeroProiezioni + " proiezioni programmate");
		}
		film.getFestivalPartecipati().remove(festival);
		festival.getFilmPartecipanti().remove(film);
	}
}
