package it.uniroma3.siw.festival.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.uniroma3.siw.festival.model.Sala;

public interface SalaRepository extends JpaRepository<Sala, Long> {

	boolean existsByNome(String nome);

	boolean existsByNomeAndIdNot(String nome, Long id);

}
