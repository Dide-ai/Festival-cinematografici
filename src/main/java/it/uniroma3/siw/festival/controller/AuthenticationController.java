package it.uniroma3.siw.festival.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.festival.model.Credenziali;
import it.uniroma3.siw.festival.model.Utente;
import it.uniroma3.siw.festival.service.CredenzialiService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {

	private final CredenzialiService credenzialiService;

	public AuthenticationController(CredenzialiService credenzialiService) {
		this.credenzialiService = credenzialiService;
	}

	@GetMapping(value = "/register")
	public String showRegisterForm(Model model) {
		model.addAttribute("utente", new Utente());
		model.addAttribute("credenziali", new Credenziali());
		return "authentication/registerUser";
	}

	@GetMapping(value = "/login")
	public String showLoginForm(Model model) {
		return "authentication/login";
	}

	@GetMapping(value = "/admin/index")
	public String index() {
		return "admin/index";
	}

	@PostMapping(value = { "/register" })
	public String registerUser(@Valid @ModelAttribute("utente") Utente utente,
			BindingResult utenteBindingResult, @Valid
			@ModelAttribute("credenziali") Credenziali credenziali,
			BindingResult credenzialiBindingResult) {

		if (!utenteBindingResult.hasErrors() && !credenzialiBindingResult.hasErrors()) {
			credenziali.setUtente(utente);
			credenzialiService.saveCredenziali(credenziali);
			return "redirect:/";
		}
		return "authentication/registerUser";
	}
}
