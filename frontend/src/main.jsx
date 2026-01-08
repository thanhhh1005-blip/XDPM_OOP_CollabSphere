import { createRoot } from 'react-dom/client'
import App from './App.jsx'
import './index.css' // 👈 BỔ SUNG DÒNG NÀY (Rất quan trọng)

createRoot(document.getElementById('root')).render(
    <App />
)