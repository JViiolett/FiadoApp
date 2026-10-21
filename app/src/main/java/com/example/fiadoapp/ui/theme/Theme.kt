package com.example.fiadoapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta del diseño Llamatico
val Morado = Color(0xFF5B2BA6)            // botones principales
val MoradoMarca = Color(0xFF5E2CA5)       // nombre de la app
val MoradoDeshabilitado = Color(0xFFC3B0DE)
val Lila = Color(0xFFEFE7FB)              // fondo de los avatares
val Fondo = Color(0xFFF9F8FB)
val Tinta = Color(0xFF17151C)             // texto principal
val Gris = Color(0xFF6F6B76)              // texto secundario
val GrisClaro = Color(0xFF9B97A1)         // placeholders y el signo $
val Borde = Color(0xFFE6E3EA)
val GrisSuave = Color(0xFFF0EEF2)         // tarjeta de saldo y botones redondos
val Rojo = Color(0xFFC53030)
val RojoSuave = Color(0xFFFCE8E8)
val Verde = Color(0xFF1F8A5B)
val VerdeSuave = Color(0xFFDDF3E7)

private val Colores = lightColorScheme(
    primary = Morado,
    onPrimary = Color.White,
    secondary = MoradoMarca,
    background = Fondo,
    onBackground = Tinta,
    surface = Fondo,
    onSurface = Tinta,
    surfaceContainerLow = Color.White,
    error = Rojo
)

@Composable
fun FiadoAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Colores, content = content)
}
