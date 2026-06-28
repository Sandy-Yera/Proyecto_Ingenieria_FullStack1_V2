![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Apache Kafka](https://img.shields.io/badge/Apache_Kafka-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)

# 🏢 Building Repair Management System
### Proyecto Semestral — Arquitectura de Microservicios

---

## 📖 Descripción del Proyecto

El **Building Repair Management System** es un ecosistema integral basado en una **arquitectura de microservicios** diseñado para gestionar todo el ciclo de vida de las reparaciones en edificios: desde la cotización inicial hasta la facturación y el historial de mantenimiento.

El sistema coordina de forma eficiente el personal técnico, la flota de vehículos y los materiales necesarios para realizar reparaciones, cubriendo procesos críticos como:

- **Gestión de Identidad:** Registro de usuarios, autenticación JWT y edificios.
- **Ciclo de Cotización:** Solicitudes de presupuesto con motor de precios y circuit breaker.
- **Operación Logística:** Asignación de técnicos, vehículos, agendas e inventario.
- **Soporte Financiero:** Procesamiento de pagos y generación de facturas.
- **Notificaciones Asíncronas:** Comunicación por eventos mediante Apache Kafka.

El ecosistema está compuesto por **17 microservicios de negocio** + 3 servicios de infraestructura, cada uno con su propia base de datos aislada, siguiendo el patrón arquitectónico **CSR (Controller – Service – Repository)** y principios de diseño REST.

---

## 👥 Integrantes del Equipo

| Nombre | GitHub |
|:---|:---|
| Catalina Lobos | [@Shany-Ghost](https://github.com/Shany-Ghost) |
| Gabriel Sandoval | [@Sandy-Yera](https://github.com/Sandy-Yera) |
| Simón Inostroza | [@siminostroza](https://github.com/siminostroza) |

---

## 🛠️ Stack Tecnológico

| Componente | Tecnología |
|:---|:---|
| Lenguaje | Java 21+ |
| Framework | Spring Boot 3.x |
| Persistencia | JPA + Hibernate + MySQL |
| Service Discovery | Spring Cloud Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Comunicación síncrona | OpenFeign |
| Mensajería asíncrona | Apache Kafka + Zookeeper |
| Autenticación | Spring Security + JWT |
| Resiliencia | Resilience4j (Circuit Breaker) |
| Documentación | SpringDoc OpenAPI (Swagger) |
| Validaciones | Bean Validation (JSR 380) |
| Contenedores | Docker & Docker Compose |
| Control de versiones | Git + GitHub |

---

## 🧩 Ecosistema de Microservicios

El sistema se organiza en **6 fases lógicas** que componen los 17 microservicios de negocio:

### 🔧 Infraestructura Base

| Servicio | Puerto | Descripción |
|:---|:---|:---|
| `api-gateway` | 8080 (externo: **9090**) | Punto de entrada único con filtro de trazabilidad `X-Trace-Id` |
| `eureka-server` | 8761 | Descubrimiento de servicios (Eureka Server) |
| `config-server` | 8888 | Configuración centralizada para todos los servicios |

### 🔑 Fase 1 — Núcleo de Identidad, Acceso y Seguridad

| Servicio | Puerto | Descripción | Estado |
|:---|:---|:---|:---|
| `ms-auth` | 8081 | Autenticación, JWT login y gestión de credenciales | ✅ Implementado |
| `ms-logs` | 8082 | Trazabilidad del sistema, consulta paginada y filtrada | ✅ Implementado |
| `ms-users` | 8083 | Registro y gestión de usuarios del sistema | ✅ Implementado |
| `ms-buildings` | 8084 | Gestión del catálogo de edificios | ✅ Implementado |
| `ms-security` | 8087 | Gestión centralizada de roles y asignaciones | ✅ Implementado |

### 💰 Fase 2 — Ciclo de Cotización y Tarifas

| Servicio | Puerto | Descripción | Estado |
|:---|:---|:---|:---|
| `ms-quotes` | 8088 | Creación y seguimiento de cotizaciones (Swagger + Circuit Breaker) | ✅ Implementado |
| `ms-price-engine` | 8095 | Motor automático de cálculo de precios por categoría, horas y materiales | ✅ Implementado |

### 🚚 Fase 3 — Recursos y Gestión de Campo

| Servicio | Puerto | Descripción | Estado |
|:---|:---|:---|:---|
| `ms-staff` | 8085 | Gestión del personal técnico con disponibilidad y especialidad | ✅ Implementado |
| `ms-fleet` | 8086 | Control de la flota de vehículos con telemetría GPS vía Kafka | ✅ Implementado |
| `ms-schedule` | 8098 | Agendamiento de técnicos con validación anti-solapamiento | ✅ Implementado |
| `ms-inventory` | 8089 | Control de materiales e insumos con consumo y reabastecimiento | ✅ Implementado |
| `ms-purchase` | 8096 | Gestión de compras con reabastecimiento automático de inventario | ✅ Implementado |

### ⚙️ Fase 4 — Operación y Ejecución

| Servicio | Puerto | Descripción | Estado |
|:---|:---|:---|:---|
| `ms-workorders` | 8097 | Máquina de estados de órdenes de trabajo (PENDING → ASSIGNED → IN_PROGRESS → COMPLETED) | ✅ Implementado |
| `ms-logistics` | 8093 | Orquestador logístico con cálculo de distancia Haversine (PLANIFICADO → EN_RUTA → COMPLETADO) | ✅ Implementado |

### 🧾 Fase 5 — Cierre Financiero y Notificaciones

| Servicio | Puerto | Descripción | Estado |
|:---|:---|:---|:---|
| `ms-payments` | 8091 | Procesamiento de pagos asociados a facturas | 🔲 Pendiente |
| `ms-billing` | 8092 | Generación de facturas al cierre de la orden | 🔲 Pendiente |
| `ms-notifications` | 8094 | Notificaciones persistidas + consumer Kafka de telemetría | 🔲 Pendiente |

---

## ✅ Funcionalidades Implementadas

### Arquitectura y Estructura
- Patrón **CSR** aplicado en todos los microservicios (Controller → Service → Repository)
- Separación estricta de responsabilidades por capa y por paquete
- Uso de **DTOs** para comunicación entre capas y entrada/salida de datos
- Cada microservicio posee su propio esquema de base de datos aislado
- Configuración centralizada vía **Config Server** + repositorio `config-repo`

### Autenticación y Seguridad JWT
- Login con emisión de **token JWT** firmado con clave secreta
- Endpoint `/api/auth/validate` para verificación de tokens por el Gateway
- Cifrado de contraseñas con **BCrypt** (Spring Security)
- Consumo de evento Kafka para eliminación en cascada de credenciales al borrar un usuario
- Gestión de roles y asignaciones de roles vía `ms-security`

### Persistencia con JPA + Hibernate
- Entidades modeladas con anotaciones `@Entity`, `@Id`, `@GeneratedValue`, `@OneToMany`, `@ManyToOne`
- Repositorios implementados con `JpaRepository` para operaciones CRUD
- Configuración del datasource y dialecto centralizada en `config-repo`

### CRUD Completo
- Operaciones **GET, POST, PUT, DELETE (y PATCH en ms-staff)** implementadas en todos los servicios activos
- Endpoints retornan JSON estructurado con `ResponseEntity`
- Códigos HTTP adecuados según la operación y resultado

### Validaciones con Bean Validation (JSR 380)
- Anotaciones de validación en DTOs (`@NotNull`, `@NotBlank`, `@Size`, `@Min`, etc.)
- Respuestas consistentes ante entradas inválidas

### Manejo de Excepciones
- `@ControllerAdvice` para manejo centralizado de errores
- Excepciones tipadas por dominio: `EntityNotFoundException`, `EntityConflictException`, `EntityBadRequestException`, `EntityCreationException`
- Respuestas estructuradas con códigos HTTP correctos (400, 404, 409, 500, etc.)

### Máquinas de Estado
- **ms-workorders:** `PENDING → ASSIGNED → IN_PROGRESS → COMPLETED / CANCELLED`
- **ms-logistics:** `PLANIFICADO → EN_RUTA → COMPLETADO / CANCELADO`
- Transiciones validadas: cada endpoint de cambio de estado verifica el estado actual antes de avanzar

### Comunicación entre Microservicios
- **Síncrona:** OpenFeign con patrón self-proxy (`@Lazy`) para separar validaciones remotas de la persistencia atómica
- **Asíncrona:** Apache Kafka para logs del sistema, telemetría GPS y eventos de ciclo de vida
- **Resiliencia:** Circuit Breaker (Resilience4j) en `ms-quotes` con configuración de ventana deslizante

### Cálculo Haversine (ms-logistics)
- Fórmula implementada localmente sin API externa: `d = R · 2 · atan2(√a, √(1−a))` con R = 6371 km
- Tiempo estimado de llegada calculado asumiendo velocidad urbana de 40 km/h
- Endpoint utilitario `GET /api/logistics/calcular-distancia` para cálculo independiente

### Logs Estructurados
- `ms-logs` consume eventos Kafka de múltiples servicios y expone consulta paginada
- Filtros por `serviceName` y `level` con paginación configurable

### Documentación Swagger
- Dependencia `springdoc-openapi-starter-webmvc-ui` en todos los servicios implementados
- `SwaggerConfig` con `@OpenAPIDefinition` en cada servicio
- Accesible en `/swagger-ui/index.html` de cada servicio

---

## 🗂️ Estructura del Repositorio

```
Proyecto_Ingenieria_FullStack1_V2/
│
├── services/                    # Microservicios de negocio (17)
│   ├── ms-auth/
│   ├── ms-logs/
│   ├── ms-users/
│   ├── ms-buildings/
│   ├── ms-security/
│   ├── ms-staff/
│   ├── ms-fleet/
│   ├── ms-schedule/
│   ├── ms-inventory/
│   ├── ms-quotes/
│   ├── ms-price-engine/
│   ├── ms-workorders/
│   ├── ms-logistics/
│   ├── ms-purchase/
│   ├── ms-payments/
│   ├── ms-billing/
│   └── ms-notifications/
│
├── infraestructure/             # Servicios de infraestructura
│   ├── api-gateway/
│   ├── eureka-server/
│   └── config-server/
│
├── docker/infra-docker/         # Archivos Docker Compose + init.sql
│
├── config-repo/                 # Configuración centralizada por servicio (*.yml)
│
└── README.md
```

Cada microservicio sigue la estructura de paquetes estándar:

```
ms-xxx/
└── src/main/java/com/logistica/ms_xxx/
    ├── controller/      # Manejo de solicitudes REST
    ├── service/         # Lógica de negocio e interfaces
    ├── repository/      # Acceso a datos (JpaRepository)
    ├── model/           # Entidades JPA y enumeraciones
    ├── dto/             # Objetos de transferencia de datos
    ├── client/          # Clientes OpenFeign entre microservicios
    └── exception/       # Excepciones tipadas y @ControllerAdvice
```

---

## 🚀 Pasos para Ejecutar

### Requisitos Previos

Asegúrate de tener instalado:
- **Docker** y **Docker Compose**
- **Git**
- Puertos disponibles: `3306` (MySQL), `8761` (Eureka), `9090` (Gateway externo), `8888` (Config Server), `9092` (Kafka)

### Instalación

**1. Clonar el repositorio**

```bash
git clone https://github.com/siminostroza/Proyecto_Ingenieria_Software.git
cd Proyecto_Ingenieria_Software
```

**2. Ejecutar el script de construcción y despliegue**

```bash
./build-project.sh
```

Este script se encarga automáticamente de:
- Compilar todos los microservicios con Maven (en contenedor Docker aislado)
- Construir las imágenes Docker de cada servicio
- Levantar los contenedores en el orden correcto mediante Docker Compose

> **Nota importante:** Si solo compilas con `./compile-all.sh`, los JARs se actualizan localmente pero las imágenes Docker no cambian. Para actualizar el entorno Docker después de cambios en el código, usa siempre `./build-project.sh` o `docker compose -f docker/infra-docker/compose.yml up --build -d`.

### Orden de Inicio de Contenedores

El sistema respeta un orden de encendido estricto configurado con *healthchecks* y `depends_on`:

1. MySQL (esquemas de base de datos por servicio)
2. Zookeeper + Kafka
3. Config Server (puerto 8888)
4. Eureka Server (puerto 8761)
5. API Gateway (puerto 8080 interno / **9090 externo**)
6. Servicios de identidad y seguridad (ms-auth, ms-users, ms-buildings, ms-security)
7. Servicios de cotización (ms-quotes, ms-price-engine)
8. Servicios de recursos (ms-staff, ms-fleet, ms-schedule, ms-inventory, ms-purchase)
9. Servicios de operación (ms-workorders, ms-logistics)
10. Servicios de cierre financiero (ms-payments, ms-billing, ms-notifications)
11. ms-logs (consume eventos de todos los servicios)

### Verificación del Despliegue

Una vez levantado el sistema:

- **Eureka Dashboard:** http://localhost:8761
- **API Gateway (punto de entrada):** http://localhost:9090
- **Swagger ms-workorders:** http://localhost:8097/swagger-ui/index.html
- **Swagger ms-logistics:** http://localhost:8093/swagger-ui/index.html
- **Swagger ms-quotes:** http://localhost:8088/swagger-ui/index.html

Todos los microservicios se registran automáticamente en Eureka y son accesibles a través del Gateway en `http://localhost:9090`.

### Pruebas de Endpoints

Realiza llamadas a través del Gateway con Postman u otra herramienta REST.

> **Estado actual:** Fases 1–4 completamente operativas. Gateway enruta a los 14 servicios implementados. Fase 5 (ms-billing, ms-payments, ms-notifications) pendiente de implementación.

---

## 📡 Referencia de Endpoints Implementados

### 👤 ms-users — `/api/users`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/users` | Lista todos los usuarios registrados | `200 OK` / `204 No Content` |
| GET | `/api/users/{id}` | Obtiene un usuario por ID | `200 OK` / `404 Not Found` |
| GET | `/api/users/existe?id={id}` | Verifica si existe un usuario por ID | `200 OK` con `true` o `false` |
| GET | `/api/users/existe?rut={rut}` | Verifica si existe un usuario por RUT | `200 OK` con `true` o `false` |
| GET | `/api/users/total-usuarios` | Retorna el total de usuarios registrados | `200 OK` con texto |
| POST | `/api/users` | Crea un nuevo usuario (body: `UserRegisterDTO`) | `201 Created` |
| PUT | `/api/users/{id}` | Actualiza los datos de un usuario por ID | `200 OK` |
| DELETE | `/api/users/{id}` | Elimina un usuario por ID (emite evento Kafka) | `204 No Content` |

**Notas:**
- Al crear un usuario, `ms-users` llama a `ms-auth` vía OpenFeign para crear la credencial.
- Al eliminar un usuario, se emite un evento Kafka que `ms-auth` y `ms-security` consumen en cascada.

---

### 🔐 ms-auth — `/api/auth`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/auth` | Lista todas las credenciales registradas | `200 OK` / `204 No Content` |
| GET | `/api/auth/existe?id={id}` | Verifica si existe una credencial por ID | `200 OK` con `true` o `false` |
| GET | `/api/auth/existe?username={username}` | Verifica si existe una credencial por username | `200 OK` con `true` o `false` |
| GET | `/api/auth/validate` | Valida un token JWT (cabecera `Authorization: Bearer <token>`) | `200 OK` / `401 Unauthorized` |
| POST | `/api/auth` | Crea una nueva credencial (receptor interno desde `ms-users`) | `201 Created` |
| POST | `/api/auth/login` | Autentica con username/password y retorna token JWT | `200 OK` con `LoginResponseDTO` |
| PUT | `/api/auth/{id}` | Actualiza una credencial por ID | `200 OK` |
| PUT | `/api/auth/usuario/{userId}` | Actualiza solo el username de una credencial por userId | `200 OK` |
| DELETE | `/api/auth/{id}` | Elimina una credencial por ID | `204 No Content` |

---

### 🏢 ms-buildings — `/api/edificios`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/edificios` | Lista todos los edificios registrados | `200 OK` / `204 No Content` |
| GET | `/api/edificios/{id}` | Obtiene un edificio por su ID | `200 OK` / `404 Not Found` |
| POST | `/api/edificios` | Registra un nuevo edificio (body: `EdificioRequestDTO`) | `201 Created` |
| PUT | `/api/edificios/{id}` | Actualiza los datos de un edificio existente | `200 OK` |
| DELETE | `/api/edificios/{id}` | Elimina un edificio por su ID | `204 No Content` |

---

### 🔒 ms-security — `/api/roles` y `/api/role-assignments`

#### Roles

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/roles` | Lista todos los roles | `200 OK` / `204 No Content` |
| POST | `/api/roles` | Crea un nuevo rol (body: `RoleRequestDTO`) | `201 Created` |
| PUT | `/api/roles/{id}` | Actualiza un rol por ID | `200 OK` |
| DELETE | `/api/roles/{id}` | Elimina un rol por ID | `204 No Content` |

#### Asignaciones de Roles

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/role-assignments` | Lista todas las asignaciones de roles | `200 OK` / `204 No Content` |
| POST | `/api/role-assignments` | Asigna un rol a un usuario | `201 Created` |
| PUT | `/api/role-assignments/{id}` | Actualiza una asignación por ID | `200 OK` |
| DELETE | `/api/role-assignments/{id}` | Elimina una asignación por ID | `204 No Content` |

---

### 👷 ms-staff — `/api/v1/staff`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/v1/staff` | Lista todos los técnicos | `200 OK` |
| GET | `/api/v1/staff/{id}` | Obtiene un técnico por ID | `200 OK` / `404 Not Found` |
| GET | `/api/v1/staff/especialidad/{especialidad}` | Filtra por especialidad (`GASFITER`, `ELECTRICISTA`, `SOLDADOR`) | `200 OK` |
| GET | `/api/v1/staff/disponibles` | Lista todos los técnicos disponibles | `200 OK` |
| GET | `/api/v1/staff/disponibles/{especialidad}` | Lista técnicos disponibles por especialidad | `200 OK` |
| POST | `/api/v1/staff` | Registra un nuevo técnico | `201 Created` |
| PUT | `/api/v1/staff/{id}` | Actualiza los datos de un técnico | `200 OK` |
| PATCH | `/api/v1/staff/{id}/disponibilidad?disponibilidad={bool}` | Cambia la disponibilidad de un técnico | `200 OK` |
| DELETE | `/api/v1/staff/{id}` | Elimina un técnico por ID | `204 No Content` |

---

### 🚗 ms-fleet — `/api/fleet`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/fleet` | Lista todos los vehículos | `200 OK` / `204 No Content` |
| GET | `/api/fleet/{id}` | Obtiene un vehículo por ID | `200 OK` / `404 Not Found` |
| POST | `/api/fleet` | Registra un nuevo vehículo | `201 Created` |
| PUT | `/api/fleet/{id}` | Actualiza los datos de un vehículo | `200 OK` |
| DELETE | `/api/fleet/{id}` | Elimina un vehículo por ID | `204 No Content` |
| POST | `/api/fleet/{id}/location` | Publica telemetría GPS al topic `fleet-gps-tracking` de Kafka | `200 OK` |

**Notas:**
- `POST /location` recibe `{ "latitud": ..., "longitud": ... }` y publica el evento a Kafka. `ms-notifications` (Fase 5) consumirá este topic.

---

### 📅 ms-schedule — `/api/schedule`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/schedule` | Lista todos los bloques horarios | `200 OK` / `204 No Content` |
| GET | `/api/schedule/{id}` | Obtiene un bloque por ID | `200 OK` / `404 Not Found` |
| GET | `/api/schedule/tecnico/{tecnicoId}` | Lista bloques de un técnico específico | `200 OK` / `204 No Content` |
| POST | `/api/schedule` | Crea un bloque horario (valida anti-solapamiento) | `201 Created` / `409 Conflict` |
| PUT | `/api/schedule/{id}` | Actualiza un bloque (valida anti-solapamiento excluyendo el propio) | `200 OK` |
| DELETE | `/api/schedule/{id}` | Elimina un bloque horario | `204 No Content` |

**Notas:**
- El sistema impide que un técnico tenga dos bloques solapados. Si hay solapamiento: `409 Conflict`.
- `ms-workorders` llama a este servicio vía Feign al asignar un técnico (`PUT /asignar`).

---

### 📦 ms-inventory — `/api/inventory`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/inventory` | Lista todos los materiales | `200 OK` / `204 No Content` |
| GET | `/api/inventory/{id}` | Obtiene un material por ID | `200 OK` / `404 Not Found` |
| POST | `/api/inventory` | Registra un nuevo material | `201 Created` |
| PUT | `/api/inventory/{id}` | Actualiza los datos de un material | `200 OK` |
| DELETE | `/api/inventory/{id}` | Elimina un material | `204 No Content` |
| PATCH | `/api/inventory/{id}/consume` | Descuenta unidades del stock (no permite stock negativo) | `200 OK` / `409 Conflict` |
| PUT | `/api/inventory/{id}/restock` | Aumenta el stock de un material | `200 OK` |

---

### 🛒 ms-purchase — `/api/purchase`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/purchase` | Lista todas las compras | `200 OK` / `204 No Content` |
| GET | `/api/purchase/{id}` | Obtiene una compra por ID | `200 OK` / `404 Not Found` |
| POST | `/api/purchase` | Registra una compra y reabastece el inventario automáticamente vía Feign | `201 Created` |
| PUT | `/api/purchase/{id}` | Actualiza una compra | `200 OK` |
| DELETE | `/api/purchase/{id}` | Elimina una compra por ID | `204 No Content` |

**Notas:**
- Al registrar una compra, `ms-purchase` llama a `ms-inventory /restock` vía Feign para incrementar el stock automáticamente.

---

### 📋 ms-quotes — `/api/cotizaciones`

Documentación Swagger disponible en `/swagger-ui/index.html`.

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/cotizaciones` | Lista todas las cotizaciones | `200 OK` / `204 No Content` |
| GET | `/api/cotizaciones/{id}` | Obtiene una cotización por ID | `200 OK` / `404 Not Found` |
| GET | `/api/cotizaciones/usuario/{userId}` | Lista cotizaciones de un usuario | `200 OK` / `204 No Content` |
| GET | `/api/cotizaciones/estado?status={status}` | Filtra por estado (`PENDING`, `SENT`, `ACCEPTED`, `REJECTED`) | `200 OK` / `204 No Content` |
| POST | `/api/cotizaciones` | Crea una cotización (body: `CotizacionRequestDTO`) | `201 Created` |
| PUT | `/api/cotizaciones/{id}` | Actualiza una cotización existente | `200 OK` |
| DELETE | `/api/cotizaciones/{id}` | Elimina una cotización por ID | `204 No Content` |

**Notas:**
- El cliente envía `{ buildingId, userId, categoria, horasTrabajo, unidadesMaterial }`. El `estimatedAmount` lo calcula `ms-price-engine` automáticamente.
- Incluye **Circuit Breaker** (Resilience4j): si algún servicio remoto falla, el circuito se abre.

---

### 💲 ms-price-engine — `/api/precios`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| POST | `/api/precios/calcular` | Calcula el precio total de una reparación | `200 OK` con `PrecioResponseDTO` |

**Body requerido (`PrecioRequestDTO`):**
```json
{
  "categoria": "PLOMERIA",
  "horasTrabajo": 3.5,
  "unidadesMaterial": 2
}
```

**Tarifas base (inicializadas automáticamente al arrancar):**

| Categoría | Costo/hora | Costo/unidad |
|:---|:---|:---|
| PLOMERIA | $25.000 | $8.000 |
| ELECTRICIDAD | $30.000 | $12.000 |
| GAS | $35.000 | $15.000 |

---

### ⚙️ ms-workorders — `/api/workorders`

Documentación Swagger disponible en `/swagger-ui/index.html`.

| Método | Endpoint | Descripción | Request Body | Respuesta exitosa |
|:---|:---|:---|:---|:---|
| POST | `/api/workorders` | Crea una orden de trabajo (estado inicial: PENDING) | `OrdenTrabajoRequestDTO` | `201 Created` |
| GET | `/api/workorders` | Lista todas las órdenes | — | `200 OK` / `204 No Content` |
| GET | `/api/workorders/{id}` | Obtiene una orden por ID | — | `200 OK` / `404 Not Found` |
| GET | `/api/workorders/estado/{estado}` | Filtra por estado | — | `200 OK` / `204 No Content` |
| GET | `/api/workorders/building/{buildingId}` | Filtra por edificio | — | `200 OK` / `204 No Content` |
| GET | `/api/workorders/tecnico/{tecnicoId}` | Filtra por técnico asignado | — | `200 OK` / `204 No Content` |
| PUT | `/api/workorders/{id}/asignar` | Asigna un técnico (PENDING → ASSIGNED) | `AsignarTecnicoRequestDTO` | `200 OK` |
| PUT | `/api/workorders/{id}/iniciar` | Inicia el trabajo (ASSIGNED → IN_PROGRESS) | — | `200 OK` |
| PUT | `/api/workorders/{id}/completar` | Completa la orden (IN_PROGRESS → COMPLETED) | `CambioEstadoRequestDTO` | `200 OK` |
| PUT | `/api/workorders/{id}/cancelar` | Cancela la orden (cualquier estado excepto COMPLETED) | `CambioEstadoRequestDTO` | `200 OK` |
| DELETE | `/api/workorders/{id}` | Elimina una orden (solo si PENDING o CANCELLED) | — | `204 No Content` |

**Notas:**
- Al asignar (`/asignar`): valida técnico en `ms-users`, edificio en `ms-buildings`, y crea un bloque horario en `ms-schedule` automáticamente.
- Dos órdenes PENDING se generan automáticamente al iniciar el servicio por primera vez (buildingId=1 PLOMERIA, buildingId=2 ELECTRICIDAD).

**Body `AsignarTecnicoRequestDTO`:**
```json
{
  "tecnicoId": 2,
  "fechaInicio": "2026-06-27T18:00:00",
  "fechaFin": "2026-06-27T20:00:00"
}
```

---

### 🗺️ ms-logistics — `/api/logistics`

Documentación Swagger disponible en `/swagger-ui/index.html`.

| Método | Endpoint | Descripción | Request Body | Respuesta exitosa |
|:---|:---|:---|:---|:---|
| POST | `/api/logistics/asignaciones` | Crea asignación logística (calcula distancia y tiempo con Haversine) | `AsignacionRequestDTO` | `201 Created` |
| GET | `/api/logistics/asignaciones` | Lista todas las asignaciones | — | `200 OK` / `204 No Content` |
| GET | `/api/logistics/asignaciones/{id}` | Obtiene una asignación por ID | — | `200 OK` / `404 Not Found` |
| GET | `/api/logistics/asignaciones/workorder/{woId}` | Filtra por orden de trabajo | — | `200 OK` / `204 No Content` |
| GET | `/api/logistics/asignaciones/vehiculo/{vehiculoId}` | Filtra por vehículo | — | `200 OK` / `204 No Content` |
| GET | `/api/logistics/asignaciones/tecnico/{tecnicoId}` | Filtra por técnico | — | `200 OK` / `204 No Content` |
| GET | `/api/logistics/asignaciones/estado/{estado}` | Filtra por estado | — | `200 OK` / `204 No Content` |
| PUT | `/api/logistics/asignaciones/{id}/en-ruta` | Marca como en ruta (PLANIFICADO → EN_RUTA) | — | `200 OK` |
| PUT | `/api/logistics/asignaciones/{id}/completar` | Completa la entrega (EN_RUTA → COMPLETADO) | `CambioEstadoRequestDTO` | `200 OK` |
| PUT | `/api/logistics/asignaciones/{id}/cancelar` | Cancela la asignación | `CambioEstadoRequestDTO` | `200 OK` |
| GET | `/api/logistics/calcular-distancia` | Calcula distancia y tiempo entre dos coordenadas | params: `lat1,lon1,lat2,lon2` | `200 OK` |

**Notas:**
- Al crear (`POST /asignaciones`): valida que la orden esté en estado `ASSIGNED` vía Feign a `ms-workorders`, valida el vehículo vía Feign a `ms-fleet`, calcula distancia Haversine y tiempo estimado (40 km/h).
- El `tecnicoId` se copia automáticamente desde la orden de trabajo.

**Body `AsignacionRequestDTO`:**
```json
{
  "ordenTrabajoId": 3,
  "vehiculoId": 1,
  "latitudOrigen": -33.4569,
  "longitudOrigen": -70.6483,
  "latitudDestino": -33.4372,
  "longitudDestino": -70.6506,
  "fechaSalida": "2026-06-27T18:00:00"
}
```

---

### 📄 ms-logs — `/api/logs`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/logs` | Consulta logs paginados (con filtros opcionales) | `200 OK` con `Page<LogResponseDTO>` |

**Parámetros de query:**

| Parámetro | Tipo | Default | Descripción |
|:---|:---|:---|:---|
| `serviceName` | String | (ninguno) | Filtra por nombre de servicio (ej: `ms-auth`) |
| `level` | String | (ninguno) | Filtra por nivel (`INFO`, `WARN`, `ERROR`) |
| `page` | int | `0` | Número de página |
| `size` | int | `50` | Registros por página |

---

## 🔄 Flujo End-to-End Completo (Fases 1–4)

```
1. POST /api/users          → Crear usuario
2. POST /api/auth/login     → Obtener token JWT
3. POST /api/edificios      → Registrar edificio
4. POST /api/cotizaciones   → Crear cotización (precio calculado automáticamente)
5. POST /api/workorders     → Crear orden de trabajo (estado: PENDING)
6. PUT  /api/workorders/{id}/asignar   → Asignar técnico (estado: ASSIGNED)
7. POST /api/logistics/asignaciones   → Asignar vehículo + calcular ruta (estado: PLANIFICADO)
8. PUT  /api/logistics/asignaciones/{id}/en-ruta  → Despachar (estado: EN_RUTA)
9. PUT  /api/workorders/{id}/iniciar   → Iniciar trabajo (estado: IN_PROGRESS)
10. PUT /api/logistics/asignaciones/{id}/completar → Llegada confirmada (estado: COMPLETADO)
11. PUT /api/workorders/{id}/completar → Trabajo finalizado (estado: COMPLETED)
```

Todos los endpoints accesibles vía Gateway: `http://localhost:9090/{path}`

---

## ☁️ Despliegue Remoto (Railway) — Subconjunto Mínimo

> **Estado:** Configuración local preparada (perfil `remote`, Dockerfile alternativo, variables documentadas). El despliegue real en Railway **no se ha ejecutado** — queda a cargo del equipo, siguiendo los pasos de esta sección.

### Subconjunto de servicios a desplegar

Para una primera validación remota se desplegará un subconjunto mínimo que cubre el flujo completo de cotización → orden de trabajo, sin dependencias de Kafka:

| Servicio | Incluido | Motivo de inclusión/exclusión |
|:---|:---|:---|
| `eureka-server` | ✅ | Descubrimiento de servicios, requerido por todos |
| `config-server` | ✅ | Configuración centralizada, requerido por todos |
| `api-gateway` | ✅ | Punto de entrada único |
| `ms-auth` | ✅ | Login/JWT, requerido para el flujo |
| `ms-users` | ✅ | Gestión de usuarios |
| `ms-buildings` | ✅ | Catálogo de edificios |
| `ms-price-engine` | ✅ | Cálculo de precios para cotizaciones |
| `ms-quotes` | ✅ | Cotizaciones |
| `ms-workorders` | ✅ | Órdenes de trabajo |
| `ms-logs` | ❌ | Requiere Kafka, fuera de alcance del subconjunto remoto |
| `ms-security` | ❌ | No crítico para el flujo mínimo |
| `ms-staff`, `ms-fleet`, `ms-schedule`, `ms-inventory`, `ms-purchase` | ❌ | No críticos para el flujo mínimo |
| `ms-logistics` | ❌ | Depende de `ms-fleet`, excluido |
| `ms-payments`, `ms-billing`, `ms-notifications` | ❌ | Fuera del flujo mínimo de validación |

> No se intenta levantar Kafka en este despliegue remoto. Cualquier servicio que dependa de Kafka queda excluido del subconjunto inicial.

### Perfil `remote`

Se agregó un perfil Spring `remote` en `config-repo/`, análogo al perfil `docker` ya existente, pero apuntando a variables de entorno en vez de hostnames fijos:

- `config-repo/application-remote.yml`: overrides globales (`spring.datasource.*` vía `${DATABASE_URL}`/`${DATABASE_USERNAME}`/`${DATABASE_PASSWORD}`, `eureka.client.serviceUrl.defaultZone` vía `${EUREKA_URL}`, `eureka.instance.hostname` vía `${RAILWAY_STATIC_URL}`).
- `config-repo/{ms-auth,ms-users,ms-buildings,ms-price-engine,ms-quotes,ms-workorders}-remote.yml`: overrides de `server.port` (vía `${PORT}`) y datasource por servicio. `ms-price-engine`, `ms-quotes` y `ms-workorders` agregan además `app.gateway-url` (vía `${APP_GATEWAY_URL}`), consumido por sus `SwaggerConfig.java` para anunciar la URL pública del Gateway sin hardcodear el hostname en código.

El Config Server resuelve estos archivos automáticamente por convención de nombres (`{application}-{profile}.yml`) cuando cada servicio arranca con `SPRING_PROFILES_ACTIVE=remote`, sin requerir cambios de código en el Config Server.

### Imagen remota del Config Server

El `Dockerfile` original de `config-server` monta `config-repo` como **volumen externo**, lo cual funciona en Docker Compose pero no en Railway (no soporta bind-mounts de host). Se agregó `infraestructure/config-server/Dockerfile.remote`, que en su lugar **copia `config-repo` dentro de la imagen** en build time. El build debe ejecutarse con la raíz del repositorio como contexto:

```bash
docker build -f infraestructure/config-server/Dockerfile.remote -t config-server-remote .
```

### Orden de despliegue sugerido

1. **MySQL** (plugin administrado de Railway, o una base por servicio usando el `createDatabaseIfNotExist=true` ya presente en las URLs JDBC)
2. `eureka-server`
3. `config-server` (con la imagen `Dockerfile.remote`)
4. `ms-auth` → `ms-users` → `ms-buildings` → `ms-price-engine` → `ms-quotes` → `ms-workorders`
5. `api-gateway` (al final, ya que enruta hacia todos los anteriores)

### Variables de entorno por servicio

| Variable | Aplica a | Descripción |
|:---|:---|:---|
| `SPRING_PROFILES_ACTIVE=remote` | Todos los servicios de negocio + `api-gateway` | Activa el perfil `remote` |
| `SPRING_CONFIG_IMPORT=optional:configserver:http://{config-server-url}/` | Todos los servicios de negocio + `api-gateway` | URL pública/interna del Config Server en Railway |
| `DATABASE_URL`, `DATABASE_USERNAME`, `DATABASE_PASSWORD` | Todos los servicios con base de datos | Credenciales de la instancia MySQL en Railway |
| `EUREKA_URL` | Todos los servicios | URL del Eureka Server en Railway (`http://{host}/eureka/`) |
| `RAILWAY_STATIC_URL` | Todos los servicios | Hostname público asignado por Railway, usado para el registro en Eureka |
| `APP_GATEWAY_URL` | `ms-price-engine`, `ms-quotes`, `ms-workorders` | URL pública del API Gateway, mostrada en Swagger como servidor alternativo |
| `PORT` | Todos los servicios | Puerto inyectado por Railway (ya soportado vía `${PORT:...}` en cada `*.yml`) |

### Verificación posterior al despliegue

Una vez desplegado por el equipo:

1. Confirmar en el dashboard de Eureka que los 9 servicios del subconjunto aparecen `UP`.
2. Probar el flujo mínimo a través del Gateway: `POST /api/auth/login`, `POST /api/users`, `POST /api/edificios`, `POST /api/cotizaciones`.
3. Reemplazar los placeholders de esta sección por las URLs reales una vez completado el despliegue (Eureka, API Gateway, Swagger de `ms-workorders`/`ms-quotes`).
