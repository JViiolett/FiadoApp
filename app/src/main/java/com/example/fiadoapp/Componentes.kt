package com.example.fiadoapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fiadoapp.ui.theme.Borde
import com.example.fiadoapp.ui.theme.Gris
import com.example.fiadoapp.ui.theme.GrisClaro
import com.example.fiadoapp.ui.theme.Lila
import com.example.fiadoapp.ui.theme.Morado
import com.example.fiadoapp.ui.theme.Rojo
import com.example.fiadoapp.ui.theme.Tinta

/** Círculo lila con las iniciales del cliente. */
@Composable
fun Avatar(nombre: String, tamano: Dp = 52.dp, letra: TextUnit = 18.sp) {
    Box(
        modifier = Modifier.size(tamano).clip(CircleShape).background(Lila),
        contentAlignment = Alignment.Center
    ) {
        Text(iniciales(nombre), color = Morado, fontWeight = FontWeight.Bold, fontSize = letra)
    }
}

/** Botón redondo gris con ícono y texto debajo (Llamar, Recordar, Compartir). */
@Composable
fun BotonRedondo(icono: ImageVector, texto: String, alTocar: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = alTocar,
            shape = CircleShape,
            color = com.example.fiadoapp.ui.theme.GrisSuave,
            modifier = Modifier.size(60.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icono, contentDescription = texto, tint = Tinta, modifier = Modifier.size(22.dp))
            }
        }
        Spacer(Modifier.padding(top = 8.dp))
        Text(texto, color = Gris, fontSize = 15.sp)
    }
}

/** Chip con un valor rápido ($2.000, $5.000, Todo…). */
@Composable
fun ChipMonto(texto: String, alTocar: () -> Unit) {
    Surface(
        onClick = alTocar,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Borde)
    ) {
        Text(
            texto,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = Tinta,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp)
        )
    }
}

/** Texto gris pequeño encima de un campo. */
@Composable
fun Etiqueta(texto: String) {
    Text(
        texto,
        color = Gris,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

/** Campo de texto redondeado y blanco, como en el diseño. */
@Composable
fun CampoRedondo(
    valor: String,
    alCambiar: (String) -> Unit,
    placeholder: String,
    error: String? = null,
    icono: ImageVector? = null,
    teclado: KeyboardOptions = KeyboardOptions.Default,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = valor,
        onValueChange = alCambiar,
        placeholder = { Text(placeholder, color = GrisClaro, fontSize = 17.sp) },
        leadingIcon = icono?.let { { Icon(it, contentDescription = null, tint = Gris) } },
        singleLine = true,
        isError = error != null,
        supportingText = error?.let { { Text(it) } },
        keyboardOptions = teclado,
        textStyle = TextStyle(fontSize = 17.sp, color = Tinta),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            errorContainerColor = Color.White,
            unfocusedBorderColor = Borde,
            focusedBorderColor = Morado,
            errorBorderColor = Rojo
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/** Campo grande de dinero: "$ 5.000". Guarda solo los dígitos. */
@Composable
fun CampoMonto(valor: String, alCambiar: (String) -> Unit, conError: Boolean = false) {
    val estilo = TextStyle(fontSize = 38.sp, fontWeight = FontWeight.Bold, color = Tinta)
    val forma = RoundedCornerShape(18.dp)
    BasicTextField(
        value = valor,
        onValueChange = { nuevo -> alCambiar(nuevo.filter { it.isDigit() }.trimStart('0').take(8)) },
        textStyle = estilo,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = MilesTransformation(),
        cursorBrush = SolidColor(Morado),
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { campo ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(forma)
                    .background(Color.White)
                    .border(1.dp, if (conError) Rojo else Borde, forma)
                    .padding(horizontal = 22.dp, vertical = 16.dp)
            ) {
                Text("$", style = estilo.copy(color = GrisClaro))
                Spacer(Modifier.width(10.dp))
                Box {
                    if (valor.isEmpty()) Text("0", style = estilo.copy(color = GrisClaro))
                    campo()
                }
            }
        }
    )
}

/** Muestra "5000" como "5.000" mientras se escribe, sin que el cursor salte. */
class MilesTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digitos = text.text
        if (digitos.isEmpty()) return TransformedText(text, OffsetMapping.Identity)
        val formateado = miles(digitos.toLong())
        val n = digitos.length
        val t = formateado.length
        val mapa = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val k = n - offset // dígitos a la derecha del cursor
                val desdeElFinal = if (k > 0) k + (k - 1) / 3 else 0
                return (t - desdeElFinal).coerceIn(0, t)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val m = t - offset // caracteres a la derecha del cursor
                val k = m - m / 4
                return (n - k).coerceIn(0, n)
            }
        }
        return TransformedText(AnnotatedString(formateado), mapa)
    }
}
