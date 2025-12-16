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

class activity_Calendario : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendario)

        sharedPreferences = getSharedPreferences(
            pantalla_menu.NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        configurarBotonesNavegacion()

        cargarCalendario()

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

    private fun cargarCalendario() {
        val contenedorCalendario = findViewById<LinearLayout>(R.id.linearLayoutContenedor)
        val textViewMensajeVacio = findViewById<TextView>(R.id.textViewMensajeVacio)

        for (i in contenedorCalendario.childCount - 1 downTo 1) {
            val child = contenedorCalendario.getChildAt(i)
            if (child.id != R.id.textViewMensajeVacio) {
                contenedorCalendario.removeViewAt(i)
            }
        }

        val todasListas = obtenerTodasListas()
            .filter { !it.completada }
            .filter { it.fechaLimite != null }

        if (todasListas.isEmpty()) {
            textViewMensajeVacio.visibility = View.VISIBLE
            textViewMensajeVacio.text = "No hay listas con fecha límite"
            return
        }

        textViewMensajeVacio.visibility = View.GONE

        val listasPorFecha = mutableMapOf<String, MutableList<ListaApp>>()

        val formatoSalida = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "ES"))

        val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        todasListas.forEach { lista ->
            try {
                val fechaLista = formatoEntrada.parse(lista.fechaLimite!!)
                val fechaKey = formatoSalida.format(fechaLista)

                if (!listasPorFecha.containsKey(fechaKey)) {
                    listasPorFecha[fechaKey] = mutableListOf()
                }
                listasPorFecha[fechaKey]?.add(lista)
            } catch (e: Exception) {
                // Ignorar fechas mal formateadas
            }
        }

        val fechasOrdenadas = listasPorFecha.keys.sortedBy { fecha ->
            try {
                formatoSalida.parse(fecha)?.time ?: Long.MAX_VALUE
            } catch (e: Exception) {
                Long.MAX_VALUE
            }
        }

        fechasOrdenadas.forEachIndexed { index, fecha ->
            val listas = listasPorFecha[fecha] ?: emptyList()

            val colorFondo = when {
                fecha == formatoSalida.format(Date()) -> Color.parseColor("#FFE0E0")
                fecha == formatoSalida.format(Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time) ->
                    Color.parseColor("#FFF0E0")
                else -> Color.parseColor("#F0F0F0")
            }

            crearSeccionFecha(
                contenedorCalendario,
                fecha,
                listas,
                colorFondo
            )
        }
    }

    private fun crearSeccionFecha(
        contenedor: LinearLayout,
        fecha: String,
        listas: List<ListaApp>,
        colorFondo: Int
    ) {
        val seccion = LinearLayout(this)
        seccion.orientation = LinearLayout.VERTICAL
        seccion.setPadding(20, 20, 20, 20)
        seccion.setBackgroundColor(colorFondo)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)
        seccion.layoutParams = params

        val textViewFecha = TextView(this)
        textViewFecha.text = fecha
        textViewFecha.textSize = 18f
        textViewFecha.setTextColor(Color.BLACK)
        textViewFecha.setTypeface(null, android.graphics.Typeface.BOLD)
        textViewFecha.setPadding(0, 0, 0, 12)
        seccion.addView(textViewFecha)

        if (listas.isEmpty()) {
            val textViewVacio = TextView(this)
            textViewVacio.text = "Sin listas"
            textViewVacio.textSize = 16f
            textViewVacio.setTextColor(Color.GRAY)
            textViewVacio.setPadding(20, 0, 0, 0)
            seccion.addView(textViewVacio)
        } else {
            listas.forEach { lista ->
                val itemLayout = LinearLayout(this)
                itemLayout.orientation = LinearLayout.HORIZONTAL
                itemLayout.gravity = Gravity.CENTER_VERTICAL
                itemLayout.setPadding(20, 8, 0, 8)

                val punto = TextView(this)
                punto.text = "•"
                punto.textSize = 16f
                punto.setPadding(0, 0, 12, 0)
                itemLayout.addView(punto)

                val textViewLista = TextView(this)
                textViewLista.text = lista.nombre
                textViewLista.textSize = 16f
                textViewLista.setTextColor(Color.BLACK)
                textViewLista.layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

                textViewLista.setOnClickListener {
                    val intent = Intent(this, VerListaActivity::class.java)
                    intent.putExtra(VerListaActivity.EXTRA_LISTA_ID, lista.id)
                    startActivity(intent)
                }

                textViewLista.paintFlags = textViewLista.paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG

                itemLayout.addView(textViewLista)

                val textViewPrioridad = TextView(this)
                textViewPrioridad.text = " (${lista.prioridad})"
                textViewPrioridad.textSize = 14f
                textViewPrioridad.setTextColor(
                    when (lista.prioridad) {
                        "Alta" -> Color.RED
                        "Media" -> Color.parseColor("#FF9800")
                        else -> Color.parseColor("#4CAF50")
                    }
                )
                itemLayout.addView(textViewPrioridad)

                seccion.addView(itemLayout)
            }
        }

        contenedor.addView(seccion)
    }

    private fun configurarBotonesNavegacion() {
        val botonCalendario = findViewById<ImageButton>(R.id.botonCalendario)
        val botonInicio = findViewById<ImageButton>(R.id.botonInicio)
        val botonAlarma = findViewById<ImageButton>(R.id.botonAlarma)

        botonCalendario.setOnClickListener {
            cargarCalendario()
        }

        botonInicio.setOnClickListener {
            finish()
        }

        botonAlarma.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarCalendario()
    }
}