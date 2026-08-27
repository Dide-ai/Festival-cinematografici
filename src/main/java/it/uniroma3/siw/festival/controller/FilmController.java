package it.uniroma3.siw.festival.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.festival.model.Credenziali;
import it.uniroma3.siw.festival.model.Film;
import it.uniroma3.siw.festival.model.Recensione;
import it.uniroma3.siw.festival.service.CredenzialiService;
import it.uniroma3.siw.festival.service.FilmService;
import it.uniroma3.siw.festival.service.RecensioneService;
import it.uniroma3.siw.festival.service.RegistaService;
import jakarta.validation.Valid;

@Controller
public class FilmController {

	private final FilmService filmService;
	private final RecensioneService recensioneService;
	private final CredenzialiService credenzialiService;
	private final RegistaService registaService;

	public FilmController(FilmService filmService, RecensioneService recensioneService,
			CredenzialiService credenzialiService, RegistaService registaService) {
		this.filmService = filmService;
		this.recensioneService = recensioneService;
		this.credenzialiService = credenzialiService;
		this.registaService = registaService;
	}

	@GetMapping("/film")
	public String list(Model model) {
		model.addAttribute("elencoFilm", filmService.findAll());
		return "film/list";
	}

	@GetMapping("/film/{id}")
	public String show(@PathVariable Long id, Principal principal, Model model) {
		Optional<Film> optional = filmService.findById(id);
		if (optional.isEmpty()) {
			return "redirect:/film";
		}
		model.addAttribute("film", optional.get());
		model.addAttribute("recensioni", recensioneService.findByFilm(id));
		model.addAttribute("recensione", new Recensione());

		// stato dipendente dall'utente: serve alla vista per mostrare il form di
		// inserimento e i bottoni sulle sole recensioni proprie
		Long utenteId = null;
		boolean giaRecensito = false;
		if (principal != null) {
			Credenziali credenziali = credenzialiService.getCredenziali(principal.getName());
			utenteId = credenziali == null ? null : credenziali.getUtente().getId();
			giaRecensito = recensioneService.haRecensito(id, principal.getName());
		}
		model.addAttribute("utenteId", utenteId);
		model.addAttribute("giaRecensito", giaRecensito);
		return "film/show";
	}

	@GetMapping("/admin/film/nuovo")
	public String createForm(Model model) {
		model.addAttribute("film", new Film());
		model.addAttribute("elencoRegisti", registaService.findAll());
		return "admin/film/form";
	}

	@GetMapping("/admin/film/{id}/modifica")
	public String editForm(@PathVariable Long id, Model model) {
		Optional<Film> optional = filmService.findById(id);
		if (optional.isEmpty()) {
			return "redirect:/film";
		}
		model.addAttribute("film", optional.get());
		model.addAttribute("elencoRegisti", registaService.findAll());
		return "admin/film/form";
	}

	@PostMapping("/admin/film")
	public String save(@Valid @ModelAttribute("film") Film film, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("elencoRegisti", registaService.findAll());
			return "admin/film/form";
		}
		filmService.save(film);
		return "redirect:/film";
	}

	@PostMapping("/admin/film/{id}/elimina")
	public String delete(@PathVariable Long id) {
		filmService.deleteById(id);
		return "redirect:/film";
	}
}
