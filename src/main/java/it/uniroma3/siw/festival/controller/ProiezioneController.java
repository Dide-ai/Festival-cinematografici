package it.uniroma3.siw.festival.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.festival.model.Proiezione;
import it.uniroma3.siw.festival.service.FestivalService;
import it.uniroma3.siw.festival.service.FilmService;
import it.uniroma3.siw.festival.service.ProiezioneService;
import it.uniroma3.siw.festival.service.SalaService;

@Controller
public class ProiezioneController {

	private final ProiezioneService proiezioneService;
	private final FestivalService festivalService;
	private final FilmService filmService;
	private final SalaService salaService;

	public ProiezioneController(ProiezioneService proiezioneService, FestivalService festivalService,
			FilmService filmService, SalaService salaService) {
		this.proiezioneService = proiezioneService;
		this.festivalService = festivalService;
		this.filmService = filmService;
		this.salaService = salaService;
	}

	@GetMapping("/admin/proiezioni")
	public String list(Model model) {
		model.addAttribute("elencoProiezioni", proiezioneService.findAll());
		return "admin/proiezioni/list";
	}

	@GetMapping("/admin/proiezioni/nuova")
	public String createForm(Model model) {
		model.addAttribute("elencoFestival", festivalService.findAll());
		model.addAttribute("elencoFilm", filmService.findAll());
		model.addAttribute("elencoSale", salaService.findAll());
		model.addAttribute("azione", "/admin/proiezioni");
		return "admin/proiezioni/form";
	}

	@GetMapping("/admin/proiezioni/{id}/modifica")
	public String editForm(@PathVariable Long id, Model model) {
		Optional<Proiezione> optional = proiezioneService.findById(id);
		if (optional.isEmpty()) {
			return "redirect:/admin/proiezioni";
		}
		// modifica(...) sposta solo sala, data e ora: film e festival restano quelli
		// scelti alla programmazione e il form li mostra come testo
		model.addAttribute("proiezione", optional.get());
		model.addAttribute("elencoSale", salaService.findAll());
		model.addAttribute("azione", "/admin/proiezioni/" + id);
		return "admin/proiezioni/form";
	}

	@PostMapping("/admin/proiezioni")
	public String programma(@RequestParam Long festivalId, @RequestParam Long filmId,
			@RequestParam Long salaId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime ora) {
		proiezioneService.programma(festivalId, filmId, salaId, data, ora);
		return "redirect:/admin/proiezioni";
	}

	@PostMapping("/admin/proiezioni/{id}")
	public String modifica(@PathVariable Long id, @RequestParam Long salaId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime ora) {
		proiezioneService.modifica(id, salaId, data, ora);
		return "redirect:/admin/proiezioni";
	}

	@PostMapping("/admin/proiezioni/{id}/annulla")
	public String annulla(@PathVariable Long id) {
		proiezioneService.annulla(id);
		return "redirect:/admin/proiezioni";
	}

	@PostMapping("/admin/proiezioni/{id}/elimina")
	public String delete(@PathVariable Long id) {
		proiezioneService.deleteById(id);
		return "redirect:/admin/proiezioni";
	}
}
