import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../auth.jsx'

export default function Login() {
  const { sessao, login } = useAuth()
  const nav = useNavigate()
  const [f, setF] = useState({ login: '', senha: '' })
  const [erro, setErro] = useState('')
  if (sessao) return <Navigate to="/" replace />

  const enviar = async (e) => {
    e.preventDefault()
    setErro('')
    try {
      await login(f.login, f.senha)
      nav('/')
    } catch (err) {
      setErro(err.message)
    }
  }

  return (
    <main className="auth">
      <form className="card" onSubmit={enviar}>
        <h2>Entrar</h2>
        {erro && <div className="erro">{erro}</div>}
        <label>Usuário</label>
        <input value={f.login} onChange={(e) => setF({ ...f, login: e.target.value })} required />
        <label>Senha</label>
        <input type="password" value={f.senha} onChange={(e) => setF({ ...f, senha: e.target.value })} required />
        <div className="acoes">
          <button>Entrar</button>
          <Link to="/registro">Criar conta</Link>
        </div>
      </form>
    </main>
  )
}
