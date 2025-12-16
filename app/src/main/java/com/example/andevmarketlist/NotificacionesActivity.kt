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

class NotificacionesActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notificaciones)

        sharedPreferences = getSharedPreferences(
            pantalla_menu.NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        configurarBotonesNavegacion()

        cargarNotificaciones()

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

    private fun cargarNotificaciones() {
        val contenedorNotificaciones = findViewById<LinearLayout>(R.id.contenedorNotificaciones)
        val textViewSinNotificaciones = findViewById<TextView>(R.id.textViewSinNotificaciones)

        for (i in contenedorNotificaciones.childCount - 1 downTo 1) {
            val child = contenedorNotificaciones.getChildAt(i)
            if (child.id != R.id.textViewSinNotificaciones) {
                contenedorNotificaciones.removeViewAt(i)
            }
        }

        val todasListas = obtenerTodasListas()
            .filter { !it.completada }
            .filter { it.fechaLimite != null }

        val tieneFechasLimite = todasListas.isNotEmpty()

        if (!tieneFechasLimite) {
            textViewSinNotificaciones.visibility = View.VISIBLE
            textViewSinNotificaciones.text = "Sin listas con fecha límite"
            return
        }

        textViewSinNotificaciones.visibility = View.GONE

        val fechaHoy = Calendar.getInstance()
        val fechaManana = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

        val formatoSalida = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "ES"))

        val formatoComparacion = SimpleDateFormat("ddMMyyyy", Locale.getDefault())

        val listasHoy = todasListas.filter { lista ->
            try {
                val fechaLista = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(lista.fechaLimite!!)
                formatoComparacion.format(fechaLista) == formatoComparacion.format(fechaHoy.time)
            } catch (e: Exception) {
                false
            }
        }

        val listasManana = todasListas.filter { lista ->
            try {
                val fechaLista = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(lista.fechaLimite!!)
                formatoComparacion.format(fechaLista) == formatoComparacion.format(fechaManana.time)
            } catch (e: Exception) {
                false
            }
        }

        crearSeccionFecha(
            contenedorNotificaciones,
            "Hoy (${formatoSalida.format(fechaHoy.time)}):",
            listasHoy,
            Color.parseColor("#FFE0E0")
        )

        crearSeccionFecha(
            contenedorNotificaciones,
            "Mañana (${formatoSalida.format(fechaManana.time)}):",
            listasManana,
            Color.parseColor("#FFF0E0")
        )
    }

    private fun crearSeccionFecha(
        contenedor: LinearLayout,
        titulo: String,
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

        val textViewTitulo = TextView(this)
        textViewTitulo.text = titulo
        textViewTitulo.textSize = 18f
        textViewTitulo.setTextColor(Color.BLACK)
        textViewTitulo.setTypeface(null, android.graphics.Typeface.BOLD)
        textViewTitulo.setPadding(0, 0, 0, 12)
        seccion.addView(textViewTitulo)

        if (listas.isEmpty()) {
            val textViewVacio = TextView(this)
            textViewVacio.text = "Sin eventos"
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
                        "Media" -> Color.parseColor("#FF9800") // Naranja
                        else -> Color.parseColor("#4CAF50") // Verde
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
            val intent = Intent(this, activity_Calendario::class.java)
            startActivity(intent)
        }

        botonInicio.setOnClickListener {
            val intent = Intent(this, pantalla_menu::class.java)
            startActivity(intent)
        }

        botonAlarma.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        cargarNotificaciones()
    }
}