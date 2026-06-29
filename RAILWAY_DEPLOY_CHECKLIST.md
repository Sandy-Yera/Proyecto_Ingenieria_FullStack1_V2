# Checklist de despliegue en Railway

Orden estricto. No avances al siguiente paso hasta confirmar que el actual
queda "healthy" en el dashboard de Railway.

**Importante sobre el contexto de build**: NO todos los servicios usan la
raíz del repositorio como contexto. Cada Dockerfile.remote hace `COPY pom.xml .`
esperando que el `pom.xml` de SU servicio esté en la raíz del contexto de
build — y no existe un `pom.xml` en la raíz del repositorio. Si dejas el
"Root Directory" de Railway vacío (= raíz del repo) para estos servicios,
el build falla con `pom.xml: no such file or directory`. La única excepción
es `config-server`, que sí necesita la raíz del repo porque su Dockerfile.remote
copia también la carpeta `config-repo/`.

Al crear cada servicio en Railway (Settings → Source), configura:
- **Root Directory**: la carpeta indicada en la tabla de abajo (columna
  "Root Directory"). Esto cambia el contexto de build a esa carpeta.
- **Dockerfile Path** (Settings → Build → Builder = Dockerfile): la ruta
  indicada (relativa al Root Directory que acabas de fijar).

| Servicio | Root Directory | Dockerfile Path |
|---|---|---|
| eureka-server | `infraestructure/eureka-server` | `Dockerfile` |
| config-server | *(vacío, raíz del repo)* | `infraestructure/config-server/Dockerfile.remote` |
| ms-auth | `services/ms-auth` | `Dockerfile.remote` |
| ms-users | `services/ms-users` | `Dockerfile.remote` |
| ms-buildings | `services/ms-buildings` | `Dockerfile.remote` |
| ms-price-engine | `services/ms-price-engine` | `Dockerfile.remote` |
| ms-quotes | `services/ms-quotes` | `Dockerfile.remote` |
| ms-workorders | `services/ms-workorders` | `Dockerfile.remote` |
| api-gateway | `infraestructure/api-gateway` | `Dockerfile.remote` |

- [ ] **MySQL** — "New" → "Database" → "Add MySQL".
- [ ] **eureka-server** — Root Directory `infraestructure/eureka-server`,
  Dockerfile Path `Dockerfile` (no es `.remote`, ya compila en multi-stage).
  Variables: ninguna (`infraestructure/eureka-server/.env.railway.example`).
  Dominio público temporal opcional para verificar el dashboard.
- [ ] **config-server** — Root Directory vacío (raíz del repo), Dockerfile
  Path `infraestructure/config-server/Dockerfile.remote`. Variables: ninguna
  (`infraestructure/config-server/.env.railway.example`).
  Dominio público temporal opcional para verificar `/ms-auth-remote/remote`.
- [ ] **ms-auth** — Root Directory `services/ms-auth`, Dockerfile Path
  `Dockerfile.remote`. Variables en `services/ms-auth/.env.railway.example`.
- [ ] **ms-users** — Root Directory `services/ms-users`, Dockerfile Path
  `Dockerfile.remote`. Variables en `services/ms-users/.env.railway.example`.
- [ ] **ms-buildings** — Root Directory `services/ms-buildings`, Dockerfile
  Path `Dockerfile.remote`. Variables en
  `services/ms-buildings/.env.railway.example`.
- [ ] **ms-price-engine** — Root Directory `services/ms-price-engine`,
  Dockerfile Path `Dockerfile.remote`. Variables en
  `services/ms-price-engine/.env.railway.example`.
- [ ] **ms-quotes** — Root Directory `services/ms-quotes`, Dockerfile Path
  `Dockerfile.remote`. Variables en `services/ms-quotes/.env.railway.example`.
- [ ] **ms-workorders** — Root Directory `services/ms-workorders`, Dockerfile
  Path `Dockerfile.remote`. Variables en
  `services/ms-workorders/.env.railway.example`.
- [ ] **api-gateway** — Root Directory `infraestructure/api-gateway`,
  Dockerfile Path `Dockerfile.remote`. Variables en
  `infraestructure/api-gateway/.env.railway.example`. Único servicio con
  dominio público permanente (Networking → Generate Domain).

## Después de desplegar cada uno de los 6 servicios core

En su `.env.railway.example`, reemplaza:
- `<MYSQL_HOST>` / `<MYSQL_PORT>` por el host/puerto **privado** del plugin
  MySQL (pestaña "Variables" del plugin, ej. `mysql.railway.internal`).
- `<MYSQL_USER>` / `<MYSQL_PASSWORD>` por las credenciales del plugin MySQL.
- `<api-gateway-domain>` por el dominio público de api-gateway (solo se
  conoce una vez desplegado ese último paso; puedes dejarlo con el valor
  por defecto del placeholder mientras tanto, ya que el `@Server` de
  Swagger tiene fallback hardcodeado).

## Verificación final

- [ ] Dashboard de eureka-server muestra los 6 servicios + api-gateway
  registrados.
- [ ] `https://<api-gateway-domain>/<ruta-del-servicio>` responde a través
  del gateway para cada uno de los 6 servicios.
- [ ] Swagger de cada servicio (via gateway) resuelve `@Server` con el
  `APP_GATEWAY_URL` real.
