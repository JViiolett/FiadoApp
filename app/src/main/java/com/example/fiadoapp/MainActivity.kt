package com.example.fiadoapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fiadoapp.ui.theme.Borde
import com.example.fiadoapp.ui.theme.FiadoAppTheme
import com.example.fiadoapp.ui.theme.Fondo
import com.example.fiadoapp.ui.theme.Gris
import com.example.fiadoapp.ui.theme.MoradoMarca
import com.example.fiadoapp.ui.theme.Rojo
import com.example.fiadoapp.ui.theme.Tinta
import com.example.fiadoapp.ui.theme.Verde
import com.example.fiadoapp.ui.theme.VerdeSuave

private const val TAG = "CICLO_MAIN"

class MainActivity : ComponentActivity() {

    private val vm: ClientesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        enableEdgeToEdge()
        setContent {
            FiadoAppTheme {
                Scaffold(containerColor = Fondo, modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaClientes(vm, Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onRestart() { super.onRestart(); Log.d(TAG, "onRestart") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }
}

@Composable
fun PantallaClientes(vm: ClientesViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var busqueda by rememberSaveable { mutableStateOf("") }

    // Abre DetalleActivity y recibe el historial (o la orden de eliminar) cuando esa pantalla termina.
    val abrirDetalle = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        val datos = resultado.data
        if (resultado.resultCode == Activity.RESULT_OK && datos != null) {
            val id = datos.getIntExtra(DetalleActivity.EXTRA_ID, -1)
            if (datos.getBooleanExtra(DetalleActivity.EXTRA_ELIMINAR, false)) {
                vm.eliminar(id)
            } else {
                vm.actualizarMovimientos(id, datos.leerMovimientos())
            }
        }
    }

    // Abre NuevoClienteActivity y recibe el cliente nuevo.
    val abrirNuevo = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { resultado ->
        val datos = resultado.data
        if (resultado.resultCode == Activity.RESULT_OK && datos != null) {
            vm.agregar(
                nombre = datos.getStringExtra(NuevoClienteActivity.EXTRA_NOMBRE) ?: "",
                apellido = datos.getStringExtra(NuevoClienteActivity.EXTRA_APELLIDO) ?: "",
                telefono = datos.getStringExtra(NuevoClienteActivity.EXTRA_TELEFONO) ?: "",
                deuda = datos.getIntExtra(NuevoClienteActivity.EXTRA_DEUDA, 0),
                descripcion = datos.getStringExtra(NuevoClienteActivity.EXTRA_DESCRIPCION) ?: ""
            )
        }
    }

    val filtrados = vm.clientes.filter {
        busqueda.isBlank() ||
            it.nombreCompleto.contains(busqueda.trim(), ignoreCase = true) ||
            it.telefono.contains(busqueda.filter { c -> c.isDigit() }.ifEmpty { "-" })
    }

    Column(modifier = modifier.fillMaxSize()) {
        // ----- Encabezado -----
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 12.dp, top = 12.dp)
        ) {
            Text(
                "Llamatico",
                color = MoradoMarca,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                val intent = Intent(context, NuevoClienteActivity::class.java)
                intent.putStringArrayListExtra(
                    NuevoClienteActivity.EXTRA_TELEFONOS_EXISTENTES,
                    ArrayList(vm.clientes.map { it.telefono })
                )
                abrirNuevo.launch(intent)
            }) {
                Icon(Iconos.AgregarPersona, contentDescription = "Nuevo cliente", tint = Tinta)
            }
        }

        // ----- Total por cobrar -----
        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Text("Por cobrar", color = Gris, fontSize = 16.sp)
            Text(
                pesos(vm.totalPorCobrar),
                color = Tinta,
                fontSize = 54.sp,
                fontWeight = FontWeight.ExtraBold
            )
            val n = vm.clientesConSaldo
            Text(
                if (n == 1) "1 cliente con saldo" else "$n clientes con saldo",
                color = Gris,
                fontSize = 17.sp
            )
        }

        // ----- Buscador -----
        CampoRedondo(
            valor = busqueda,
            alCambiar = { busqueda = it },
            placeholder = "Buscar cliente",
            icono = Iconos.Buscar,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(Modifier.height(12.dp))

        // ----- Lista -----
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(filtrados, key = { it.id }) { cliente ->
                FilaCliente(cliente) {
                    val intent = Intent(context, DetalleActivity::class.java)
                    intent.putExtra(DetalleActivity.EXTRA_ID, cliente.id)
                    intent.putExtra(DetalleActivity.EXTRA_NOMBRE, cliente.nombreCompleto)
                    intent.putExtra(DetalleActivity.EXTRA_TELEFONO, cliente.telefono)
                    intent.ponerMovimientos(cliente.movimientos)
                    abrirDetalle.launch(intent)
                }
                HorizontalDivider(color = Borde)
            }
            if (filtrados.isEmpty()) {
                item {
                    Text(
                        if (busqueda.isBlank()) "Todavía no hay clientes." else "No hay clientes con \"$busqueda\".",
                        color = Gris,
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaCliente(cliente: Cliente, alTocar: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = alTocar)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Avatar(cliente.nombreCompleto)
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(cliente.nombreCompleto, color = Tinta, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text(telefonoBonito(cliente.telefono), color = Gris, fontSize = 15.sp)
        }
        if (cliente.deuda > 0) {
            Text(pesos(cliente.deuda), color = Rojo, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        } else {
            Surface(shape = RoundedCornerShape(50), color = VerdeSuave) {
                Text(
                    "Al día",
                    color = Verde,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                )
            }
        }
        Spacer(Modifier.width(10.dp))
        Icon(Iconos.Siguiente, contentDescription = null, tint = Gris, modifier = Modifier.size(20.dp))
    }
}
