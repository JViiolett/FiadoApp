# Bitácora · Taller de codificación Sesión 6 · FiadoApp

**Asignatura:** Diseño de Aplicaciones para Dispositivos Móviles
**Docente:** Ing. Kevin Torrez
**Estudiante:** Juana Violeta Rojas Espejo

---

## 1. Secuencia de Logcat (filtro `tag:CICLO`)

### Al abrir el detalle (tocar un cliente)

```
CICLO_MAIN      onPause
CICLO_DETALLE   onCreate
CICLO_DETALLE   onStart
CICLO_DETALLE   onResume
CICLO_MAIN      onStop
```

### Al volver a la lista (Atrás o "Guardar y volver")

```
CICLO_DETALLE   onPause
CICLO_MAIN      onRestart
CICLO_MAIN      onStart
CICLO_MAIN      onResume
CICLO_DETALLE   onStop
CICLO_DETALLE   onDestroy
```

> Verifica en tu Logcat que salga igual; si algo cambia, anota aquí lo que viste.

### Explicación

- La lista (MainActivity) se **pausa** antes de que el detalle se cree, porque deja de tener el foco. Pero solo se **detiene** (`onStop`) cuando el detalle ya está visible por completo: Android no oculta la pantalla de abajo hasta que la de arriba está lista.
- Al volver, la lista reaparece con **`onRestart`**, no con `onCreate`: nunca se destruyó, seguía debajo en la pila de Activities.
- El detalle sí llega a **`onDestroy`**, porque Atrás (o `finish()`) lo saca de la pila.

## 2. Intent explícito vs. Intent implícito

| | Intent explícito | Intent implícito |
|---|---|---|
| Qué indica | La clase exacta que se abre (`DetalleActivity::class.java`) | Una acción (`ACTION_DIAL`, `ACTION_SENDTO`, `ACTION_SEND`) y unos datos (`tel:`, `smsto:`, texto) |
| Quién lo atiende | Siempre esa Activity de mi app | Cualquier app instalada que sepa hacer la acción; si hay varias, Android muestra un selector |
| Uso en FiadoApp | Lista → Detalle, Lista → Nuevo cliente | Llamar, enviar SMS, compartir |

Para devolver datos se usa el launcher (`rememberLauncherForActivityResult` + `StartActivityForResult`). La pantalla que responde arma un Intent, llama a `setResult(RESULT_OK, intent)` y a `finish()`. Si el usuario sale con Atrás, llega `RESULT_CANCELED` y la lista no cambia.

## 3. Mapa de pantallas del proyecto

> Esta tabla debe llevar las pantallas de **la app del proyecto de su equipo**, no las de FiadoApp. Abajo queda llena con FiadoApp como referencia; reemplácenla con las suyas.

| Pantalla (Activity) | Qué muestra | Desde dónde se abre y con qué datos | Intents implícitos que usa |
|---|---|---|---|
| ⭐ MainActivity | Lista de clientes con su deuda y el total por cobrar | Es la pantalla de inicio (launcher); no recibe datos | Ninguno |
| DetalleActivity | La deuda de un cliente y sus acciones | Desde la lista, con id, nombre, teléfono y deuda. Devuelve id y nueva deuda | Llamar (DIAL), SMS (SENDTO), compartir (SEND) |
| NuevoClienteActivity | Formulario: nombre, teléfono y deuda inicial | Desde el botón "Nuevo cliente" de la lista, sin datos. Devuelve nombre, teléfono y deuda | Ninguno |

⭐ = pantalla más importante, la que conviene construir primero.

## 4. Evidencias por entregar

- [ ] Captura de la lista después de un abono
- [ ] Captura del detalle de un cliente
- [ ] Esta bitácora con la secuencia de Logcat
- [ ] Mapa de pantallas del proyecto del equipo
- [ ] Carpeta del proyecto comprimida **sin la carpeta `build`** (o enlace a GitHub)
