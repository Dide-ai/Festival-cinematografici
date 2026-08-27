import { useEffect, useState } from 'react'
import type { Proiezione } from './types'

interface Props {
  festivalId: number
}

export default function ProgrammaProiezioni({ festivalId }: Props) {
  const [proiezioni, setProiezioni] = useState<Proiezione[]>([])
  const [caricamento, setCaricamento] = useState<boolean>(true)
  const [errore, setErrore] = useState<string | null>(null)
  const [filtroData, setFiltroData] = useState<string>('')
  const [filtroStato, setFiltroStato] = useState<string>('')

  useEffect(() => {
    async function carica() {
      setCaricamento(true)
      setErrore(null)
      try {
        const risposta = await fetch(`/api/festival/${festivalId}/proiezioni`)
        if (!risposta.ok) {
          throw new Error(`il server ha risposto ${risposta.status}`)
        }
        const dati: Proiezione[] = await risposta.json()
        setProiezioni(dati)
      } catch (e) {
        setErrore(e instanceof Error ? e.message : 'errore di rete')
      } finally {
        setCaricamento(false)
      }
    }
    carica()
  }, [festivalId])

  // i due filtri lavorano sui dati gia' caricati: nessuna nuova chiamata al server
  const visibili = proiezioni.filter(
    (p) => (filtroData === '' || p.data === filtroData) &&
           (filtroStato === '' || p.stato === filtroStato)
  )

  if (caricamento) {
    return <p>Caricamento del programma...</p>
  }

  if (errore) {
    return <p>Non è stato possibile caricare il programma: {errore}</p>
  }

  return (
    <div>
      <div>
        <label>Data: </label>
        <input type="date" value={filtroData} onChange={(e) => setFiltroData(e.target.value)} />
        <label> Stato: </label>
        <select value={filtroStato} onChange={(e) => setFiltroStato(e.target.value)}>
          <option value="">Tutti</option>
          <option value="SCHEDULED">SCHEDULED</option>
          <option value="COMPLETED">COMPLETED</option>
          <option value="CANCELLED">CANCELLED</option>
        </select>
        <button onClick={() => { setFiltroData(''); setFiltroStato('') }}>Azzera i filtri</button>
      </div>

      {proiezioni.length === 0 && <p>Questo festival non ha proiezioni.</p>}

      {proiezioni.length > 0 && visibili.length === 0 &&
        <p>Nessuna proiezione corrisponde ai filtri.</p>}

      {visibili.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Data</th>
              <th>Ora</th>
              <th>Film</th>
              <th>Durata</th>
              <th>Sala</th>
              <th>Stato</th>
            </tr>
          </thead>
          <tbody>
            {visibili.map((p) => (
              <tr key={p.id}>
                <td>{p.data}</td>
                <td>{p.ora}</td>
                <td>{p.titoloFilm}</td>
                <td>{p.durataFilm} min</td>
                <td>{p.nomeSala}</td>
                <td>{p.stato}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  )
}
