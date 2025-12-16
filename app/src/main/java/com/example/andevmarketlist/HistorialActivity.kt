package com.example.andevmarketlist

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.dataclases.ListaApp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class HistorialActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial)

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
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    private fun cargarHistorial() {
        val linearLayoutContenedor = findViewById<LinearLayout>(R.id.linearLayoutContenedor)
        linearLayoutContenedor.removeAllViews()

        val listasCompletadas = obtenerTodasListas()
            .filter { it.completada }
            .sortedByDescending { it.fechaCreacion }

        if (listasCompletadas.isEmpty()) {
            val tvMensaje = TextView(this)
            tvMensaje.text = "No hay listas completadas en el historial"
            tvMensaje.textSize = 18f
            tvMensaje.gravity = Gravity.CENTER
            tvMensaje.setTextColor(Color.GRAY)
            tvMensaje.setPadding(0, 100, 0, 0)

            linearLayoutContenedor.addView(tvMensaje)
            return
        }

        listasCompletadas.forEachIndexed { index, lista ->
            val card = crearCardListaCompletada(lista, index)
            linearLayoutContenedor.addView(card)
        }
    }

    private fun crearCardListaCompletada(lista: ListaApp, index: Int): LinearLayout {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.setPadding(25, 20, 25, 20)

        card.setBackgroundColor(Color.parseColor("#F0F0F0"))

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(16, 8, 16, 16)
        card.layoutParams = params

        val textViewNombre = TextView(this)
        textViewNombre.text = lista.nombre
        textViewNombre.textSize = 18f
        textViewNombre.setTextColor(Color.BLACK)
        textViewNombre.setTypeface(null, android.graphics.Typeface.BOLD)
        textViewNombre.setPadding(0, 0, 0, 12)
        card.addView(textViewNombre)

        val infoLayout = LinearLayout(this)
        infoLayout.orientation = LinearLayout.VERTICAL
        infoLayout.setPadding(0, 0, 0, 0)

        val tvEstado = TextView(this)
        tvEstado.text = "Completada"
        tvEstado.textSize = 14f
        tvEstado.setTextColor(Color.parseColor("#4CAF50"))
        tvEstado.setPadding(0, 0, 0, 6)
        infoLayout.addView(tvEstado)

        val tvFecha = TextView(this)
        tvFecha.text = "Creada: ${lista.fechaCreacion}"
        tvFecha.textSize = 14f
        tvFecha.setTextColor(Color.DKGRAY)
        tvFecha.setPadding(0, 0, 0, 6)
        infoLayout.addView(tvFecha)

        val tvPrioridad = TextView(this)
        tvPrioridad.text = "Prioridad: ${lista.prioridad}"
        tvPrioridad.textSize = 14f
        tvPrioridad.setTextColor(Color.DKGRAY)
        tvPrioridad.setPadding(0, 0, 0, 6)
        infoLayout.addView(tvPrioridad)

        if (lista.productos.isNotEmpty()) {
            val tvProductos = TextView(this)
            val productosTexto = if (lista.productos.size > 3) {
                lista.productos.take(3).joinToString(", ") + ", ..."
            } else {
                lista.productos.joinToString(", ")
            }
            tvProductos.text = "Productos: $productosTexto"
            tvProductos.textSize = 14f
            tvProductos.setTextColor(Color.DKGRAY)
            tvProductos.setPadding(0, 0, 0, 6)
            infoLayout.addView(tvProductos)
        }

        card.addView(infoLayout)

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
            val intent = Intent(this, pantalla_menu::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        cargarHistorial()
    }
}