package it.uniroma3.siw.festival.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festival.exception.EntitaDuplicataException;
import it.uniroma3.siw.festival.exception.OperazioneNonPermessaException;
import it.uniroma3.siw.festival.model.Film;
import it.uniroma3.siw.festival.repository.FilmRepository;
import it.uniroma3.siw.festival.repository.ProiezioneRepository;

@Service
public class FilmService {

	private final FilmRepository filmRepository;
	private final ProiezioneRepository proiezioneRepository;

	public FilmService(FilmRepository filmRepository, ProiezioneRepository proiezioneRepository) {
		this.filmRepository = filmRepository;
		this.proiezioneRepository = proiezioneRepository;
	}

	@Transactional(readOnly = true)
	public List<Film> findAll() {
		return filmRepository.findAllConRegista();
	}

	@Transactional(readOnly = true)
	public Optional<Film> findById(Long id) {
		return filmRepository.findById(id);
	}

	@Transactional
	public Film save(Film film) {
		boolean duplicato = film.getId() == null
			? filmRepository.existsByTitoloAndAnno(film.getTitolo(), film.getAnno())
			: filmRepository.existsByTitoloAndAnnoAndIdNot(film.getTitolo(), film.getAnno(), film.getId());
		if (duplicato) {
			throw new EntitaDuplicataException(
				"Il film '" + film.getTitolo() + "' (" + film.getAnno() + ") è già presente nel sistema");
		}
		if (film.getId() == null) {
			return filmRepository.save(film);
		}
		// In aggiornamento l'oggetto arriva dal form: e' detached e ha le collezioni a null,
		// perche' il form non le contiene. save(...) su un'entita' con id e' una merge, e la
		// merge di una collezione null la dereferenzia: sparirebbero le recensioni
		// (orphanRemoval) e le righe di film_festival (lato owner della M-N). Si ricarica
		// quindi l'entita' gestita e vi si copiano i soli campi scalari.
		Film esistente = filmRepository.findById(film.getId())
			.orElseThrow(() -> new OperazioneNonPermessaException("Film non trovato"));
		esistente.setTitolo(film.getTitolo());
		esistente.setAnno(film.getAnno());
		esistente.setDurata(film.getDurata());
		esistente.setGenere(film.getGenere());
		esistente.setPaeseProduzione(film.getPaeseProduzione());
		esistente.setRegista(film.getRegista());
		return filmRepository.save(esistente);
	}

	@Transactional
	public void deleteById(Long id) {
		Film film = filmRepository.findById(id)
			.orElseThrow(() -> new OperazioneNonPermessaException("Film non trovato"));
		long numeroProiezioni = proiezioneRepository.countByFilmId(id);
		if (numeroProiezioni > 0) {
			throw new OperazioneNonPermessaException(
				"Impossibile eliminare il film: ha " + numeroProiezioni + " proiezioni programmate");
		}
		filmRepository.delete(film);
	}
}
