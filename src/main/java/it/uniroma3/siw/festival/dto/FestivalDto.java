package it.uniroma3.siw.festival.dto;

import it.uniroma3.siw.festival.model.Festival;

// Date come stringhe ISO: nessuna configurazione Jackson, e il frontend le usa cosi' come sono.
public record FestivalDto(Long id, String nome, Integer anno, String citta,
		String dataInizio, String dataFine, String descrizione) {

	public static FestivalDto da(Festival festival) {
		return new FestivalDto(
			festival.getId(),
			festival.getNome(),
			festival.getAnno(),
			festival.getCitta(),
			festival.getDataInizio().toString(),
			festival.getDataFine().toString(),
			festival.getDescrizione());
	}
}
