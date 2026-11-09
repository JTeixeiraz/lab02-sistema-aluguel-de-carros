import { createContext, useContext, useState } from 'react'
import { api, getSessao, salvarSessao } from './api'

const Ctx = createContext(null)

export function AuthProvider({ children }) {
  const [sessao, setSessao] = useState(getSessao())

  const entrar = (s) => {
    salvarSessao(s)
    setSessao(s)
  }
  const login = async (login, senha) =>
    entrar(await api('/auth/login', { metodo: 'POST', corpo: { login, senha } }))
  const registrar = async (tipo, corpo) =>
    entrar(await api(`/auth/registro/${tipo}`, { metodo: 'POST', corpo }))
  const sair = () => {
    salvarSessao(null)
    setSessao(null)
  }

  return <Ctx.Provider value={{ sessao, login, registrar, sair }}>{children}</Ctx.Provider>
}

export const useAuth = () => useContext(Ctx)
