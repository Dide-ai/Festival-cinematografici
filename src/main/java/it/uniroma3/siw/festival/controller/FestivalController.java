package it.uniroma3.siw.festival.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import it.uniroma3.siw.festival.model.Festival;
import it.uniroma3.siw.festival.service.FestivalService;
import it.uniroma3.siw.festival.service.FilmService;
import it.uniroma3.siw.festival.service.ProiezioneService;
import jakarta.validation.Valid;

@Controller
public class FestivalController {

	private final FestivalService festivalService;
	private final ProiezioneService proiezioneService;
	private final FilmService filmService;

	public FestivalController(FestivalService festivalService, ProiezioneService proiezioneService,
			FilmService filmService) {
		this.festivalService = festivalService;
		this.proiezioneService = proiezioneService;
		this.filmService = filmService;
	}

	@GetMapping("/festival")
	public String list(Model model) {
		model.addAttribute("elencoFestival", festivalService.findAll());
		return "festival/list";
	}

	@GetMapping("/festival/{id}")
	public String show(@PathVariable Long id, Model model) {
		Optional<Festival> optional = festivalService.findById(id);
		if (optional.isEmpty()) {
			return "redirect:/festival";
		}
		model.addAttribute("festival", optional.get());
		model.addAttribute("proiezioni", proiezioneService.programmaDelFestival(id));
		model.addAttribute("elencoFilm", filmService.findAll());
		return "festival/show";
	}

	@GetMapping("/festival/{id}/programma")
	public String programma(@PathVariable Long id, Model model) {
		Optional<Festival> optional = festivalService.findById(id);
		if (optional.isEmpty()) {
			return "redirect:/festival";
		}
		model.addAttribute("festival", optional.get());
		return "festival/programma";
	}

	@GetMapping("/admin/festival/nuovo")
	public String createForm(Model model) {
		model.addAttribute("festival", new Festival());
		return "admin/festival/form";
	}

	@GetMapping("/admin/festival/{id}/modifica")
	public String editForm(@PathVariable Long id, Model model) {
		Optional<Festival> optional = festivalService.findById(id);
		if (optional.isEmpty()) {
			return "redirect:/festival";
		}
		model.addAttribute("festival", optional.get());
		return "admin/festival/form";
	}

	@PostMapping("/admin/festival")
	public String save(@Valid @ModelAttribute("festival") Festival festival, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "admin/festival/form";
		}
		festivalService.save(festival);
		return "redirect:/festival";
	}

	@PostMapping("/admin/festival/{id}/elimina")
	public String delete(@PathVariable Long id) {
		festivalService.deleteById(id);
		return "redirect:/festival";
	}

	@PostMapping("/admin/festival/{id}/film")
	public String aggiungiFilm(@PathVariable Long id, @RequestParam Long filmId) {
		festivalService.aggiungiFilm(id, filmId);
		return "redirect:/festival/" + id;
	}

	@PostMapping("/admin/festival/{id}/film/{filmId}/elimina")
	public String rimuoviFilm(@PathVariable Long id, @PathVariable Long filmId) {
		festivalService.rimuoviFilm(id, filmId);
		return "redirect:/festival/" + id;
	}
}
