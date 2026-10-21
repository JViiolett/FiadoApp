package com.example.fiadoapp

import android.content.Intent
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Tipos de movimiento: FIADO suma a la deuda, ABONO la resta. */
const val FIADO = "FIADO"
const val ABONO = "ABONO"

/** Un registro del historial de un cliente: qué se fió o qué abonó, cuánto y cuándo. */
data class Movimiento(
    val tipo: String,
    val monto: Int,
    val descripcion: String,
    val fecha: Long = System.currentTimeMillis()
) {
    /** "15 oct" */
    val fechaCorta: String
        get() = SimpleDateFormat("d MMM", Locale("es", "CO")).format(Date(fecha)).replace(".", "")

    /** Lo que se muestra como título en el historial. */
    val titulo: String
        get() = if (tipo == FIADO) "Fiado · $descripcion" else "Abono"
}

/** Deuda = todo lo fiado menos todo lo abonado. */
fun calcularDeuda(movimientos: List<Movimiento>): Int =
    movimientos.sumOf { if (it.tipo == FIADO) it.monto else -it.monto }

// ---------- Enviar y recibir el historial por Intent ----------
// Un Intent no puede llevar una lista de objetos propios directamente,
// así que se manda como cuatro listas paralelas (una por cada dato).

private const val EXTRA_TIPOS = "mov_tipos"
private const val EXTRA_MONTOS = "mov_montos"
private const val EXTRA_DESCRIPCIONES = "mov_descripciones"
private const val EXTRA_FECHAS = "mov_fechas"

fun Intent.ponerMovimientos(movimientos: List<Movimiento>) {
    putStringArrayListExtra(EXTRA_TIPOS, ArrayList(movimientos.map { it.tipo }))
    putIntegerArrayListExtra(EXTRA_MONTOS, ArrayList(movimientos.map { it.monto }))
    putStringArrayListExtra(EXTRA_DESCRIPCIONES, ArrayList(movimientos.map { it.descripcion }))
    putExtra(EXTRA_FECHAS, movimientos.map { it.fecha }.toLongArray())
}

fun Intent.leerMovimientos(): List<Movimiento> {
    val tipos = getStringArrayListExtra(EXTRA_TIPOS) ?: return emptyList()
    val montos = getIntegerArrayListExtra(EXTRA_MONTOS) ?: return emptyList()
    val descripciones = getStringArrayListExtra(EXTRA_DESCRIPCIONES) ?: return emptyList()
    val fechas = getLongArrayExtra(EXTRA_FECHAS) ?: return emptyList()
    return tipos.indices.map { i -> Movimiento(tipos[i], montos[i], descripciones[i], fechas[i]) }
}
