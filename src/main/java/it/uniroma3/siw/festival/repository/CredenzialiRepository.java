package it.uniroma3.siw.festival.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.festival.model.Credenziali;

public interface CredenzialiRepository extends JpaRepository<Credenziali, Long> {

	Credenziali findByUsername(String username);

}
