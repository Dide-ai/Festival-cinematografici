package it.uniroma3.siw.festival.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import it.uniroma3.siw.festival.model.Proiezione;

public interface ProiezioneRepository extends JpaRepository<Proiezione, Long> {

	// controllo di sovrapposizione: il join fetch su film serve perche' chi chiama legge
	// subito altra.getFilm().getDurata() su ogni riga (senza, e' un N+1)
	@Query("select p from Proiezione p join fetch p.film "
	     + "where p.sala.id = :salaId and p.data = :data")
	List<Proiezione> findBySalaIdAndData(@Param("salaId") Long salaId, @Param("data") LocalDate data);

	// dipendenze prima di cancellare: query di conteggio, NON collezioni caricate in memoria
	long countBySalaId(Long salaId);

	long countByFilmId(Long filmId);

	long countByFestivalId(Long festivalId);

	long countByFestivalIdAndFilmId(Long festivalId, Long filmId);

	@Query("select p from Proiezione p join fetch p.film join fetch p.sala "
	     + "where p.festival.id = :id order by p.data, p.ora")
	List<Proiezione> findByFestivalIdConFilmESala(@Param("id") Long festivalId);

	@Query("select p from Proiezione p join fetch p.film join fetch p.sala join fetch p.festival "
	     + "order by p.data, p.ora")
	List<Proiezione> findAllConFilmSalaEFestival();

}
