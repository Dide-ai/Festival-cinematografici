package it.uniroma3.siw.festival.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.festival.model.Regista;
import it.uniroma3.siw.festival.service.RegistaService;
import jakarta.validation.Valid;

@Controller
public class RegistaController {

	private final RegistaService registaService;

	public RegistaController(RegistaService registaService) {
		this.registaService = registaService;
	}

	@GetMapping("/registi")
	public String list(Model model) {
		model.addAttribute("elencoRegisti", registaService.findAll());
		return "registi/list";
	}

	@GetMapping("/registi/{id}")
	public String show(@PathVariable Long id, Model model) {
		Optional<Regista> optional = registaService.findById(id);
		if (optional.isEmpty()) {
			return "redirect:/registi";
		}
		model.addAttribute("regista", optional.get());
		return "registi/show";
	}

	@GetMapping("/admin/registi/nuovo")
	public String createForm(Model model) {
		model.addAttribute("regista", new Regista());
		return "admin/registi/form";
	}

	@GetMapping("/admin/registi/{id}/modifica")
	public String editForm(@PathVariable Long id, Model model) {
		Optional<Regista> optional = registaService.findById(id);
		if (optional.isEmpty()) {
			return "redirect:/registi";
		}
		model.addAttribute("regista", optional.get());
		return "admin/registi/form";
	}

	@PostMapping("/admin/registi")
	public String save(@Valid @ModelAttribute("regista") Regista regista, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "admin/registi/form";
		}
		registaService.save(regista);
		return "redirect:/registi";
	}

	@PostMapping("/admin/registi/{id}/elimina")
	public String delete(@PathVariable Long id) {
		registaService.deleteById(id);
		return "redirect:/registi";
	}
}
