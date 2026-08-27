package it.uniroma3.siw.festival.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import it.uniroma3.siw.festival.model.Credenziali;

@ControllerAdvice
public class GlobalController {

	@ModelAttribute("userDetails")
	public UserDetails getUtente() {
		UserDetails utente = null;

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
			utente = (UserDetails) authentication.getPrincipal();
		}
		return utente;
	}

	// evita la dipendenza thymeleaf-extras-springsecurity6: i template usano ${isAdmin}
	@ModelAttribute("isAdmin")
	public boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return false;
		}
		for (GrantedAuthority authority : authentication.getAuthorities()) {
			if (Credenziali.RUOLO_ADMIN.equals(authority.getAuthority())) {
				return true;
			}
		}
		return false;
	}
}
