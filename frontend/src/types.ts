// Rispecchia ProiezioneDto del backend: se cambia il record Java, cambia anche qui.
export interface Proiezione {
  id: number
  data: string
  ora: string
  stato: string
  filmId: number
  titoloFilm: string
  durataFilm: number
  nomeSala: string
}
