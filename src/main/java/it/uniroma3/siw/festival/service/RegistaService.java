package it.uniroma3.siw.festival.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festival.exception.EntitaDuplicataException;
import it.uniroma3.siw.festival.exception.OperazioneNonPermessaException;
import it.uniroma3.siw.festival.model.Regista;
import it.uniroma3.siw.festival.repository.FilmRepository;
import it.uniroma3.siw.festival.repository.RegistaRepository;

@Service
public class RegistaService {

	private final RegistaRepository registaRepository;
	private final FilmRepository filmRepository;

	public RegistaService(RegistaRepository registaRepository, FilmRepository filmRepository) {
		this.registaRepository = registaRepository;
		this.filmRepository = filmRepository;
	}

	@Transactional(readOnly = true)
	public List<Regista> findAll() {
		return registaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<Regista> findById(Long id) {
		return registaRepository.findById(id);
	}

	@Transactional
	public Regista save(Regista regista) {
		boolean duplicato = regista.getId() == null
			? registaRepository.existsByNomeAndCognomeAndDataNascita(
				regista.getNome(), regista.getCognome(), regista.getDataNascita())
			: registaRepository.existsByNomeAndCognomeAndDataNascitaAndIdNot(
				regista.getNome(), regista.getCognome(), regista.getDataNascita(), regista.getId());
		if (duplicato) {
			throw new EntitaDuplicataException(
				"Il regista '" + regista.getNome() + " " + regista.getCognome() + "' è già presente nel sistema");
		}
		return registaRepository.save(regista);
	}

	@Transactional
	public void deleteById(Long id) {
		Regista regista = registaRepository.findById(id)
			.orElseThrow(() -> new OperazioneNonPermessaException("Regista non trovato"));
		long numeroFilm = filmRepository.countByRegistaId(id);
		if (numeroFilm > 0) {
			throw new OperazioneNonPermessaException(
				"Impossibile eliminare il regista: ha " + numeroFilm + " film associati");
		}
		registaRepository.delete(regista);
	}
}
