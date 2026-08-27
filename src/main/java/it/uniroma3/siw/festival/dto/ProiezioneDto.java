package it.uniroma3.siw.festival.dto;

import it.uniroma3.siw.festival.model.Proiezione;

// Il DTO rompe i cicli del modello (Film <-> Festival, Film <-> Proiezione) ed espone solo
// cio' che serve al frontend: niente proxy LAZY da serializzare, niente ricorsione infinita.
public record ProiezioneDto(Long id, String data, String ora, String stato,
		Long filmId, String titoloFilm, Integer durataFilm, String nomeSala) {

	public static ProiezioneDto da(Proiezione proiezione) {
		return new ProiezioneDto(
			proiezione.getId(),
			proiezione.getData().toString(),
			proiezione.getOra().toString(),
			proiezione.getStato().name(),
			proiezione.getFilm().getId(),
			proiezione.getFilm().getTitolo(),
			proiezione.getFilm().getDurata(),
			proiezione.getSala().getNome());
	}
}
