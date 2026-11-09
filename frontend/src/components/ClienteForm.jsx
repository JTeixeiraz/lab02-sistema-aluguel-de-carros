import { useState } from 'react'

const vazio = { rg: '', cpf: '', nome: '', endereco: '', profissao: '', empregadores: [] }

export default function ClienteForm({ inicial, onSubmit, rotulo = 'Salvar' }) {
  const [d, setD] = useState({ ...vazio, ...inicial, profissao: inicial?.profissao ?? '' })
  const campo = (k) => ({ value: d[k], onChange: (e) => setD({ ...d, [k]: e.target.value }) })

  const setEmp = (i, k, v) =>
    setD({ ...d, empregadores: d.empregadores.map((e, j) => (j === i ? { ...e, [k]: v } : e)) })

  const enviar = (e) => {
    e.preventDefault()
    onSubmit({
      ...d,
      empregadores: d.empregadores.map((x) => ({ nome: x.nome, rendimento: Number(x.rendimento) })),
    })
  }

  return (
    <form onSubmit={enviar}>
      <label>Nome</label>
      <input {...campo('nome')} required />
      <div className="linha">
        <div><label>RG</label><input {...campo('rg')} required /></div>
        <div><label>CPF</label><input {...campo('cpf')} required /></div>
      </div>
      <label>Endereço</label>
      <input {...campo('endereco')} required />
      <label>Profissão</label>
      <input {...campo('profissao')} />
      <h4>Empregadores e rendimentos (máx. 3)</h4>
      {d.empregadores.map((e, i) => (
        <div className="linha" key={i}>
          <input placeholder="Empregador" value={e.nome} onChange={(ev) => setEmp(i, 'nome', ev.target.value)} required />
          <input type="number" min="0" step="0.01" placeholder="Rendimento" value={e.rendimento}
            onChange={(ev) => setEmp(i, 'rendimento', ev.target.value)} required />
          <button type="button" className="perigo" style={{ flex: '0 0 auto' }}
            onClick={() => setD({ ...d, empregadores: d.empregadores.filter((_, j) => j !== i) })}>Remover</button>
        </div>
      ))}
      <div className="acoes">
        <button type="button" className="sec" disabled={d.empregadores.length >= 3}
          onClick={() => setD({ ...d, empregadores: [...d.empregadores, { nome: '', rendimento: '' }] })}>
          + Empregador
        </button>
        <button>{rotulo}</button>
      </div>
    </form>
  )
}
