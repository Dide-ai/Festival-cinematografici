package it.uniroma3.siw.festival.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
public class Festival {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String nome;

	// niente @AnnoNonFuturo: l'anno di un'edizione di festival si programma in anticipo.
	// Il vincolo resta su Film.anno, che e' l'anno di produzione e non puo' essere futuro.
	@NotNull
	@Min(1895)
	@Column(nullable = false)
	private Integer anno;

	@NotBlank
	@Column(nullable = false)
	private String citta;

	@NotNull
	@Column(nullable = false)
	private LocalDate dataInizio;

	@NotNull
	@Column(nullable = false)
	private LocalDate dataFine;

	@Size(max = 2048)
	@Column(length = 2048)
	private String descrizione;

	@ManyToMany(mappedBy = "festivalPartecipati", fetch = FetchType.LAZY)
	private List<Film> filmPartecipanti;

	@OneToMany(mappedBy = "festival", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Proiezione> proiezioni;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}

	public LocalDate getDataInizio() {
		return dataInizio;
	}

	public void setDataInizio(LocalDate dataInizio) {
		this.dataInizio = dataInizio;
	}

	public LocalDate getDataFine() {
		return dataFine;
	}

	public void setDataFine(LocalDate dataFine) {
		this.dataFine = dataFine;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public List<Film> getFilmPartecipanti() {
		return filmPartecipanti;
	}

	public void setFilmPartecipanti(List<Film> filmPartecipanti) {
		this.filmPartecipanti = filmPartecipanti;
	}

	public List<Proiezione> getProiezioni() {
		return proiezioni;
	}

	public void setProiezioni(List<Proiezione> proiezioni) {
		this.proiezioni = proiezioni;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Festival other = (Festival) obj;
		return Objects.equals(id, other.id);
	}

}
