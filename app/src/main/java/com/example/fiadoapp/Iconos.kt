package com.example.fiadoapp

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.unit.dp

/**
 * Íconos de línea dibujados a mano (estilo del diseño), así no hace falta
 * agregar ninguna librería de íconos al proyecto.
 */
object Iconos {

    private fun circulo(cx: Float, cy: Float, r: Float) =
        "M${cx - r} ${cy}a$r $r 0 1 0 ${2 * r} 0a$r $r 0 1 0 ${-2 * r} 0"

    private fun icono(nombre: String, vararg trazos: String): ImageVector =
        ImageVector.Builder(
            name = nombre,
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            trazos.forEach { d ->
                addPath(
                    pathData = addPathNodes(d),
                    fill = null,
                    stroke = SolidColor(Color.Black),
                    strokeLineWidth = 2f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round
                )
            }
        }.build()

    val AgregarPersona = icono(
        "agregar_persona",
        "M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2",
        circulo(9f, 7f, 4f),
        "M19 8v6",
        "M22 11h-6"
    )
    val Persona = icono(
        "persona",
        "M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2",
        circulo(12f, 7f, 4f)
    )
    val Buscar = icono("buscar", circulo(11f, 11f, 8f), "M21 21l-4.3-4.3")
    val Siguiente = icono("siguiente", "M9 18l6-6-6-6")
    val Atras = icono("atras", "M15 18l-6-6 6-6")
    val Telefono = icono(
        "telefono",
        "M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 " +
            "19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 " +
            ".7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 " +
            "12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z"
    )
    val Mensaje = icono("mensaje", "M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z")
    val Compartir = icono(
        "compartir",
        circulo(18f, 5f, 3f),
        circulo(6f, 12f, 3f),
        circulo(18f, 19f, 3f),
        "M8.59 13.51l6.83 3.98",
        "M15.41 6.51l-6.82 3.98"
    )
    val FlechaAbono = icono("flecha_abono", "M17 7L7 17", "M17 17H7V7")
    val FlechaFiado = icono("flecha_fiado", "M7 7h10v10", "M7 17L17 7")
    val Mas = icono("mas", "M5 12h14", "M12 5v14")
    val Check = icono("check", "M20 6L9 17l-5-5")
    val MasOpciones = icono(
        "mas_opciones",
        circulo(5f, 12f, 1f),
        circulo(12f, 12f, 1f),
        circulo(19f, 12f, 1f)
    )
}
