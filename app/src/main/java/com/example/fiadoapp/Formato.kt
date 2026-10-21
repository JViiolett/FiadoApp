package com.example.fiadoapp

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

private val simbolos = DecimalFormatSymbols(Locale.ROOT).apply { groupingSeparator = '.' }
private val formatoMiles = DecimalFormat("#,###", simbolos)

/** 18000 → "18.000" */
fun miles(n: Long): String = formatoMiles.format(n)

/** 18000 → "$18.000" */
fun pesos(n: Int): String = "$" + miles(n.toLong())

/** "3109876543" → "310 987 6543" */
fun telefonoBonito(t: String): String =
    if (t.length == 10) "${t.substring(0, 3)} ${t.substring(3, 6)} ${t.substring(6)}" else t

/** "Don Pedro" → "DP", "Luz Ángela" → "LÁ" */
fun iniciales(nombre: String): String =
    nombre.trim().split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
