package com.example.fiadoapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.fiadoapp.ui.theme.Borde
import com.example.fiadoapp.ui.theme.FiadoAppTheme
import com.example.fiadoapp.ui.theme.Fondo
import com.example.fiadoapp.ui.theme.Gris
import com.example.fiadoapp.ui.theme.GrisSuave
import com.example.fiadoapp.ui.theme.Morado
import com.example.fiadoapp.ui.theme.MoradoDeshabilitado
import com.example.fiadoapp.ui.theme.Rojo
import com.example.fiadoapp.ui.theme.RojoSuave
import com.example.fiadoapp.ui.theme.Tinta
import com.example.fiadoapp.ui.theme.Verde
import com.example.fiadoapp.ui.theme.VerdeSuave

private const val TAG = "CICLO_DETALLE"

/** Guarda el historial mientras se edita, para que no se pierda si el celular gira. */
class DetalleViewModel : ViewModel() {
    val movimientos = mutableStateListOf<Movimiento>()
    var cargado = false
}

class DetalleActivity : ComponentActivity() {

    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_NOMBRE = "nombre"
        const val EXTRA_TELEFONO = "telefono"
        const val EXTRA_ELIMINAR = "eliminar"
    }

    private val vm: DetalleViewModel by viewModels()
    private var id = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        enableEdgeToEdge()

        // Los datos llegan en el Intent que abrió esta pantalla.
        id = intent.getIntExtra(EXTRA_ID, -1)
        val nombre = intent.getStringExtra(EXTRA_NOMBRE) ?: "Sin nombre"
        val telefono = intent.getStringExtra(EXTRA_TELEFONO) ?: ""
        if (!vm.cargado) {
            vm.movimientos.addAll(intent.leerMovimientos())
            vm.cargado = true
        }
        guardarResultado()

        setContent {
            FiadoAppTheme {
                PantallaDetalle(
                    nombre = nombre,
                    telefono = telefono,
                    movimientos = vm.movimientos,
                    alCambiar = { guardarResultado() },
                    alVolver = { finish() },
                    alEliminar = {
                        val respuesta = Intent()
                        respuesta.putExtra(EXTRA_ID, id)
                        respuesta.putExtra(EXTRA_ELIMINAR, true)
                        setResult(RESULT_OK, respuesta)
                        finish()
                    }
                )
            }
        }
    }

    /**
     * Deja lista la respuesta con el historial actual. Así, salga con la flecha
     * o con el botón Atrás del celular, la lista recibe los cambios.
     */
    private fun guardarResultado() {
        val respuesta = Intent()
        respuesta.putExtra(EXTRA_ID, id)
        respuesta.ponerMovimientos(vm.movimientos)
        setResult(RESULT_OK, respuesta)
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaDetalle(
    nombre: String,
    telefono: String,
    movimientos: MutableList<Movimiento>,
    alCambiar: () -> Unit,
    alVolver: () -> Unit,
    alEliminar: () -> Unit
) {
    val context = LocalContext.current
    val deuda = calcularDeuda(movimientos)
    val debe = deuda > 0

    // Qué panel inferior está abierto: FIADO, ABONO o ninguno (null).
    var panel by rememberSaveable { mutableStateOf<String?>(null) }
    var menuAbierto by rememberSaveable { mutableStateOf(false) }
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        containerColor = Fondo,
        bottomBar = {
            Column(modifier = Modifier.background(Fondo).navigationBarsPadding()) {
                HorizontalDivider(color = Borde)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    OutlinedButton(
                        onClick = { panel = FIADO },
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, Borde),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White,
                            contentColor = Tinta
                        ),
                        modifier = Modifier.weight(0.8f).height(62.dp)
                    ) {
                        Icon(Iconos.Mas, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Fiar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { panel = ABONO },
                        enabled = debe,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Morado,
                            disabledContainerColor = MoradoDeshabilitado,
                            disabledContentColor = Color.White
                        ),
                        modifier = Modifier.weight(1.2f).height(62.dp)
                    ) {
                        Icon(Iconos.FlechaAbono, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Registrar abono", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                IconButton(onClick = alVolver) {
                    Icon(Iconos.Atras, contentDescription = "Volver", tint = Tinta)
                }
                Spacer(Modifier.weight(1f))
                Box {
                    IconButton(onClick = { menuAbierto = true }) {
                        Icon(Iconos.MasOpciones, contentDescription = "Más opciones", tint = Tinta)
                    }
                    DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                        DropdownMenuItem(
                            text = { Text("Eliminar cliente", color = Rojo) },
                            onClick = {
                                menuAbierto = false
                                confirmarEliminar = true
                            }
                        )
                    }
                }
            }

            // ----- Nombre y teléfono -----
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            ) {
                Avatar(nombre, tamano = 76.dp, letra = 26.sp)
                Spacer(Modifier.width(20.dp))
                Column {
                    Text(nombre, color = Tinta, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    Text(telefonoBonito(telefono), color = Gris, fontSize = 18.sp)
                }
            }

            // ----- Saldo pendiente -----
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(GrisSuave)
                    .padding(horizontal = 26.dp, vertical = 22.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Saldo pendiente", color = Gris, fontSize = 16.sp, modifier = Modifier.weight(1f))
                    EstadoChip(debe)
                }
                Text(
                    pesos(deuda),
                    color = if (debe) Rojo else Verde,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // ----- Acciones (Intents implícitos) -----
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                BotonRedondo(Iconos.Telefono, "Llamar") {
                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telefono")))
                }
                BotonRedondo(Iconos.Mensaje, "Recordar") {
                    val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$telefono"))
                    intent.putExtra(
                        "sms_body",
                        "Hola $nombre, te recordamos que tu saldo pendiente es de ${pesos(deuda)}. ¡Gracias!"
                    )
                    context.startActivity(intent)
                }
                BotonRedondo(Iconos.Compartir, "Compartir") {
                    val ultimos = movimientos.takeLast(5).joinToString("\n") {
                        "${it.fechaCorta}: ${it.titulo} ${if (it.tipo == FIADO) "+" else "-"}${pesos(it.monto)}"
                    }
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    intent.putExtra(Intent.EXTRA_TEXT, "Saldo de $nombre: ${pesos(deuda)}\n$ultimos")
                    context.startActivity(Intent.createChooser(intent, "Compartir con"))
                }
            }

            // ----- Movimientos -----
            Text(
                "Movimientos",
                color = Tinta,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 4.dp)
            )
            if (movimientos.isEmpty()) {
                Text(
                    "Todavía no hay movimientos.",
                    color = Gris,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
            val recientes = movimientos.asReversed()
            recientes.forEachIndexed { i, mov ->
                FilaMovimiento(mov)
                if (i < recientes.lastIndex) {
                    HorizontalDivider(color = Borde, modifier = Modifier.padding(horizontal = 24.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    // ----- Panel inferior para fiar o abonar -----
    panel?.let { tipo ->
        ModalBottomSheet(
            onDismissRequest = { panel = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            PanelMovimiento(tipo = tipo, deuda = deuda) { monto, descripcion ->
                movimientos.add(Movimiento(tipo, monto, if (tipo == FIADO) descripcion else "Abono"))
                alCambiar()
                panel = null
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text("¿Eliminar a $nombre?") },
            text = { Text("Se borrará el cliente y todo su historial.") },
            confirmButton = {
                TextButton(onClick = alEliminar) { Text("Eliminar", color = Rojo) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun EstadoChip(debe: Boolean) {
    Surface(shape = RoundedCornerShape(50), color = if (debe) RojoSuave else VerdeSuave) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Box(
                Modifier.size(7.dp).clip(CircleShape).background(if (debe) Rojo else Verde)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                if (debe) "Debe" else "Al día",
                color = if (debe) Rojo else Verde,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun FilaMovimiento(mov: Movimiento) {
    val esAbono = mov.tipo == ABONO
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(if (esAbono) VerdeSuave else GrisSuave)
        ) {
            Icon(
                if (esAbono) Iconos.FlechaAbono else Iconos.FlechaFiado,
                contentDescription = null,
                tint = if (esAbono) Verde else Gris,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(mov.titulo, color = Tinta, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Text(mov.fechaCorta, color = Gris, fontSize = 15.sp)
        }
        Text(
            if (esAbono) "-${pesos(mov.monto)}" else "+${pesos(mov.monto)}",
            color = if (esAbono) Verde else Tinta,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/** Contenido del panel inferior: valor, valores rápidos y (si es fiado) qué se llevó. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PanelMovimiento(tipo: String, deuda: Int, alRegistrar: (Int, String) -> Unit) {
    val esAbono = tipo == ABONO
    var monto by rememberSaveable {
        mutableStateOf(if (esAbono) minOf(5000, deuda).toString() else "")
    }
    var descripcion by rememberSaveable { mutableStateOf("") }
    var tocoDescripcion by rememberSaveable { mutableStateOf(false) }

    val valor = monto.toIntOrNull() ?: 0
    val errorMonto = validarMontoMaximo(valor)
        ?: if (esAbono && valor > deuda) "No puede ser mayor que lo que debe (${pesos(deuda)})" else null
    val errorDescripcion = if (esAbono) null else validarDescripcion(descripcion)
    val valido = valor > 0 && errorMonto == null && errorDescripcion == null
    val saldoDespues = if (esAbono) deuda - valor else deuda + valor

    Column(
        modifier = Modifier
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .imePadding()
    ) {
        Text(
            if (esAbono) "Registrar abono" else "Registrar fiado",
            color = Tinta,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(20.dp))
        CampoMonto(valor = monto, alCambiar = { monto = it }, conError = errorMonto != null)
        Text(
            errorMonto ?: "Saldo después: ${pesos(saldoDespues)}",
            color = if (errorMonto != null) Rojo else Gris,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(Modifier.height(16.dp))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(2000, 5000, 10000).forEach { rapido ->
                ChipMonto(pesos(rapido)) { monto = rapido.toString() }
            }
            if (esAbono) {
                ChipMonto("Todo (${pesos(deuda)})") { monto = deuda.toString() }
            }
        }
        if (!esAbono) {
            Spacer(Modifier.height(20.dp))
            Etiqueta("¿Qué se llevó?")
            CampoRedondo(
                valor = descripcion,
                alCambiar = {
                    descripcion = it.take(60)
                    tocoDescripcion = true
                },
                placeholder = "Ej. Arroz y aceite",
                error = if (tocoDescripcion) errorDescripcion else null,
                teclado = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { alRegistrar(valor, descripcion.trim()) },
            enabled = valido,
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Morado,
                disabledContainerColor = MoradoDeshabilitado,
                disabledContentColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth().height(62.dp)
        ) {
            Text(
                if (esAbono) "Registrar ${pesos(valor)}" else "Fiar",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
