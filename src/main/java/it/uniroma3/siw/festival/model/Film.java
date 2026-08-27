package it.uniroma3.siw.festival.model;

import java.util.List;
import java.util.Objects;

import it.uniroma3.siw.festival.validation.AnnoNonFuturo;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Film {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@NotBlank
	@Column(nullable = false)
	private String titolo;

	@NotNull
	@Min(1895)
	@AnnoNonFuturo
	@Column(nullable = false)
	private Integer anno;

	@NotNull
	@Min(1)
	@Max(600)
	private Integer durata;

	@NotBlank
	private String genere;

	@NotBlank
	private String paeseProduzione;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "regista_id")
	private Regista regista;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
		name = "film_festival",
		joinColumns = @JoinColumn(name = "film_id"),
		inverseJoinColumns = @JoinColumn(name = "festival_id")
	)
	private List<Festival> festivalPartecipati;

	@OneToMany(mappedBy = "film", fetch = FetchType.LAZY)
	private List<Proiezione> proiezioni;

	@OneToMany(mappedBy = "film", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Recensione> recensioni;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public Integer getDurata() {
		return durata;
	}

	public void setDurata(Integer durata) {
		this.durata = durata;
	}

	public String getGenere() {
		return genere;
	}

	public void setGenere(String genere) {
		this.genere = genere;
	}

	public String getPaeseProduzione() {
		return paeseProduzione;
	}

	public void setPaeseProduzione(String paeseProduzione) {
		this.paeseProduzione = paeseProduzione;
	}

	public Regista getRegista() {
		return regista;
	}

	public void setRegista(Regista regista) {
		this.regista = regista;
	}

	public List<Festival> getFestivalPartecipati() {
		return festivalPartecipati;
	}

	public void setFestivalPartecipati(List<Festival> festivalPartecipati) {
		this.festivalPartecipati = festivalPartecipati;
	}

	public List<Proiezione> getProiezioni() {
		return proiezioni;
	}

	public void setProiezioni(List<Proiezione> proiezioni) {
		this.proiezioni = proiezioni;
	}

	public List<Recensione> getRecensioni() {
		return recensioni;
	}

	public void setRecensioni(List<Recensione> recensioni) {
		this.recensioni = recensioni;
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
		Film other = (Film) obj;
		return Objects.equals(id, other.id);
	}

}
