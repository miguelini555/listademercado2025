package com.example.andevmarketlist

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.dataclases.ListaApp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class histActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hist)

        Toast.makeText(this, "histActivity cargada", Toast.LENGTH_SHORT).show()

        sharedPreferences = getSharedPreferences(
            pantalla_menu.NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        configurarBotonesNavegacion()

        cargarHistorial()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun obtenerTodasListas(): List<ListaApp> {
        val stringGuardado: String? = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        )

        return if (stringGuardado != null) {
            try {
                Json.decodeFromString<List<ListaApp>>(stringGuardado)
            } catch (e: Exception) {
                Toast.makeText(this, "Error al cargar listas", Toast.LENGTH_SHORT).show()
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    private fun cargarHistorial() {
        val contenedorHistorial = findViewById<LinearLayout>(R.id.contenedorHistorial)
        val mensajeVacio = findViewById<TextView>(R.id.textViewMensajeVacio)

        contenedorHistorial.removeAllViews()

        val todasListas = obtenerTodasListas()
        Toast.makeText(this, "Total listas: ${todasListas.size}", Toast.LENGTH_SHORT).show()

        val listasCompletadas = todasListas.filter { it.completada }
        Toast.makeText(this, "Listas completadas: ${listasCompletadas.size}", Toast.LENGTH_SHORT).show()

        if (listasCompletadas.isEmpty()) {
            mensajeVacio.visibility = View.VISIBLE
            return
        }

        mensajeVacio.visibility = View.GONE

        val listasOrdenadas = listasCompletadas.sortedByDescending {
            try {
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).parse(it.fechaCreacion)
            } catch (e: Exception) {
                Date()
            }
        }

        listasOrdenadas.forEachIndexed { index, lista ->
            val card = crearCardListaCompletada(lista)
            contenedorHistorial.addView(card)
        }
    }

    private fun crearCardListaCompletada(lista: ListaApp): LinearLayout {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(20, 20, 20, 20)

        card.setBackgroundColor(Color.parseColor("#E8E8E8"))

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.bottomMargin = 16
        card.layoutParams = params

        val textViewNombre = TextView(this)
        textViewNombre.text = lista.nombre
        textViewNombre.textSize = 18f
        textViewNombre.setTextColor(Color.BLACK)
        textViewNombre.setPadding(0, 0, 0, 8)
        card.addView(textViewNombre)

        val textViewEstado = TextView(this)
        textViewEstado.text = "✓ Completada"
        textViewEstado.textSize = 14f
        textViewEstado.setTextColor(Color.parseColor("#4CAF50"))
        textViewEstado.setPadding(0, 0, 0, 8)
        card.addView(textViewEstado)

        val textViewFecha = TextView(this)
        textViewFecha.text = "Creada: ${lista.fechaCreacion}"
        textViewFecha.textSize = 14f
        textViewFecha.setTextColor(Color.DKGRAY)
        textViewFecha.setPadding(0, 0, 0, 8)
        card.addView(textViewFecha)

        if (lista.fechaLimite != null) {
            val textViewFechaLimite = TextView(this)
            textViewFechaLimite.text = "Fecha limite: ${lista.fechaLimite}"
            textViewFechaLimite.textSize = 14f
            textViewFechaLimite.setTextColor(Color.DKGRAY)
            textViewFechaLimite.setPadding(0, 0, 0, 8)
            card.addView(textViewFechaLimite)
        }

        val textViewPrioridad = TextView(this)
        textViewPrioridad.text = "Prioridad: ${lista.prioridad}"
        textViewPrioridad.textSize = 14f
        textViewPrioridad.setTextColor(Color.DKGRAY)
        textViewPrioridad.setPadding(0, 0, 0, 8)
        card.addView(textViewPrioridad)

        if (lista.productos.isNotEmpty()) {
            val textViewProductos = TextView(this)
            val productosTexto = lista.productos.take(3).joinToString(", ")
            val textoCompleto = if (lista.productos.size > 3) {
                "$productosTexto, ... (${lista.productos.size} total)"
            } else {
                "$productosTexto (${lista.productos.size} total)"
            }
            textViewProductos.text = "Productos: $textoCompleto"
            textViewProductos.textSize = 14f
            textViewProductos.setTextColor(Color.DKGRAY)
            card.addView(textViewProductos)
        }

        card.setOnClickListener {
            val intent = Intent(this, VerListaActivity::class.java)
            intent.putExtra(VerListaActivity.EXTRA_LISTA_ID, lista.id)
            startActivity(intent)
        }

        return card
    }

    private fun configurarBotonesNavegacion() {
        val botonCalendario = findViewById<ImageButton>(R.id.botonCalendario)
        val botonInicio = findViewById<ImageButton>(R.id.botonInicio)
        val botonAlarma = findViewById<ImageButton>(R.id.botonAlarma)

        botonCalendario.setOnClickListener {
            val intent = Intent(this, activity_Calendario::class.java)
            startActivity(intent)
        }

        botonInicio.setOnClickListener {
            finish()
        }

        botonAlarma.setOnClickListener {
            cargarHistorial()
            Toast.makeText(this, "Historial actualizado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarHistorial()
    }
}