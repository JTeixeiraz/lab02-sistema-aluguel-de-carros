import { useCallback, useEffect, useState } from 'react'
import { api } from '../api'

const vazio = { matricula: '', ano: new Date().getFullYear(), marca: '', modelo: '', placa: '', tipoProprietario: 'EMPRESA' }

export default function Automoveis() {
  const [lista, setLista] = useState([])
  const [f, setF] = useState(vazio)
  const [editId, setEditId] = useState(null)
  const [erro, setErro] = useState('')

  const carregar = useCallback(() => api('/automoveis').then(setLista).catch((e) => setErro(e.message)), [])
  useEffect(() => { carregar() }, [carregar])

  const campo = (k) => ({ value: f[k], onChange: (e) => setF({ ...f, [k]: e.target.value }) })

  const salvar = async (e) => {
    e.preventDefault(); setErro('')
    try {
      const corpo = { ...f, ano: Number(f.ano) }
      if (editId) await api(`/automoveis/${editId}`, { metodo: 'PUT', corpo })
      else await api('/automoveis', { metodo: 'POST', corpo })
      setF(vazio); setEditId(null); carregar()
    } catch (err) { setErro(err.message) }
  }

  const remover = async (a) => {
    if (!confirm(`Remover ${a.placa}?`)) return
    try { await api(`/automoveis/${a.id}`, { metodo: 'DELETE' }); carregar() } catch (err) { setErro(err.message) }
  }

  return (
    <main>
      <h2>Automóveis</h2>
      {erro && <div className="erro">{erro}</div>}
      <form className="card" onSubmit={salvar}>
        <h3>{editId ? 'Editar automóvel' : 'Novo automóvel'}</h3>
        <div className="linha">
          <div><label>Matrícula</label><input {...campo('matricula')} required /></div>
          <div><label>Placa</label><input {...campo('placa')} required /></div>
          <div><label>Ano</label><input type="number" {...campo('ano')} required /></div>
        </div>
        <div className="linha">
          <div><label>Marca</label><input {...campo('marca')} required /></div>
          <div><label>Modelo</label><input {...campo('modelo')} required /></div>
          <div><label>Propriedade</label>
            <select {...campo('tipoProprietario')}>
              <option value="CLIENTE">Cliente</option><option value="EMPRESA">Empresa</option><option value="BANCO">Banco</option>
            </select>
          </div>
        </div>
        <div className="acoes">
          <button>{editId ? 'Salvar' : 'Cadastrar'}</button>
          {editId && <button type="button" className="sec" onClick={() => { setEditId(null); setF(vazio) }}>Cancelar</button>}
        </div>
      </form>
      <div className="card" style={{ overflowX: 'auto' }}>
        <table>
          <thead><tr><th>Placa</th><th>Matrícula</th><th>Veículo</th><th>Ano</th><th>Propriedade</th><th></th></tr></thead>
          <tbody>
            {lista.map((a) => (
              <tr key={a.id}>
                <td>{a.placa}</td><td>{a.matricula}</td><td>{a.marca} {a.modelo}</td><td>{a.ano}</td><td>{a.tipoProprietario}</td>
                <td><div className="acoes" style={{ marginTop: 0 }}>
                  <button className="sec" onClick={() => { setEditId(a.id); setF(a) }}>Editar</button>
                  <button className="perigo" onClick={() => remover(a)}>Remover</button>
                </div></td>
              </tr>
            ))}
            {lista.length === 0 && <tr><td colSpan="6">Nenhum automóvel cadastrado.</td></tr>}
          </tbody>
        </table>
      </div>
    </main>
  )
}
