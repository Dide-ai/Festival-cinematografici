package it.uniroma3.siw.festival.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festival.exception.EntitaDuplicataException;
import it.uniroma3.siw.festival.exception.OperazioneNonPermessaException;
import it.uniroma3.siw.festival.model.Credenziali;
import it.uniroma3.siw.festival.model.Film;
import it.uniroma3.siw.festival.model.Recensione;
import it.uniroma3.siw.festival.model.Utente;
import it.uniroma3.siw.festival.repository.FilmRepository;
import it.uniroma3.siw.festival.repository.RecensioneRepository;

@Service
public class RecensioneService {

	private final RecensioneRepository recensioneRepository;
	private final FilmRepository filmRepository;
	private final CredenzialiService credenzialiService;

	public RecensioneService(RecensioneRepository recensioneRepository, FilmRepository filmRepository,
			CredenzialiService credenzialiService) {
		this.recensioneRepository = recensioneRepository;
		this.filmRepository = filmRepository;
		this.credenzialiService = credenzialiService;
	}

	@Transactional(readOnly = true)
	public List<Recensione> findByFilm(Long filmId) {
		return recensioneRepository.findByFilmIdConUtente(filmId);
	}

	@Transactional(readOnly = true)
	public Optional<Recensione> findById(Long id) {
		return recensioneRepository.findById(id);
	}

	// usato da film/show.html per non mostrare il form a chi ha gia' recensito
	@Transactional(readOnly = true)
	public boolean haRecensito(Long filmId, String username) {
		Credenziali credenziali = credenzialiService.getCredenziali(username);
		if (credenziali == null) {
			return false;
		}
		return recensioneRepository.existsByFilmIdAndUtenteId(filmId, credenziali.getUtente().getId());
	}

	// findById + verificaProprietario: cosi' il 403 scatta gia' sulla GET del form di
	// modifica, non solo sulla POST. Il controllo resta nel service.
	@Transactional(readOnly = true)
	public Recensione findByIdPerModifica(Long recensioneId, String username) {
		Recensione recensione = recensioneRepository.findById(recensioneId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Recensione non trovata"));
		verificaProprietario(recensione, username);
		return recensione;
	}

	@Transactional
	public Recensione crea(Long filmId, String username, Recensione recensione) {
		Film film = filmRepository.findById(filmId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Film non trovato"));
		Credenziali credenziali = credenzialiService.getCredenziali(username);
		if (credenziali == null) {
			throw new OperazioneNonPermessaException("Utente non trovato");
		}
		Utente utente = credenziali.getUtente();
		if (recensioneRepository.existsByFilmIdAndUtenteId(film.getId(), utente.getId())) {
			throw new EntitaDuplicataException("Hai già recensito il film '" + film.getTitolo() + "'");
		}
		recensione.setFilm(film);
		recensione.setUtente(utente);
		recensione.setData(LocalDate.now());
		return recensioneRepository.save(recensione);
	}

	@Transactional
	public Recensione modifica(Long recensioneId, String username, String testo, Integer voto) {
		Recensione recensione = recensioneRepository.findById(recensioneId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Recensione non trovata"));
		verificaProprietario(recensione, username);
		recensione.setTesto(testo);
		recensione.setVoto(voto);
		return recensioneRepository.save(recensione);
	}

	@Transactional
	public void elimina(Long recensioneId, String username) {
		Recensione recensione = recensioneRepository.findById(recensioneId)
			.orElseThrow(() -> new OperazioneNonPermessaException("Recensione non trovata"));
		verificaProprietario(recensione, username);
		recensioneRepository.delete(recensione);
	}

	private void verificaProprietario(Recensione recensione, String username) {
		Credenziali credenziali = credenzialiService.getCredenziali(username);
		if (credenziali == null || !recensione.getUtente().getId().equals(credenziali.getUtente().getId())) {
			throw new OperazioneNonPermessaException("Puoi modificare o eliminare solo le tue recensioni");
		}
	}
}
