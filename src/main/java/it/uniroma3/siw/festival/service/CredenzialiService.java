package it.uniroma3.siw.festival.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.festival.model.Credenziali;
import it.uniroma3.siw.festival.repository.CredenzialiRepository;

@Service
public class CredenzialiService {

	private final PasswordEncoder passwordEncoder;
	private final CredenzialiRepository credenzialiRepository;

	public CredenzialiService(PasswordEncoder passwordEncoder, CredenzialiRepository credenzialiRepository) {
		this.passwordEncoder = passwordEncoder;
		this.credenzialiRepository = credenzialiRepository;
	}

	@Transactional(readOnly = true)
	public Credenziali getCredenziali(Long id) {
		Optional<Credenziali> result = this.credenzialiRepository.findById(id);
		return result.orElse(null);
	}

	@Transactional(readOnly = true)
	public Credenziali getCredenziali(String username) {
		Optional<Credenziali> result = Optional.ofNullable(this.credenzialiRepository.findByUsername(username));
		return result.orElse(null);
	}

	@Transactional
	public Credenziali saveCredenziali(Credenziali credenziali) {
		credenziali.setRuolo(Credenziali.RUOLO_DEFAULT);
		credenziali.setPassword(this.passwordEncoder.encode(credenziali.getPassword()));
		return this.credenzialiRepository.save(credenziali);
	}
}
