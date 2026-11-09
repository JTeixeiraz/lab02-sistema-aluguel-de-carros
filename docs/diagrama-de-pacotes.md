# Diagrama de Pacotes (Visao Logica)

```mermaid
flowchart TB
    subgraph Frontend [Front-end React + Vite]
        pages[pages] --> components[components]
        pages --> auth[auth / api]
    end
    subgraph Backend [Back-end Spring Boot - MVC]
        controller[controller<br/>View/REST] --> service[service<br/>regras de negocio]
        controller --> dto[dto]
        service --> repository[repository]
        repository --> model[model<br/>entidades]
        security[security + config] --> service
    end
    DB[(Supabase<br/>PostgreSQL)]
    auth -- HTTPS JSON + JWT --> controller
    repository -- JPA / JDBC --> DB
```

| Camada MVC | Pacote | Responsabilidade |
|------------|--------|------------------|
| Model | `model`, `repository` | Entidades JPA e persistencia |
| View | `frontend/` (React) e `dto` | Interface e contratos JSON |
| Controller | `controller` | Endpoints REST, autorizacao por perfil |
| Servico | `service` | Regras (limite de 3 empregadores, transicoes de status) |
| Seguranca | `security`, `config` | JWT stateless, CORS, BCrypt |
