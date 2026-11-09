import { useCallback, useEffect, useState } from 'react'
import { api } from '../api'
import { useAuth } from '../auth.jsx'

const fmt = (d) => new Date(d + 'T00:00:00').toLocaleDateString('pt-BR')

function NovoPedido({ automoveis, editando, onSalvo, onCancelar }) {
  const [f, setF] = useState({
    automovelId: editando?.automovel.id ?? '',
    dataInicio: editando?.dataInicio ?? '',
    dataFim: editando?.dataFim ?? '',
  })
  const [erro, setErro] = useState('')

  const enviar = async (e) => {
    e.preventDefault()
    setErro('')
    try {
      const corpo = { ...f, automovelId: Number(f.automovelId) }
      if (editando) await api(`/pedidos/${editando.id}`, { metodo: 'PUT', corpo })
      else await api('/pedidos', { metodo: 'POST', corpo })
      onSalvo()
    } catch (err) { setErro(err.message) }
  }

  return (
    <form className="card" onSubmit={enviar}>
      <h3>{editando ? `Modificar pedido #${editando.id}` : 'Novo pedido de aluguel'}</h3>
      {erro && <div className="erro">{erro}</div>}
      <label>Automóvel</label>
      <select value={f.automovelId} onChange={(e) => setF({ ...f, automovelId: e.target.value })} required>
        <option value="">Selecione…</option>
        {automoveis.map((a) => (
          <option key={a.id} value={a.id}>{a.marca} {a.modelo} {a.ano} — {a.placa}</option>
        ))}
      </select>
      <div className="linha">
        <div><label>Início</label><input type="date" value={f.dataInicio} onChange={(e) => setF({ ...f, dataInicio: e.target.value })} required /></div>
        <div><label>Fim</label><input type="date" value={f.dataFim} onChange={(e) => setF({ ...f, dataFim: e.target.value })} required /></div>
      </div>
      <div className="acoes">
        <button>{editando ? 'Salvar' : 'Enviar pedido'}</button>
        <button type="button" className="sec" onClick={onCancelar}>Fechar</button>
      </div>
    </form>
  )
}

export default function Pedidos() {
  const { sessao } = useAuth()
  const agente = sessao.perfil === 'AGENTE'
  const [pedidos, setPedidos] = useState([])
  const [automoveis, setAutomoveis] = useState([])
  const [form, setForm] = useState(null) // null | {} | pedido
  const [erro, setErro] = useState('')

  const carregar = useCallback(async () => {
    try {
      const [p, a] = await Promise.all([api('/pedidos'), api('/automoveis')])
      setPedidos(p); setAutomoveis(a)
    } catch (e) { setErro(e.message) }
  }, [])
  useEffect(() => { carregar() }, [carregar])

  const acao = async (fn) => {
    setErro('')
    try { await fn(); await carregar() } catch (e) { setErro(e.message) }
  }

  const cancelar = (p) => confirm(`Cancelar pedido #${p.id}?`) &&
    acao(() => api(`/pedidos/${p.id}`, { metodo: 'DELETE' }))

  const avaliar = (p, status) => {
    const parecer = prompt('Parecer (opcional):') ?? ''
    return acao(() => api(`/pedidos/${p.id}/avaliacao`, { metodo: 'PATCH', corpo: { status, parecer } }))
  }

  return (
    <main>
      <div className="linha" style={{ alignItems: 'center' }}>
        <h2>{agente ? 'Pedidos recebidos' : 'Meus pedidos'}</h2>
        {!agente && <button style={{ flex: '0 0 auto' }} onClick={() => setForm({})}>Novo pedido</button>}
      </div>
      {erro && <div className="erro">{erro}</div>}
      {form && (
        <NovoPedido automoveis={automoveis} editando={form.id ? form : null}
          onSalvo={() => { setForm(null); carregar() }} onCancelar={() => setForm(null)} />
      )}
      <div className="card" style={{ overflowX: 'auto' }}>
        <table>
          <thead>
            <tr>
              <th>#</th>{agente && <th>Cliente</th>}<th>Automóvel</th><th>Período</th><th>Status</th><th>Parecer</th><th></th>
            </tr>
          </thead>
          <tbody>
            {pedidos.map((p) => (
              <tr key={p.id}>
                <td>{p.id}</td>
                {agente && <td>{p.cliente.nome}<br /><small>{p.cliente.cpf}</small></td>}
                <td>{p.automovel.marca} {p.automovel.modelo}<br /><small>{p.automovel.placa}</small></td>
                <td>{fmt(p.dataInicio)} – {fmt(p.dataFim)}</td>
                <td><span className={`badge ${p.status}`}>{p.status.replace('_', ' ')}</span>
                  {p.bancoCredito && <><br /><small>crédito: {p.bancoCredito}</small></>}</td>
                <td>{p.parecer}</td>
                <td>
                  <div className="acoes" style={{ marginTop: 0 }}>
                    {!agente && p.status === 'EM_ANALISE' && <button className="sec" onClick={() => setForm(p)}>Modificar</button>}
                    {!agente && !['CONTRATADO', 'CANCELADO'].includes(p.status) && <button className="perigo" onClick={() => cancelar(p)}>Cancelar</button>}
                    {agente && p.status === 'EM_ANALISE' && <>
                      <button className="ok" onClick={() => avaliar(p, 'APROVADO')}>Aprovar</button>
                      <button className="perigo" onClick={() => avaliar(p, 'REPROVADO')}>Reprovar</button>
                    </>}
                    {agente && p.status === 'APROVADO' && <button onClick={() => avaliar(p, 'CONTRATADO')}>Firmar contrato</button>}
                  </div>
                </td>
              </tr>
            ))}
            {pedidos.length === 0 && <tr><td colSpan="7">Nenhum pedido.</td></tr>}
          </tbody>
        </table>
      </div>
    </main>
  )
}
