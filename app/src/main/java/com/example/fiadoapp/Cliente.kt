package com.example.fiadoapp

/** Un cliente de la tienda y su historial de fiados y abonos. */
data class Cliente(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val telefono: String,
    val movimientos: List<Movimiento> = emptyList()
) {
    val nombreCompleto: String
        get() = "$nombre $apellido".trim()

    /** La deuda sale del historial, así nunca se descuadra. */
    val deuda: Int
        get() = calcularDeuda(movimientos)
}
