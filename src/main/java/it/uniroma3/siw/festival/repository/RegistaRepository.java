package it.uniroma3.siw.festival.repository;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.festival.model.Regista;

public interface RegistaRepository extends JpaRepository<Regista, Long> {

	boolean existsByNomeAndCognomeAndDataNascita(String nome, String cognome, LocalDate dataNascita);

	boolean existsByNomeAndCognomeAndDataNascitaAndIdNot(String nome, String cognome,
			LocalDate dataNascita, Long id);

}
