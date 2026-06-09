# Dog Finder - ADM Mobile 2026

## Integrantes
- Ximena González
- Manuel Sainz

## Escenario elegido
Dog Finder.

La aplicación permite explorar razas de perros utilizando la Dog CEO API, visualizar imágenes por raza y guardar razas favoritas en una galería local.

## Tecnologías utilizadas
- Kotlin
- Jetpack Compose
- Material Design 3
- Navigation Compose
- Retrofit
- Gson
- Room
- Coil
- Coroutines
- Fastlane
- JUnit
- Detekt
- OWASP Dependency Check

## Funcionalidades
- Listado de razas de perros.
- Búsqueda por raza.
- Visualización de detalle por raza.
- Imágenes aleatorias desde API externa.
- Gestión de favoritos con persistencia local(galeria personal).
- Navegar entre distintas pantallas utilizando Navigation Compose.

## Arquitectura
El proyecto está organizado en capas:

- `data/local`: base de datos Room, DAO y entidades.
- `data/remote`: modelos de API y comunicación con Dog CEO API mediante Retrofit.
- `data/repository`: implementación del repositorio(Implementación de la lógica de acceso a datos).
- `domain`: contiene modelos e interfaces de negocio.
- `ui`: pantallas, navegación y tema visual.
- `platform`: componentes Android nativos como Service(DogSyncService), BroadcastReceiver(NetworkChangeReceiver) y ContentProvider(FavoriteBreedsProvider).

## API utilizada
Dog CEO API  
https://dog.ceo/dog-api/

Endpoints principales:
- `/breeds/list/all`
- `/breed/{breed}/images`
- `/breed/{breed}/images/random`
- `/breeds/image/random`

## Pantallas

### Home

Permite:

- Buscar razas
- Explorar imágenes
- Navegar al detalle

### Detail

Permite:

- Visualizar información de la raza
- Consultar galería de imágenes
- Compartir imágenes mediante Intent
- Agregar o quitar favoritos

### Favorites

Permite:

- Visualizar favoritos guardados
- Eliminar favoritos

### Settings

Permite:

- Gestionar preferencias de usuario
- Configurar opciones visuales

---
## Persistencia local
Se utiliza Room para guardar razas favoritas en una base de datos local.
Entidades principales:

- FavoriteBreedEntity
Acceso mediante:
- FavoriteBreedDao

Base de datos:
- DogFinderDatabase
Los datos permanecen disponibles incluso luego de cerrar la aplicación.

---

## Componentes Android implementados
### Activity
MainActivity
Punto de entrada principal de la aplicación.
### Service
DogSyncService
Servicio utilizado para ejecutar tareas de sincronización.
### BroadcastReceiver
NetworkChangeReceiver
Detecta cambios de conectividad y notifica al usuario.
### ContentProvider
FavoriteBreedsProvider
Permite exponer información mediante un Content Provider.
### Intent
Se utiliza Intent.ACTION_SEND para compartir imágenes desde la pantalla de detalle.

---
## Testing
Pruebas unitarias implementadas con JUnit.
Ejecutar:

```bash
gradlew.bat test
```
---

## Fastlane
Automatización de tareas de build y testing.
Lanes disponibles:
### test
```bash
fastlane test
```
Ejecuta pruebas unitarias.
### build_debug
```bash
fastlane build_debug
```
Genera APK Debug.

---

## Seguridad y calidad
### SAST - Detekt
Análisis estático de código Kotlin.
Ejecutar:
```bash
gradlew.bat detekt
```
Reporte:
```text
app/build/reports/detekt/
```
### Dependency Check
Análisis de vulnerabilidades en dependencias.
Ejecutar:
```bash
gradlew.bat dependencyCheckAnalyze
```
Reporte:
```text
app/build/reports/dependency-check/
```
---
## Pruebas
Se incluyen pruebas unitarias con JUnit.

Comando:
```bash
./gradlew test
```
En Windows:
```bash
gradlew.bat test
```
## Fastlane
Se utiliza Fastlane para automatizar tareas de build y testing.

```md
## Instalación de Fastlane
gem install fastlane
```

```bash
fastlane test
fastlane build_debug
```

## Seguridad y calidad
Se utiliza Dependency Check para detectar vulnerabilidades en dependencias.
Comando:
```bash
./gradlew dependencyCheckAnalyze
```
En Windows:
```bash
gradlew.bat dependencyCheckAnalyze
```
---

## Cómo ejecutar el proyecto
1. Clonar el repositorio.
2. Abrirlo en Android Studio.
3. Esperar la sincronización de Gradle.
4. Crear o seleccionar un emulador Android.
5. Ejecutar la configuración `app`.

## Entregables
- Código fuente en GitHub.
- README.
- Mockups light/dark.
- Reportes de seguridad/calidad.
- Video demo de 30 a 60 segundos.
- Presentación final.