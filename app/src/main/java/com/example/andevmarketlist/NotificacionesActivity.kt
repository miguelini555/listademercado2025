package com.example.andevmarketlist

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.databinding.ActivityNotificacionesBinding
import com.example.andevmarketlist.dataclases.ListaApp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class NotificacionesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNotificacionesBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNotificacionesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(
            pantalla_menu.NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        configurarBotonesNavegacion()
        cargarNotificaciones()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
    }

    private fun obtenerTodasListas(): List<ListaApp> {
        val stringGuardado = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        )

        return try {
            Json.decodeFromString(stringGuardado ?: "[]")
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun cargarNotificaciones() {
        val contenedor = binding.contenedorNotificaciones
        val textoVacio = binding.textViewSinNotificaciones

        for (i in contenedor.childCount - 1 downTo 1) {
            val child = contenedor.getChildAt(i)
            if (child.id != textoVacio.id) {
                contenedor.removeViewAt(i)
            }
        }

        val todasListas = obtenerTodasListas()
            .filter { !it.completada && it.fechaLimite != null }

        if (todasListas.isEmpty()) {
            textoVacio.visibility = View.VISIBLE
            textoVacio.text = "Sin listas con fecha límite"
            return
        }

        textoVacio.visibility = View.GONE

        val hoy = Calendar.getInstance()
        val manana = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }

        val formatoSalida = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "ES"))
        val formatoComparacion = SimpleDateFormat("ddMMyyyy", Locale.getDefault())
        val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val listasHoy = todasListas.filter {
            try {
                formatoComparacion.format(formatoEntrada.parse(it.fechaLimite!!)) ==
                        formatoComparacion.format(hoy.time)
            } catch (e: Exception) {
                false
            }
        }

        val listasManana = todasListas.filter {
            try {
                formatoComparacion.format(formatoEntrada.parse(it.fechaLimite!!)) ==
                        formatoComparacion.format(manana.time)
            } catch (e: Exception) {
                false
            }
        }

        crearSeccionFecha(
            contenedor,
            "Hoy (${formatoSalida.format(hoy.time)}):",
            listasHoy,
            Color.parseColor("#FFE0E0")
        )

        crearSeccionFecha(
            contenedor,
            "Mañana (${formatoSalida.format(manana.time)}):",
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
        val seccion = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(colorFondo)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
        }

        val tituloView = TextView(this).apply {
            text = titulo
            textSize = 18f
            setTextColor(Color.BLACK)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }
        seccion.addView(tituloView)

        if (listas.isEmpty()) {
            seccion.addView(TextView(this).apply {
                text = "Sin eventos"
                textSize = 16f
                setTextColor(Color.GRAY)
                setPadding(20, 0, 0, 0)
            })
        } else {
            listas.forEach { lista ->
                val item = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(20, 8, 0, 8)
                }

                item.addView(TextView(this).apply {
                    text = "•"
                    textSize = 16f
                    setPadding(0, 0, 12, 0)
                })

                val nombre = TextView(this).apply {
                    text = lista.nombre
                    textSize = 16f
                    setTextColor(Color.BLACK)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    paintFlags = paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                    setOnClickListener {
                        startActivity(
                            Intent(this@NotificacionesActivity, VerListaActivity::class.java)
                                .putExtra(VerListaActivity.EXTRA_LISTA_ID, lista.id)
                        )
                    }
                }

                item.addView(nombre)

                item.addView(TextView(this).apply {
                    text = " (${lista.prioridad})"
                    textSize = 14f
                    setTextColor(
                        when (lista.prioridad) {
                            "Alta" -> Color.RED
                            "Media" -> Color.parseColor("#FF9800")
                            else -> Color.parseColor("#4CAF50")
                        }
                    )
                })

                seccion.addView(item)
            }
        }

        contenedor.addView(seccion)
    }

    private fun configurarBotonesNavegacion() {
        binding.botonCalendario.setOnClickListener {
            startActivity(Intent(this, activity_Calendario::class.java))
        }

        binding.botonInicio.setOnClickListener {
            startActivity(Intent(this, pantalla_menu::class.java))
        }

        binding.botonAlarma.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarNotificaciones()
    }
}
