# Mis Gastos

Aplicacion Android para registrar gastos personales de forma local y sin dependencias remotas. El proyecto esta construido con Kotlin y Jetpack Compose, y guarda la informacion en el dispositivo usando Room y DataStore.

## Estado actual

- Build debug verificado
- Build release verificado
- Tests unitarios verificados
- Lint debug verificado
- Persistencia local activa
- Sin login ni backend

## Funcionalidad principal

- Panel de inicio con resumen del dia y del mes
- Registro, edicion y eliminacion de gastos
- Clasificacion por categorias
- Filtros, orden y agrupacion del historial
- Configuracion de tema y formato de fecha
- Montos mostrados siempre en COP
- Datos locales con Room y DataStore

## Stack tecnico

- Kotlin
- Jetpack Compose
- Navigation Compose
- Hilt
- Room
- DataStore
- Gradle Kotlin DSL

## Requisitos

- JDK 17
- Android SDK configurado localmente
- Gradle Wrapper incluido en el repositorio

## Compilacion y validacion

Desde la raiz del proyecto:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat assembleRelease
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
```

APK debug generado en:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Persistencia y migraciones

- La base de datos local usa Room
- El proyecto exporta el schema en `app/schemas`
- No se permiten migraciones destructivas silenciosas
- Cualquier cambio futuro de esquema debe incluir migracion explicita

## Estructura

```text
app/src/main/java/com/misgastos
app/src/main/res
app/src/test
app/schemas
```
