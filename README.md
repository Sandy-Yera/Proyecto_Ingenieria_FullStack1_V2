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
| `api-gateway` | 8080 | Punto de entrada único con filtro JWT |
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

### 💰 Fase 2 y 3 — Ciclo de Cotización y Tarifas

| Servicio | Puerto | Descripción | Estado |
|:---|:---|:---|:---|
| `ms-quotes` | 8088 | Creación y seguimiento de cotizaciones (Swagger + Circuit Breaker) | ✅ Implementado |
| `ms-price-engine` | 8095 | Motor automático de cálculo de precios por categoría, horas y materiales | ✅ Implementado |

### 🚚 Fase 4 — Recursos y Logística

| Servicio | Puerto | Descripción | Estado |
|:---|:---|:---|:---|
| `ms-staff` | 8085 | Gestión del personal técnico con disponibilidad y especialidad | ✅ Implementado |
| `ms-fleet` | 8086 | Control de la flota de vehículos | 🔲 Esqueleto |
| `ms-schedule` | 8098 | Agendamiento y disponibilidad | 🔲 Esqueleto |
| `ms-inventory` | 8089 | Control de materiales e insumos | 🔲 Esqueleto |

### ⚙️ Fase 5 — Operación y Ejecución

| Servicio | Puerto | Descripción | Estado |
|:---|:---|:---|:---|
| `ms-workorders` | 8097 | Gestión de órdenes de trabajo (flujo crítico) | 🔲 Esqueleto |
| `ms-logistics` | 8093 | Orquestador central de operaciones | 🔲 Esqueleto |
| `ms-purchase` | 8096 | Gestión de compras de materiales | 🔲 Esqueleto |

### 🧾 Fase 6 — Cierre, Pagos y Notificaciones

| Servicio | Puerto | Descripción | Estado |
|:---|:---|:---|:---|
| `ms-payments` | 8091 | Procesamiento de pagos | 🔲 Esqueleto |
| `ms-billing` | 8092 | Generación de facturas legales | 🔲 Esqueleto |
| `ms-notifications` | 8094 | Notificaciones asíncronas vía Kafka | 🔲 Esqueleto |

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
- Scripts SQL de inicialización de base de datos

### CRUD Completo
- Operaciones **GET, POST, PUT, DELETE (y PATCH en ms-staff)** implementadas en los servicios activos
- Endpoints retornan JSON estructurado con `ResponseEntity`
- Códigos HTTP adecuados según la operación y resultado

### Validaciones con Bean Validation (JSR 380)
- Anotaciones de validación en DTOs (`@NotNull`, `@NotBlank`, `@Size`, `@Min`, etc.)
- Respuestas consistentes ante entradas inválidas
- Separación limpia entre entidades JPA y objetos de transferencia

### Manejo de Excepciones
- `@ControllerAdvice` para manejo centralizado de errores
- Excepciones tipadas por dominio: `EntityNotFoundException`, `EntityConflictException`, `EntityBadRequestException`, `EntityCreationException`, `CertificacionDuplicadaException`
- Respuestas estructuradas con códigos HTTP correctos (400, 404, 409, 500, etc.)

### Reglas de Negocio
- Gestión de estados de cotización: `PENDING → SENT → ACCEPTED / REJECTED`
- Categorías de servicio: `PLOMERIA`, `ELECTRICIDAD`, `GAS`
- Restricciones de disponibilidad de técnicos por especialidad (`GASFITER`, `ELECTRICISTA`, `SOLDADOR`)
- Control de integridad referencial entre entidades del dominio

### Comunicación entre Microservicios
- **Síncrona:** OpenFeign para consultas en tiempo real (`ms-quotes` → `ms-users` + `ms-buildings` + `ms-price-engine`)
- **Asíncrona:** Apache Kafka para logs del sistema y eventos de ciclo de vida (eliminación de usuarios)
- **Resiliencia:** Circuit Breaker (Resilience4j) en `ms-quotes` con configuración de ventana deslizante y habilitado automáticamente sobre todos los clientes Feign

### Logs Estructurados
- `ms-logs` consume eventos Kafka de múltiples servicios y expone consulta paginada
- Filtros por `serviceName` y `level` con paginación configurable
- Los servicios `ms-auth`, `ms-users` y `ms-staff` emiten logs vía Kafka

### Documentación Swagger
- `ms-quotes` expone documentación OpenAPI 3.0 con anotaciones `@Operation`, `@ApiResponses`, `@Tag`
- Accesible en `/swagger-ui.html` o `/v3/api-docs` del servicio

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
- Puertos disponibles: `3306` (MySQL), `8761` (Eureka), `8080` (Gateway), `8888` (Config Server), `9092` (Kafka)

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
- Compilar todos los microservicios con Maven
- Construir las imágenes Docker de cada servicio
- Levantar los contenedores en el orden correcto mediante Docker Compose

### Orden de Inicio de Contenedores

El sistema respeta un orden de encendido estricto configurado con *healthchecks* y `depends_on`:

1. MySQL (esquemas de base de datos por servicio)
2. Zookeeper + Kafka
3. Config Server (puerto 8888)
4. Eureka Server (puerto 8761)
5. API Gateway (puerto 8080)
6. Servicios de identidad y seguridad (ms-auth, ms-users, ms-buildings, ms-security)
7. Servicios de cotización (ms-quotes, ms-price-engine)
8. Servicios de recursos y logística (ms-staff, ms-fleet, ms-schedule, ms-inventory)
9. Servicios de operación (ms-workorders, ms-logistics, ms-purchase)
10. Servicios de pagos y notificaciones (ms-payments, ms-billing, ms-notifications)
11. ms-logs (consume eventos de todos los servicios)

### Verificación del Despliegue

Una vez levantado el sistema:

- **Eureka Dashboard:** http://localhost:8761
- **API Gateway:** http://localhost:8080
- **Swagger ms-quotes:** http://localhost:8088/swagger-ui.html

Todos los microservicios se registran automáticamente en Eureka y son accesibles a través del Gateway.

### Pruebas de Endpoints

Realiza llamadas directamente a través del Gateway con Postman u otra herramienta REST.

> **Estado actual:** Microservicios con CRUD completo operativo: `ms-users`, `ms-auth`, `ms-logs`, `ms-buildings`, `ms-staff`, `ms-security`, `ms-quotes`, `ms-price-engine`.

---

## 📡 Referencia de Endpoints Implementados

### 👤 ms-users — `/api/users`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/users` | Lista todos los usuarios registrados | `200 OK` / `204 No Content` |
| GET | `/api/users/existe?id={id}` | Verifica si existe un usuario por ID | `200 OK` con `true` o `false` |
| GET | `/api/users/existe?rut={rut}` | Verifica si existe un usuario por RUT | `200 OK` con `true` o `false` |
| GET | `/api/users/total-usuarios` | Retorna el total de usuarios registrados | `200 OK` con texto |
| POST | `/api/users` | Crea un nuevo usuario (body: `UserRegisterDTO`) | `201 Created` |
| PUT | `/api/users/{id}` | Actualiza los datos de un usuario por ID | `200 OK` |
| DELETE | `/api/users/{id}` | Elimina un usuario por ID (emite evento Kafka) | `204 No Content` |

**Notas:**
- `GET /existe` acepta solo un parámetro a la vez. Si se envían ambos retorna `400 Bad Request`.
- Al crear un usuario, `ms-users` llama a `ms-auth` vía OpenFeign para crear la credencial.
- Al eliminar un usuario, se emite un evento Kafka que `ms-auth` consume para eliminar la credencial en cascada.

---

### 🔐 ms-auth — `/api/auth`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/auth` | Lista todas las credenciales registradas | `200 OK` / `204 No Content` |
| GET | `/api/auth/existe?id={id}` | Verifica si existe una credencial por ID | `200 OK` con `true` o `false` |
| GET | `/api/auth/existe?username={username}` | Verifica si existe una credencial por username | `200 OK` con `true` o `false` |
| GET | `/api/auth/validate` | Valida un token JWT (cabecera `Authorization: Bearer <token>`) | `200 OK` / `401 Unauthorized` |
| POST | `/api/auth` | Crea una nueva credencial (receptor OpenFeign desde `ms-users`) | `201 Created` |
| POST | `/api/auth/login` | Autentica con username/password y retorna token JWT | `200 OK` con `LoginResponseDTO` |
| PUT | `/api/auth/{id}` | Actualiza una credencial por ID | `200 OK` |
| PUT | `/api/auth/usuario/{userId}` | Actualiza solo el username de una credencial por userId | `200 OK` |
| DELETE | `/api/auth/{id}` | Elimina una credencial por ID | `204 No Content` |

**Notas:**
- `POST /api/auth/login` recibe `{ "username": "...", "password": "..." }` y retorna `{ token, type, expiresIn, userId, username }`.
- `GET /api/auth/validate` es usado por el API Gateway para verificar tokens antes de redirigir peticiones.
- `POST /api/auth` es un endpoint receptor interno; en producción es invocado únicamente por `ms-users`.

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
| POST | `/api/role-assignments` | Asigna un rol a un usuario (body: `RoleAssignmentRequestDTO`) | `201 Created` |
| PUT | `/api/role-assignments/{id}` | Actualiza una asignación por ID | `200 OK` |
| DELETE | `/api/role-assignments/{id}` | Elimina una asignación por ID | `204 No Content` |

---

### 👷 ms-staff — `/api/v1/staff`

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/v1/staff` | Lista todos los técnicos | `200 OK` |
| GET | `/api/v1/staff/{id}` | Obtiene un técnico por ID | `200 OK` / `404 Not Found` |
| GET | `/api/v1/staff/especialidad/{especialidad}` | Filtra técnicos por especialidad (`GASFITER`, `ELECTRICISTA`, `SOLDADOR`) | `200 OK` |
| GET | `/api/v1/staff/disponibles` | Lista todos los técnicos disponibles | `200 OK` |
| GET | `/api/v1/staff/disponibles/{especialidad}` | Lista técnicos disponibles por especialidad | `200 OK` |
| POST | `/api/v1/staff` | Registra un nuevo técnico (body: `StaffRequestDTO`) | `201 Created` |
| PUT | `/api/v1/staff/{id}` | Actualiza los datos de un técnico | `200 OK` |
| PATCH | `/api/v1/staff/{id}/disponibilidad?disponibilidad={bool}` | Cambia la disponibilidad de un técnico | `200 OK` |
| DELETE | `/api/v1/staff/{id}` | Elimina un técnico por ID | `204 No Content` |

---

### 📋 ms-quotes — `/api/cotizaciones`

Documentación Swagger disponible en `/swagger-ui.html`.

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| GET | `/api/cotizaciones` | Lista todas las cotizaciones | `200 OK` / `204 No Content` |
| GET | `/api/cotizaciones/{id}` | Obtiene una cotización por ID | `200 OK` / `404 Not Found` |
| GET | `/api/cotizaciones/usuario/{userId}` | Lista cotizaciones de un usuario específico | `200 OK` / `204 No Content` |
| GET | `/api/cotizaciones/estado?status={status}` | Filtra por estado (`PENDING`, `SENT`, `ACCEPTED`, `REJECTED`) | `200 OK` / `204 No Content` |
| POST | `/api/cotizaciones` | Crea una nueva cotización (body: `CotizacionRequestDTO`) | `201 Created` |
| PUT | `/api/cotizaciones/{id}` | Actualiza una cotización existente | `200 OK` |
| DELETE | `/api/cotizaciones/{id}` | Elimina una cotización por ID | `204 No Content` |

**Notas:**
- Categorías disponibles: `PLOMERIA`, `ELECTRICIDAD`, `GAS`.
- Al crear una cotización, `ms-quotes` valida el usuario vía `ms-users`, el edificio vía `ms-buildings` y consulta el precio automático vía `ms-price-engine` usando OpenFeign. El cliente no envía el monto: lo calcula el motor de precios.
- Incluye **Circuit Breaker** (Resilience4j): si cualquiera de los tres servicios remotos falla, el circuito se abre automáticamente.

---

### 💲 ms-price-engine — `/api/precios`

Documentación Swagger disponible en `/swagger-ui.html`.

| Método | Endpoint | Descripción | Respuesta exitosa |
|:---|:---|:---|:---|
| POST | `/api/precios/calcular` | Calcula el precio total de una reparación (body: `PrecioRequestDTO`) | `200 OK` con `PrecioResponseDTO` |

**Body requerido (`PrecioRequestDTO`):**
```json
{
  "categoria": "PLOMERIA",
  "horasTrabajo": 3.5,
  "unidadesMaterial": 2
}
```

**Respuesta (`PrecioResponseDTO`):**
```json
{
  "categoria": "PLOMERIA",
  "horasTrabajo": 3.5,
  "unidadesMaterial": 2,
  "costoLaboral": 87500.0,
  "costoMateriales": 16000.0,
  "montoTotal": 103500.0
}
```

**Notas:**
- Categorías disponibles: `PLOMERIA`, `ELECTRICIDAD`, `GAS`.
- Las tarifas base se inicializan automáticamente al arrancar el servicio (`DataInitializer`): PLOMERIA (25.000/hora, 8.000/unidad), ELECTRICIDAD (30.000/hora, 12.000/unidad), GAS (35.000/hora, 15.000/unidad).
- Este endpoint es llamado internamente por `ms-quotes` vía OpenFeign al crear una cotización.

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

**Ejemplos:**
- `/api/logs?page=0&size=20`
- `/api/logs?level=ERROR`
- `/api/logs?serviceName=ms-auth&level=INFO&page=0&size=10`

**Notas:**
- Los logs son generados automáticamente por `ms-users`, `ms-auth` y `ms-staff` mediante **Kafka** (`KafkaLogProducer`). No se crean manualmente vía API.
- Este endpoint es de solo lectura (consulta).
