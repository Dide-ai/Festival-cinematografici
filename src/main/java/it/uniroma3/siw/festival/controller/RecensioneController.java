package it.uniroma3.siw.festival.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.festival.exception.OperazioneNonPermessaException;
import it.uniroma3.siw.festival.model.Recensione;
import it.uniroma3.siw.festival.service.FilmService;
import it.uniroma3.siw.festival.service.RecensioneService;
import jakarta.validation.Valid;

@Controller
public class RecensioneController {

	private final RecensioneService recensioneService;
	private final FilmService filmService;

	public RecensioneController(RecensioneService recensioneService, FilmService filmService) {
		this.recensioneService = recensioneService;
		this.filmService = filmService;
	}

	@PostMapping("/film/{filmId}/recensioni")
	public String crea(@PathVariable Long filmId,
			@Valid @ModelAttribute("recensione") Recensione recensione,
			BindingResult bindingResult, Principal principal, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("film", filmService.findById(filmId)
				.orElseThrow(() -> new OperazioneNonPermessaException("Film non trovato")));
			model.addAttribute("azione", "/film/" + filmId + "/recensioni");
			return "recensioni/form";
		}
		recensioneService.crea(filmId, principal.getName(), recensione);
		return "redirect:/film/" + filmId;
	}

	@GetMapping("/recensioni/{id}/modifica")
	public String formModifica(@PathVariable Long id, Principal principal, Model model) {
		Recensione recensione = recensioneService.findByIdPerModifica(id, principal.getName());
		model.addAttribute("recensione", recensione);
		model.addAttribute("film", recensione.getFilm());
		model.addAttribute("azione", "/recensioni/" + id);
		return "recensioni/form";
	}

	@PostMapping("/recensioni/{id}")
	public String modifica(@PathVariable Long id,
			@Valid @ModelAttribute("recensione") Recensione recensione,
			BindingResult bindingResult, Principal principal, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("film",
				recensioneService.findByIdPerModifica(id, principal.getName()).getFilm());
			model.addAttribute("azione", "/recensioni/" + id);
			return "recensioni/form";
		}
		Recensione salvata = recensioneService.modifica(id, principal.getName(),
			recensione.getTesto(), recensione.getVoto());
		return "redirect:/film/" + salvata.getFilm().getId();
	}

	@PostMapping("/recensioni/{id}/elimina")
	public String elimina(@PathVariable Long id, Principal principal) {
		// serve solo a sapere su quale film tornare: il controllo di appartenenza e in elimina(...)
		Recensione recensione = recensioneService.findById(id)
			.orElseThrow(() -> new OperazioneNonPermessaException("Recensione non trovata"));
		Long filmId = recensione.getFilm().getId();
		recensioneService.elimina(id, principal.getName());
		return "redirect:/film/" + filmId;
	}
}
