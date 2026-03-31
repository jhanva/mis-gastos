# Proyecto Android: Mis gastos

## Contexto general
Quiero crear una aplicación Android llamada **“Mis gastos”** para uso personal, optimizada inicialmente para un **Samsung S23 Ultra**. La aplicación debe estar diseñada con una experiencia moderna, minimalista, fluida y bien estructurada, pero sobre todo debe funcionar **100% offline**, sin depender de internet para ninguna funcionalidad principal.

La app debe servir para **registrar, organizar, consultar y administrar gastos personales** de forma rápida e intuitiva. El enfoque principal es que sea una herramienta confiable, visualmente limpia, fácil de usar en el día a día y bien construida técnicamente.

---

## Objetivo del proyecto
Construir una aplicación Android nativa para registrar gastos personales, con categorías editables, buena organización visual, navegación clara, almacenamiento local robusto y arquitectura moderna, de forma que el producto sea mantenible, escalable y agradable de usar.

---

## Nombre de la app
**Mis gastos**

---

## Enfoque funcional
La aplicación debe permitir al usuario:

1. Registrar gastos fácilmente.
2. Asignar cada gasto a una categoría.
3. Crear, editar y eliminar categorías.
4. Consultar el historial de gastos.
5. Filtrar y buscar información.
6. Ver resúmenes simples de gasto.
7. Administrar todo completamente offline.
8. Tener una interfaz minimalista, moderna y con buenos íconos.

---

## Restricción principal
La aplicación debe funcionar **100% offline**.

Esto significa:
- No debe requerir conexión a internet para crear, editar, eliminar o consultar datos.
- Toda la información debe almacenarse localmente en el dispositivo.
- La app debe ser usable en cualquier momento sin sincronización remota.
- Si en el futuro se desea backup o sincronización, debe quedar preparado para extenderse, pero la versión actual no debe depender de ello.

---

## Plataforma objetivo
- Android
- Uso principal en Samsung S23 Ultra
- Debe adaptarse bien a pantallas grandes
- Debe contemplar modo claro y modo oscuro
- Debe respetar buenas prácticas modernas de Android

---

## Requerimientos técnicos recomendados
Quiero que la app se desarrolle con tecnologías modernas, sólidas y apropiadas para Android nativo.

### Stack esperado
- **Lenguaje:** Kotlin
- **UI:** Jetpack Compose
- **Diseño visual:** Material 3
- **Arquitectura:** MVVM + Repository Pattern
- **Persistencia local:** Room
- **Preferencias/configuración:** DataStore
- **Inyección de dependencias:** Hilt
- **Navegación:** Navigation Compose
- **Concurrencia:** Coroutines + Flow

### Motivo
Quiero una app moderna, mantenible, rápida, bien organizada, alineada con las mejores prácticas actuales de Android, y que esté preparada para crecer sin rehacer la base.

---

## Filosofía de diseño
La app debe verse:
- minimalista
- limpia
- moderna
- elegante
- ligera visualmente
- fácil de entender
- sin saturación de colores
- con iconografía consistente
- con jerarquía visual clara

### Lineamientos visuales
- Evitar pantallas cargadas.
- Usar suficiente espacio en blanco.
- Usar cards limpias y bien espaciadas.
- Usar tipografía legible y moderna.
- Usar íconos bonitos, consistentes y claros.
- Mantener una paleta sobria con un color principal elegante.
- La experiencia debe sentirse premium, pero simple.

---

## Concepto funcional de la app

La app debe girar alrededor de estas entidades:

### 1. Gasto
Cada gasto es un registro individual y debe tener como mínimo:
- id único
- valor
- descripción o nombre corto
- categoría
- fecha
- hora
- método de pago
- nota opcional

Campos sugeridos:
- `id`
- `amount`
- `title`
- `description`
- `categoryId`
- `paymentMethod`
- `date`
- `time`
- `notes`
- `createdAt`
- `updatedAt`

### 2. Categoría
Las categorías deben ser administrables por el usuario.

Cada categoría debe tener:
- id único
- nombre
- ícono
- color
- estado activo/inactivo opcional

Campos sugeridos:
- `id`
- `name`
- `iconName`
- `colorHex`
- `isActive`
- `createdAt`
- `updatedAt`

### 3. Método de pago
Inicialmente puede manejarse como un texto o enum, por ejemplo:
- efectivo
- tarjeta
- transferencia
- otro

En una siguiente fase podría convertirse en entidad propia, pero para la V1 puede ser simple.

---

## Alcance de la V1
La primera versión debe incluir obligatoriamente:

### A. Pantalla de inicio
Debe mostrar:
- resumen del gasto del día
- resumen del gasto del mes
- últimos gastos registrados
- accesos rápidos
- posibilidad clara de registrar un nuevo gasto

### B. Registro de gasto
Debe existir una pantalla o modal donde el usuario pueda:
- ingresar monto
- elegir categoría
- seleccionar fecha
- seleccionar hora
- escribir una descripción
- elegir método de pago
- agregar una nota opcional
- guardar el gasto

La experiencia debe ser rápida y cómoda. Registrar un gasto no debe sentirse pesado.

### C. Historial de gastos
Debe existir una vista de listado con:
- todos los gastos
- orden por fecha descendente
- agrupación opcional por día o mes
- posibilidad de entrar al detalle
- opción para editar o eliminar

### D. Gestión de categorías
Debe existir una sección donde el usuario pueda:
- ver todas las categorías
- crear nueva categoría
- editar categoría
- eliminar categoría
- asignar ícono
- elegir color

### E. Filtros y búsqueda
Debe ser posible:
- buscar por texto
- filtrar por categoría
- filtrar por rango de fechas
- ordenar por monto o fecha

### F. Configuración básica
Debe existir una pantalla de ajustes con:
- tema claro/oscuro
- moneda
- formato de fecha
- preferencias básicas de visualización

---

## Funcionalidades deseables para una segunda fase
Estas no son obligatorias en la V1, pero la arquitectura debería permitir agregarlas luego:

- presupuestos por categoría
- metas de ahorro
- exportación a CSV o JSON
- backup local
- importación de datos
- gráfico mensual
- comparación entre meses
- gastos recurrentes
- cuentas o bolsillos separados
- etiquetas adicionales
- adjuntar recibos/imágenes

---

## Flujo esperado del usuario

### Flujo principal
1. El usuario abre la app.
2. Ve un resumen general de sus gastos.
3. Presiona “Agregar gasto”.
4. Ingresa el valor.
5. Selecciona una categoría.
6. Añade descripción si lo desea.
7. Escoge método de pago.
8. Guarda el registro.
9. El gasto aparece de inmediato en el historial y en los resúmenes.

### Flujo de categorías
1. El usuario entra a la sección de categorías.
2. Ve la lista de categorías existentes.
3. Puede crear una nueva.
4. Ingresa nombre.
5. Selecciona ícono.
6. Selecciona color.
7. Guarda.
8. La categoría queda disponible inmediatamente para nuevos gastos.

### Flujo de edición
1. El usuario entra al historial.
2. Selecciona un gasto.
3. Visualiza detalle.
4. Puede editar campos o eliminar el gasto.

---

## Consideraciones UX importantes

### Registro rápido
La app debe priorizar que registrar un gasto tome pocos pasos.

### Validaciones
- El monto debe ser obligatorio.
- La categoría debe ser obligatoria.
- No permitir guardar gastos inválidos.
- Mostrar mensajes claros y elegantes de validación.

### Confirmaciones
- Confirmar antes de eliminar un gasto.
- Confirmar antes de eliminar una categoría si ya tiene gastos asociados.
- Idealmente impedir borrar una categoría usada sin antes reasignar o advertir.

### Retroalimentación visual
- Mostrar snackbars o mensajes breves al guardar, editar o eliminar.
- La interfaz debe sentirse reactiva y confiable.

---

## Navegación sugerida
Usar navegación simple, clara y moderna.

### Opción recomendada
Barra inferior con 4 secciones:
1. **Inicio**
2. **Gastos**
3. **Categorías**
4. **Ajustes**

### Alternativa
También se puede usar una estructura con FAB principal para “Agregar gasto”.

---

## Estructura visual de pantallas esperadas

### 1. Inicio
Contenido sugerido:
- saludo o título simple
- card con total del día
- card con total del mes
- lista de últimos movimientos
- botón flotante para agregar gasto

### 2. Gastos
Contenido sugerido:
- barra de búsqueda
- chips de filtros
- listado de gastos
- cada gasto con:
  - ícono de categoría
  - nombre o descripción
  - fecha
  - monto

### 3. Detalle de gasto
Contenido sugerido:
- monto destacado
- categoría
- fecha y hora
- método de pago
- nota
- botones de editar y eliminar

### 4. Categorías
Contenido sugerido:
- grid o lista de categorías
- botón para agregar
- visual con color e ícono
- opción de editar/eliminar

### 5. Ajustes
Contenido sugerido:
- tema
- moneda
- formato de fecha
- opciones futuras de exportación/importación

---

## Requerimientos de arquitectura

La app debe organizarse de forma limpia.

### Capas sugeridas
- `ui`
- `domain`
- `data`
- `di`
- `navigation`
- `utils`

### Dentro de `data`
- `local`
- `repository`
- `model`
- `mapper`

### Dentro de `ui`
- `screens`
- `components`
- `theme`
- `viewmodel`

### Objetivo
Mantener separación clara de responsabilidades, facilitar pruebas y permitir crecimiento futuro.

---

## Persistencia de datos

### Base de datos local
Usar Room para almacenar:
- gastos
- categorías
- relaciones necesarias

### Requisitos
- consultas eficientes
- soporte para filtros
- soporte para ordenamiento
- actualizaciones reactivas con Flow
- persistencia segura en dispositivo

### Preferencias
Usar DataStore para:
- tema
- moneda
- configuración visual
- opciones de usuario

---

## Comportamiento offline esperado
La app debe:
- abrir rápido
- cargar datos sin internet
- actualizar interfaces al instante cuando se agregue o edite información
- no depender de ningún servicio externo
- no incluir login
- no requerir autenticación
- no fallar por ausencia de conectividad

---

## Requisitos de diseño técnico detallados

### Sobre el código
- Código limpio y legible
- Nombres claros
- Buen uso de `sealed classes`, `state holders` y `UiState`
- Separación entre lógica y UI
- Reutilización de componentes
- Componentes Compose bien organizados
- Evitar lógica pesada en composables
- ViewModels responsables del estado de pantalla

### Sobre el estado
- La UI debe ser reactiva
- El estado debe venir del ViewModel
- Los cambios en base de datos deben reflejarse automáticamente en pantalla

### Sobre la mantenibilidad
- Estructura preparada para agregar módulos futuros
- No usar hacks rápidos si rompen escalabilidad
- Priorizar claridad sobre complejidad innecesaria

---

## Requerimientos de experiencia visual

### Estilo general
- limpio
- premium
- minimalista
- sin exceso de elementos
- iconografía moderna
- transiciones suaves

### Íconos
Usar un set consistente, idealmente basado en Material Symbols o equivalente.

### Colores
- neutros elegantes
- uno o dos colores de acento
- soporte para modo oscuro
- colores de categorías visibles pero no estridentes

---

## Reglas de negocio iniciales

1. Todo gasto debe pertenecer a una categoría.
2. Toda categoría debe tener nombre.
3. El monto debe ser mayor que cero.
4. La fecha del gasto debe poder editarse.
5. El usuario puede eliminar gastos.
6. El usuario puede crear y eliminar categorías.
7. Si una categoría ya tiene gastos asociados, al eliminarla debe manejarse correctamente:
   - impedir la eliminación, o
   - solicitar reasignación, o
   - advertir claramente la consecuencia

---

## Qué se espera del resultado
Se espera un proyecto funcional y bien estructurado que incluya:

- arquitectura moderna Android
- app nativa en Kotlin
- interfaz con Jetpack Compose
- base de datos local con Room
- gestión completa de gastos
- gestión completa de categorías
- navegación clara
- diseño minimalista
- funcionamiento offline total
- código organizado y mantenible

---

## Entregables esperados
El resultado ideal del proyecto debe incluir:

1. Proyecto Android estructurado.
2. Pantallas base implementadas.
3. Modelos de datos.
4. Base de datos Room.
5. Repositorios.
6. ViewModels.
7. Componentes reutilizables.
8. Tema visual.
9. Navegación funcional.
10. CRUD de gastos.
11. CRUD de categorías.
12. Ajustes básicos.
13. App usable completamente offline.

---

## Orden recomendado de implementación

### Fase 1: Base del proyecto
- crear proyecto Android
- configurar Kotlin, Compose, Hilt, Room, Navigation, DataStore
- definir arquitectura de paquetes
- configurar tema visual

### Fase 2: Modelo de datos
- crear entidad de gastos
- crear entidad de categorías
- crear DAO
- crear base de datos Room
- crear repositorios

### Fase 3: Lógica base
- crear casos de uso o capa domain
- crear ViewModels
- definir UiState por pantalla

### Fase 4: UI principal
- pantalla Inicio
- pantalla Lista de gastos
- pantalla Crear/editar gasto
- pantalla Categorías
- pantalla Ajustes

### Fase 5: Experiencia de usuario
- validaciones
- feedback visual
- confirmaciones
- filtros
- búsqueda

### Fase 6: Pulido
- modo oscuro
- animaciones sutiles
- íconos consistentes
- revisión de responsive para pantalla grande
- limpieza de código

---

## Prioridades del proyecto
Orden de prioridad:

1. Funcionamiento offline real
2. Registro rápido de gastos
3. Arquitectura sólida
4. Interfaz minimalista y bonita
5. Gestión flexible de categorías
6. Navegación clara
7. Escalabilidad futura

---

## Instrucción final
Diseñar y desarrollar la aplicación **“Mis gastos”** con enfoque Android nativo moderno, 100% offline, minimalista y mantenible, priorizando experiencia de usuario, rendimiento local, arquitectura limpia y facilidad de uso diaria.