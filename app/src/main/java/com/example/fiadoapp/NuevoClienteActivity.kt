package com.example.fiadoapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fiadoapp.ui.theme.Borde
import com.example.fiadoapp.ui.theme.FiadoAppTheme
import com.example.fiadoapp.ui.theme.Fondo
import com.example.fiadoapp.ui.theme.Gris
import com.example.fiadoapp.ui.theme.GrisClaro
import com.example.fiadoapp.ui.theme.GrisSuave
import com.example.fiadoapp.ui.theme.Morado
import com.example.fiadoapp.ui.theme.Rojo
import com.example.fiadoapp.ui.theme.Tinta

/** Pantalla que devuelve un cliente nuevo a la lista. */
class NuevoClienteActivity : ComponentActivity() {

    companion object {
        const val EXTRA_NOMBRE = "nombre"
        const val EXTRA_APELLIDO = "apellido"
        const val EXTRA_TELEFONO = "telefono"
        const val EXTRA_DEUDA = "deuda"
        const val EXTRA_DESCRIPCION = "descripcion"
        const val EXTRA_TELEFONOS_EXISTENTES = "telefonos_existentes"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Teléfonos que ya están en la lista, para no repetir clientes.
        val telefonosExistentes =
            intent.getStringArrayListExtra(EXTRA_TELEFONOS_EXISTENTES) ?: arrayListOf()

        setContent {
            FiadoAppTheme {
                FormularioCliente(
                    telefonosExistentes = telefonosExistentes,
                    alVolver = { finish() },
                    alGuardar = { nombre, apellido, telefono, deuda, descripcion ->
                        val respuesta = Intent()
                        respuesta.putExtra(EXTRA_NOMBRE, nombre)
                        respuesta.putExtra(EXTRA_APELLIDO, apellido)
                        respuesta.putExtra(EXTRA_TELEFONO, telefono)
                        respuesta.putExtra(EXTRA_DEUDA, deuda)
                        respuesta.putExtra(EXTRA_DESCRIPCION, descripcion)
                        setResult(RESULT_OK, respuesta)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun FormularioCliente(
    telefonosExistentes: List<String>,
    alVolver: () -> Unit,
    alGuardar: (String, String, String, Int, String) -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var fiado by rememberSaveable { mutableStateOf("") }
    var descripcion by rememberSaveable { mutableStateOf("") }
    // Los errores se muestran después del primer intento de registrar.
    var mostrarErrores by rememberSaveable { mutableStateOf(false) }

    val valorFiado = fiado.toIntOrNull() ?: 0
    val errorNombre = validarNombre(nombre)
    val errorApellido = validarApellido(apellido)
    val errorTelefono = validarTelefono(telefono, telefonosExistentes)
    val errorFiado = validarMontoMaximo(valorFiado)
    // Si arranca debiendo, hay que decir qué se llevó.
    val errorDescripcion = if (valorFiado > 0) validarDescripcion(descripcion) else null
    val todoBien = listOf(errorNombre, errorApellido, errorTelefono, errorFiado, errorDescripcion)
        .all { it == null }

    Scaffold(
        containerColor = Fondo,
        bottomBar = {
            Column(modifier = Modifier.background(Fondo).navigationBarsPadding().imePadding()) {
                HorizontalDivider(color = Borde)
                Button(
                    onClick = {
                        mostrarErrores = true
                        if (todoBien) {
                            alGuardar(nombre.trim(), apellido.trim(), telefono, valorFiado, descripcion.trim())
                        }
                    },
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Morado),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .height(62.dp)
                ) {
                    Icon(Iconos.Check, contentDescription = null, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Registrar cliente", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // ----- Barra superior -----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                IconButton(onClick = alVolver) {
                    Icon(Iconos.Atras, contentDescription = "Volver", tint = Tinta)
                }
                Text("Nuevo cliente", color = Tinta, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            }

            // ----- Foto (marcador) -----
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(GrisSuave)
                    .align(Alignment.CenterHorizontally)
            ) {
                Icon(Iconos.Persona, contentDescription = null, tint = Gris, modifier = Modifier.size(34.dp))
            }

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Etiqueta("Nombre")
                CampoRedondo(
                    valor = nombre,
                    alCambiar = { nombre = it.take(30) },
                    placeholder = "Ej. Pedro",
                    error = if (mostrarErrores) errorNombre else null,
                    teclado = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
                Spacer(Modifier.height(16.dp))

                Etiqueta("Apellido")
                CampoRedondo(
                    valor = apellido,
                    alCambiar = { apellido = it.take(30) },
                    placeholder = "Opcional",
                    error = if (mostrarErrores) errorApellido else null,
                    teclado = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
                Spacer(Modifier.height(16.dp))

                Etiqueta("Teléfono")
                CampoRedondo(
                    valor = telefono,
                    alCambiar = { telefono = it.filter { c -> c.isDigit() }.take(10) },
                    placeholder = "310 987 6543",
                    icono = Iconos.Telefono,
                    error = if (mostrarErrores) errorTelefono else null,
                    teclado = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
                Spacer(Modifier.height(16.dp))

                Etiqueta("Fiado inicial")
                CampoMonto(
                    valor = fiado,
                    alCambiar = { fiado = it },
                    conError = mostrarErrores && errorFiado != null
                )
                Text(
                    if (mostrarErrores && errorFiado != null) errorFiado
                    else "Opcional. Déjelo en 0 si no debe nada.",
                    color = if (mostrarErrores && errorFiado != null) Rojo else GrisClaro,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )

                if (valorFiado > 0) {
                    Spacer(Modifier.height(16.dp))
                    Etiqueta("¿Qué se llevó?")
                    CampoRedondo(
                        valor = descripcion,
                        alCambiar = { descripcion = it.take(60) },
                        placeholder = "Ej. Arroz y aceite",
                        error = if (mostrarErrores) errorDescripcion else null,
                        teclado = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
