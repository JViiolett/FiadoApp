# FiadoApp (Llamatico)

App Android para llevar los fiados de una tienda: lista de clientes con lo que deben, historial de fiados y abonos, y recordatorios por llamada, SMS o compartir.

Taller de codificación · Sesión 6 · Diseño de Aplicaciones para Dispositivos Móviles
Universitaria de Colombia · Ingeniería de Software · Docente: Ing. Kevin Torrez

## Equipo

- Juana Violeta Rojas Espejo
- Kevin Palacios
- Cristian Arrieta

## Qué hace

| Pantalla | Qué muestra |
| --- | --- |
| `MainActivity` | Lista de clientes, total por cobrar, buscador |
| `DetalleActivity` | Saldo, historial de movimientos, fiar y abonar, llamar / SMS / compartir |
| `NuevoClienteActivity` | Formulario con validaciones para registrar un cliente |

- Intent explícito para ir de la lista al detalle y al formulario, con respuesta por `setResult`.
- Intents implícitos: `ACTION_DIAL`, `ACTION_SENDTO` (SMS) y `ACTION_SEND` (compartir).
- Ciclo de vida registrado en Logcat con los tags `CICLO_MAIN` y `CICLO_DETALLE`.

## Cómo correrla

1. Clonar el repositorio y abrir la carpeta en Android Studio.
2. Esperar a que Gradle sincronice.
3. Presionar **Run** con un emulador o un celular conectado.

Hecho con Kotlin y Jetpack Compose (compileSdk 37, minSdk 24).
