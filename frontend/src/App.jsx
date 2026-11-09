import { Navigate, NavLink, Route, Routes } from 'react-router-dom'
import { useAuth } from './auth.jsx'
import Login from './pages/Login.jsx'
import Registro from './pages/Registro.jsx'
import Pedidos from './pages/Pedidos.jsx'
import Perfil from './pages/Perfil.jsx'
import Automoveis from './pages/Automoveis.jsx'

function Protegida({ perfil, children }) {
  const { sessao } = useAuth()
  if (!sessao) return <Navigate to="/login" replace />
  if (perfil && sessao.perfil !== perfil) return <Navigate to="/" replace />
  return children
}

export default function App() {
  const { sessao, sair } = useAuth()
  return (
    <>
      {sessao && (
        <header className="topo">
          <strong>Aluguel de Carros</strong>
          <nav>
            <NavLink to="/" end className={({ isActive }) => (isActive ? 'ativo' : '')}>Pedidos</NavLink>
            {sessao.perfil === 'CLIENTE' && <NavLink to="/perfil">Meu cadastro</NavLink>}
            {sessao.perfil === 'AGENTE' && <NavLink to="/automoveis">Automóveis</NavLink>}
            <span>{sessao.login} ({sessao.perfil === 'AGENTE' ? sessao.tipoAgente : 'CLIENTE'})</span>
            <button className="sec" onClick={sair}>Sair</button>
          </nav>
        </header>
      )}
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/registro" element={<Registro />} />
        <Route path="/" element={<Protegida><Pedidos /></Protegida>} />
        <Route path="/perfil" element={<Protegida perfil="CLIENTE"><Perfil /></Protegida>} />
        <Route path="/automoveis" element={<Protegida perfil="AGENTE"><Automoveis /></Protegida>} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </>
  )
}
