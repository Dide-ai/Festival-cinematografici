package it.uniroma3.siw.festival.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festival.exception.EntitaDuplicataException;
import it.uniroma3.siw.festival.exception.OperazioneNonPermessaException;
import it.uniroma3.siw.festival.model.Sala;
import it.uniroma3.siw.festival.repository.ProiezioneRepository;
import it.uniroma3.siw.festival.repository.SalaRepository;

@Service
public class SalaService {

	private final SalaRepository salaRepository;
	private final ProiezioneRepository proiezioneRepository;

	public SalaService(SalaRepository salaRepository, ProiezioneRepository proiezioneRepository) {
		this.salaRepository = salaRepository;
		this.proiezioneRepository = proiezioneRepository;
	}

	@Transactional(readOnly = true)
	public List<Sala> findAll() {
		return salaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Optional<Sala> findById(Long id) {
		return salaRepository.findById(id);
	}

	@Transactional
	public Sala save(Sala sala) {
		boolean duplicato = sala.getId() == null
			? salaRepository.existsByNome(sala.getNome())
			: salaRepository.existsByNomeAndIdNot(sala.getNome(), sala.getId());
		if (duplicato) {
			throw new EntitaDuplicataException("La sala '" + sala.getNome() + "' è già presente nel sistema");
		}
		return salaRepository.save(sala);
	}

	@Transactional
	public void deleteById(Long id) {
		Sala sala = salaRepository.findById(id)
			.orElseThrow(() -> new OperazioneNonPermessaException("Sala non trovata"));
		long numeroProiezioni = proiezioneRepository.countBySalaId(id);
		if (numeroProiezioni > 0) {
			throw new OperazioneNonPermessaException(
				"Impossibile eliminare la sala: ha " + numeroProiezioni + " proiezioni programmate");
		}
		salaRepository.delete(sala);
	}
}
