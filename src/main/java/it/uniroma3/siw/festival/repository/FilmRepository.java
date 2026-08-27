package it.uniroma3.siw.festival.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import it.uniroma3.siw.festival.model.Film;

public interface FilmRepository extends JpaRepository<Film, Long> {

	boolean existsByTitoloAndAnno(String titolo, Integer anno);

	boolean existsByTitoloAndAnnoAndIdNot(String titolo, Integer anno, Long id);

	@Query("select f from Film f join fetch f.regista order by f.titolo")
	List<Film> findAllConRegista();

	long countByRegistaId(Long registaId);   // dipendenze prima di cancellare un regista

}
