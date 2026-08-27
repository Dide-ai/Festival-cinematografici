package it.uniroma3.siw.festival.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.festival.model.Sala;
import it.uniroma3.siw.festival.service.SalaService;
import jakarta.validation.Valid;

@Controller
public class SalaController {

	private final SalaService salaService;

	public SalaController(SalaService salaService) {
		this.salaService = salaService;
	}

	@GetMapping("/admin/sale")
	public String list(Model model) {
		model.addAttribute("elencoSale", salaService.findAll());
		return "admin/sale/list";
	}

	@GetMapping("/admin/sale/nuova")
	public String createForm(Model model) {
		model.addAttribute("sala", new Sala());
		return "admin/sale/form";
	}

	@GetMapping("/admin/sale/{id}/modifica")
	public String editForm(@PathVariable Long id, Model model) {
		Optional<Sala> optional = salaService.findById(id);
		if (optional.isEmpty()) {
			return "redirect:/admin/sale";
		}
		model.addAttribute("sala", optional.get());
		return "admin/sale/form";
	}

	@PostMapping("/admin/sale")
	public String save(@Valid @ModelAttribute("sala") Sala sala, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "admin/sale/form";
		}
		salaService.save(sala);
		return "redirect:/admin/sale";
	}

	@PostMapping("/admin/sale/{id}/elimina")
	public String delete(@PathVariable Long id) {
		salaService.deleteById(id);
		return "redirect:/admin/sale";
	}
}
