import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth.jsx'
import ClienteForm from '../components/ClienteForm.jsx'

export default function Registro() {
  const { sessao, registrar } = useAuth()
  const nav = useNavigate()
  const [tipo, setTipo] = useState('cliente')
  const [cred, setCred] = useState({ login: '', senha: '' })
  const [ag, setAg] = useState({ tipoAgente: 'BANCO', codigoConvite: '' })
  const [erro, setErro] = useState('')
  if (sessao) return <Navigate to="/" replace />

  const executar = async (corpo) => {
    setErro('')
    try {
      await registrar(tipo, corpo)
      nav('/')
    } catch (err) {
      setErro(err.message)
    }
  }

  return (
    <main className="auth" style={{ maxWidth: 560 }}>
      <div className="card">
        <h2>Cadastro</h2>
        <div className="acoes">
          <button className={tipo === 'cliente' ? '' : 'sec'} onClick={() => setTipo('cliente')}>Cliente</button>
          <button className={tipo === 'agente' ? '' : 'sec'} onClick={() => setTipo('agente')}>Agente</button>
        </div>
        {erro && <div className="erro">{erro}</div>}
        <label>Usuário</label>
        <input value={cred.login} onChange={(e) => setCred({ ...cred, login: e.target.value })} required />
        <label>Senha (mín. 6)</label>
        <input type="password" value={cred.senha} onChange={(e) => setCred({ ...cred, senha: e.target.value })} required />
        {tipo === 'cliente' ? (
          <ClienteForm rotulo="Cadastrar" onSubmit={(dados) => executar({ ...cred, dados })} />
        ) : (
          <form onSubmit={(e) => { e.preventDefault(); executar({ ...cred, ...ag }) }}>
            <label>Tipo de agente</label>
            <select value={ag.tipoAgente} onChange={(e) => setAg({ ...ag, tipoAgente: e.target.value })}>
              <option value="BANCO">Banco</option>
              <option value="EMPRESA">Empresa</option>
            </select>
            <label>Código de convite</label>
            <input value={ag.codigoConvite} onChange={(e) => setAg({ ...ag, codigoConvite: e.target.value })} required />
            <div className="acoes"><button>Cadastrar</button></div>
          </form>
        )}
        <p><Link to="/login">Já tenho conta</Link></p>
      </div>
    </main>
  )
}
