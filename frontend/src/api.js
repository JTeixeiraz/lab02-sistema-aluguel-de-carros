const BASE = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'

export function getSessao() {
  try {
    return JSON.parse(localStorage.getItem('sessao'))
  } catch {
    return null
  }
}

export function salvarSessao(s) {
  if (s) localStorage.setItem('sessao', JSON.stringify(s))
  else localStorage.removeItem('sessao')
}

export async function api(caminho, { metodo = 'GET', corpo } = {}) {
  const sessao = getSessao()
  const res = await fetch(BASE + caminho, {
    method: metodo,
    headers: {
      'Content-Type': 'application/json',
      ...(sessao ? { Authorization: `Bearer ${sessao.token}` } : {}),
    },
    body: corpo ? JSON.stringify(corpo) : undefined,
  })
  if (res.status === 401 && sessao && !caminho.startsWith('/auth/')) {
    salvarSessao(null)
    window.location.hash = '#/login'
    throw new Error('Sessao expirada')
  }
  if (res.status === 204) return null
  const dados = await res.json().catch(() => null)
  if (!res.ok) throw new Error(dados?.erro ?? `Erro ${res.status}`)
  return dados
}
