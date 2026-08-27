package it.uniroma3.siw.festival.controller.rest;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.uniroma3.siw.festival.dto.FestivalDto;
import it.uniroma3.siw.festival.dto.ProiezioneDto;
import it.uniroma3.siw.festival.service.FestivalService;
import it.uniroma3.siw.festival.service.ProiezioneService;

@RestController
@RequestMapping("/api/festival")
public class RestFestivalController {

	private final FestivalService festivalService;
	private final ProiezioneService proiezioneService;

	public RestFestivalController(FestivalService festivalService, ProiezioneService proiezioneService) {
		this.festivalService = festivalService;
		this.proiezioneService = proiezioneService;
	}

	@GetMapping
	public ResponseEntity<List<FestivalDto>> findAll() {
		return ResponseEntity.ok(festivalService.findAll().stream().map(FestivalDto::da).toList());
	}

	@GetMapping("/{id}")
	public ResponseEntity<FestivalDto> findById(@PathVariable Long id) {
		return festivalService.findById(id)
			.map(FestivalDto::da)
			.map(ResponseEntity::ok)
			.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/{id}/proiezioni")
	public ResponseEntity<List<ProiezioneDto>> proiezioniDelFestival(@PathVariable Long id) {
		// l'esistenza del festival va verificata prima: programmaDelFestival(...) su un id
		// inesistente restituisce una lista vuota, che sarebbe un 200 sbagliato
		if (festivalService.findById(id).isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		// programmaDelFestival usa una query con join fetch su film e sala: senza,
		// ProiezioneDto.da(...) leggerebbe titolo e nome sala con una query per riga
		return ResponseEntity.ok(
			proiezioneService.programmaDelFestival(id).stream().map(ProiezioneDto::da).toList());
	}
}
