# Checklist de despliegue en Railway

Orden estricto. No avances al siguiente paso hasta confirmar que el actual
queda "healthy" en el dashboard de Railway.

Cada servicio usa Builder = Dockerfile, ruta = su `Dockerfile.remote`
correspondiente, contexto de build = raíz del repositorio.

- [ ] **MySQL** — "New" → "Database" → "Add MySQL".
- [ ] **eureka-server** — `infraestructure/eureka-server/Dockerfile` (no es
  `.remote`, ya compila en multi-stage). Variables: ninguna
  (`infraestructure/eureka-server/.env.railway.example`). Dominio público
  temporal opcional para verificar el dashboard.
- [ ] **config-server** — `infraestructure/config-server/Dockerfile.remote`.
  Variables: ninguna (`infraestructure/config-server/.env.railway.example`).
  Dominio público temporal opcional para verificar `/ms-auth-remote/remote`.
- [ ] **ms-auth** — `services/ms-auth/Dockerfile.remote`. Variables en
  `services/ms-auth/.env.railway.example`.
- [ ] **ms-users** — `services/ms-users/Dockerfile.remote`. Variables en
  `services/ms-users/.env.railway.example`.
- [ ] **ms-buildings** — `services/ms-buildings/Dockerfile.remote`. Variables
  en `services/ms-buildings/.env.railway.example`.
- [ ] **ms-price-engine** — `services/ms-price-engine/Dockerfile.remote`.
  Variables en `services/ms-price-engine/.env.railway.example`.
- [ ] **ms-quotes** — `services/ms-quotes/Dockerfile.remote`. Variables en
  `services/ms-quotes/.env.railway.example`.
- [ ] **ms-workorders** — `services/ms-workorders/Dockerfile.remote`.
  Variables en `services/ms-workorders/.env.railway.example`.
- [ ] **api-gateway** — `infraestructure/api-gateway/Dockerfile.remote`.
  Variables en `infraestructure/api-gateway/.env.railway.example`. Único
  servicio con dominio público permanente (Networking → Generate Domain).

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
