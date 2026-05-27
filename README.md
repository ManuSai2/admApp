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
- Room
- Coil
- Fastlane
- JUnit

## Funcionalidades
- Listado de razas de perros.
- Búsqueda por raza.
- Visualización de detalle por raza.
- Imágenes aleatorias desde API externa.
- Gestión de favoritos con persistencia local.
- Navegación entre Home, Detalle y Favoritos.
- Uso de Service, BroadcastReceiver y ContentProvider.

## Arquitectura
El proyecto está organizado en capas:

- `data/local`: base de datos Room, DAO y entidades.
- `data/remote`: modelos de API y servicio Retrofit.
- `data/repository`: implementación del repositorio.
- `domain`: modelos e interfaces de dominio.
- `ui`: pantallas, navegación y tema visual.
- `platform`: componentes Android nativos como Service, BroadcastReceiver y ContentProvider.

## API utilizada
Dog CEO API  
https://dog.ceo/dog-api/

Endpoints principales:
- `/breeds/list/all`
- `/breed/{breed}/images`
- `/breed/{breed}/images/random`
- `/breeds/image/random`

## Persistencia local
Se utiliza Room para guardar razas favoritas en una base de datos local.

## Componentes Android requeridos
- Activity: `MainActivity`
- Service: `DogSyncService`
- BroadcastReceiver: `NetworkChangeReceiver`
- ContentProvider: `FavoriteBreedsProvider`
- Intents: usados para navegación/acciones del sistema según implementación

## Cómo ejecutar el proyecto
1. Clonar el repositorio.
2. Abrirlo en Android Studio.
3. Esperar la sincronización de Gradle.
4. Crear o seleccionar un emulador Android.
5. Ejecutar la configuración `app`.

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

## Entregables
- Código fuente en GitHub.
- README.
- Mockups light/dark.
- Reportes de seguridad/calidad.
- Video demo de 30 a 60 segundos.
- Presentación final de máximo 5 diapositivas.