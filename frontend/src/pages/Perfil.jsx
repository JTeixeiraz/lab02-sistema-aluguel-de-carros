import { useEffect, useState } from 'react'
import { api } from '../api'
import { useAuth } from '../auth.jsx'
import ClienteForm from '../components/ClienteForm.jsx'

export default function Perfil() {
  const { sair } = useAuth()
  const [cliente, setCliente] = useState(null)
  const [msg, setMsg] = useState('')
  const [erro, setErro] = useState('')

  useEffect(() => { api('/clientes/me').then(setCliente).catch((e) => setErro(e.message)) }, [])

  const salvar = async (dados) => {
    setErro(''); setMsg('')
    try {
      setCliente(await api('/clientes/me', { metodo: 'PUT', corpo: dados }))
      setMsg('Cadastro atualizado')
    } catch (e) { setErro(e.message) }
  }

  const excluir = async () => {
    if (!confirm('Excluir sua conta definitivamente?')) return
    try {
      await api('/clientes/me', { metodo: 'DELETE' })
      sair()
    } catch (e) { setErro(e.message) }
  }

  return (
    <main>
      <div className="card">
        <h2>Meu cadastro</h2>
        {erro && <div className="erro">{erro}</div>}
        {msg && <p>{msg}</p>}
        {cliente && <ClienteForm key={cliente.id + cliente.nome} inicial={cliente} onSubmit={salvar} />}
        <div className="acoes"><button className="perigo" onClick={excluir}>Excluir conta</button></div>
      </div>
    </main>
  )
}
