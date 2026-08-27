package it.uniroma3.siw.festival.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(NoResourceFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNoResourceFound(NoResourceFoundException e, Model model) {
		model.addAttribute("errorMessage", "Pagina non trovata");
		return "error/errore";
	}

	@ExceptionHandler(OperazioneNonPermessaException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public String handleOperazioneNonPermessa(OperazioneNonPermessaException e, Model model) {
		model.addAttribute("errorMessage", e.getMessage());
		return "error/errore";
	}

	@ExceptionHandler({ SalaOccupataException.class, EntitaDuplicataException.class })
	@ResponseStatus(HttpStatus.CONFLICT)
	public String handleConflitto(RuntimeException e, Model model) {
		model.addAttribute("errorMessage", e.getMessage());
		return "error/errore";
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public String handleGenericException(Exception e, Model model) {
		// senza questa riga il gestore inghiotte lo stack trace e ogni bug
		// diventa una pagina 500 muta
		logger.error("Errore non gestito", e);
		model.addAttribute("errorMessage", "Si è verificato un errore interno.");
		return "error/errore";
	}
}
