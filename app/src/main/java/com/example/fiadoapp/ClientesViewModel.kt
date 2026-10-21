package com.example.fiadoapp

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.util.Calendar

/** Fecha de ejemplo: día y mes de 2026. */
private fun dia(dia: Int, mes: Int): Long =
    Calendar.getInstance().apply { set(2026, mes - 1, dia, 10, 0, 0) }.timeInMillis

/** Guarda la lista de clientes. Sobrevive a que el celular gire. */
class ClientesViewModel : ViewModel() {

    val clientes = mutableStateListOf(
        Cliente(
            1, "Don Pedro", "", "3109876543",
            listOf(
                Movimiento(FIADO, 23000, "Arroz y aceite", dia(12, 10)),
                Movimiento(ABONO, 5000, "Abono", dia(15, 10))
            )
        ),
        Cliente(
            2, "Doña Marta", "", "3124567890",
            listOf(
                Movimiento(FIADO, 30000, "Mercado de la semana", dia(8, 10)),
                Movimiento(FIADO, 12500, "Pan, leche y huevos", dia(16, 10))
            )
        ),
        Cliente(
            3, "Andrés", "Gómez", "3209871234",
            listOf(Movimiento(FIADO, 12300, "Gaseosas y pasabocas", dia(14, 10)))
        ),
        Cliente(
            4, "Carlos", "Ruiz", "3001234567",
            listOf(
                Movimiento(FIADO, 10000, "Café y azúcar", dia(5, 10)),
                Movimiento(ABONO, 3000, "Abono", dia(11, 10))
            )
        ),
        Cliente(
            5, "Luz", "Ángela", "3157654321",
            listOf(
                Movimiento(FIADO, 8000, "Jabón y detergente", dia(2, 10)),
                Movimiento(ABONO, 8000, "Abono", dia(9, 10))
            )
        )
    )

    val totalPorCobrar: Int
        get() = clientes.sumOf { it.deuda }

    val clientesConSaldo: Int
        get() = clientes.count { it.deuda > 0 }

    /** Reemplaza el historial de un cliente con el que devolvió el detalle. */
    fun actualizarMovimientos(id: Int, movimientos: List<Movimiento>) {
        val posicion = clientes.indexOfFirst { it.id == id }
        if (posicion >= 0) clientes[posicion] = clientes[posicion].copy(movimientos = movimientos)
    }

    fun eliminar(id: Int) {
        clientes.removeAll { it.id == id }
    }

    fun agregar(nombre: String, apellido: String, telefono: String, deuda: Int, descripcion: String) {
        val nuevoId = (clientes.maxOfOrNull { it.id } ?: 0) + 1
        val movimientos = if (deuda > 0) listOf(Movimiento(FIADO, deuda, descripcion)) else emptyList()
        clientes.add(Cliente(nuevoId, nombre, apellido, telefono, movimientos))
    }
}
