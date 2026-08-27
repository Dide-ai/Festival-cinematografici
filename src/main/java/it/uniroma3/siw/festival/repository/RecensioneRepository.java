package it.uniroma3.siw.festival.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festival.model.Recensione;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

	boolean existsByFilmIdAndUtenteId(Long filmId, Long utenteId);

	@Query("select r from Recensione r join fetch r.utente where r.film.id = :id order by r.data desc")
	List<Recensione> findByFilmIdConUtente(@Param("id") Long filmId);

}
