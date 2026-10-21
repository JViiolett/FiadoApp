package com.example.fiadoapp

/** Cada función devuelve el mensaje de error, o null si el dato está bien. */

const val DEUDA_MAXIMA = 10_000_000

fun validarNombre(valor: String): String? {
    val limpio = valor.trim()
    return when {
        limpio.isEmpty() -> "Escribe el nombre"
        limpio.length < 2 -> "El nombre debe tener al menos 2 letras"
        !limpio.all { it.isLetter() || it == ' ' } -> "El nombre solo puede tener letras"
        else -> null
    }
}

/** El apellido es opcional, pero si se escribe debe ser válido. */
fun validarApellido(valor: String): String? {
    val limpio = valor.trim()
    return when {
        limpio.isEmpty() -> null
        limpio.length < 2 -> "El apellido debe tener al menos 2 letras"
        !limpio.all { it.isLetter() || it == ' ' } -> "El apellido solo puede tener letras"
        else -> null
    }
}

fun validarTelefono(telefono: String, existentes: List<String>): String? = when {
    telefono.isEmpty() -> "Escribe el teléfono"
    telefono.length != 10 -> "El teléfono debe tener 10 dígitos"
    !telefono.startsWith("3") -> "El celular debe empezar por 3"
    telefono in existentes -> "Ya hay un cliente con este teléfono"
    else -> null
}

fun validarMontoMaximo(valor: Int): String? =
    if (valor > DEUDA_MAXIMA) "No puede pasar de ${pesos(DEUDA_MAXIMA)}" else null

fun validarDescripcion(descripcion: String): String? {
    val limpio = descripcion.trim()
    return when {
        limpio.isEmpty() -> "Escribe qué se llevó el cliente"
        limpio.length < 3 -> "Escribe al menos 3 letras"
        else -> null
    }
}
