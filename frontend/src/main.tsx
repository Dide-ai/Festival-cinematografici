import { createRoot } from 'react-dom/client'
import ProgrammaProiezioni from './ProgrammaProiezioni'

const root = document.getElementById('root')

if (root) {
  // il festivalId arriva da Thymeleaf con data-festival-id sul div #root:
  // nessuna duplicazione di routing, nessun bisogno di React Router
  const festivalId = Number(root.dataset.festivalId)
  createRoot(root).render(<ProgrammaProiezioni festivalId={festivalId} />)
}
