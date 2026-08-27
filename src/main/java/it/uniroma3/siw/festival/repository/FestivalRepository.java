package it.uniroma3.siw.festival.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festival.model.Festival;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

	boolean existsByNomeAndAnno(String nome, Integer anno);

	boolean existsByNomeAndAnnoAndIdNot(String nome, Integer anno, Long id);

	// --- usati SOLO dallo script di analisi delle strategie di fetch ---

	@Query("select distinct f from Festival f "
	     + "left join fetch f.filmPartecipanti film left join fetch film.regista "
	     + "where f.id = :id")
	Optional<Festival> findByIdConFilmERegistiJoinFetch(@Param("id") Long id);

	@EntityGraph(attributePaths = { "filmPartecipanti", "filmPartecipanti.regista" })
	@Query("select f from Festival f where f.id = :id")
	Optional<Festival> findByIdConFilmERegistiEntityGraph(@Param("id") Long id);

}
