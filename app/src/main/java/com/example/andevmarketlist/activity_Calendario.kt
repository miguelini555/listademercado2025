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
import com.example.andevmarketlist.databinding.ActivityCalendarioBinding
import com.example.andevmarketlist.dataclases.ListaApp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class activity_Calendario : AppCompatActivity() {

    private lateinit var binding: ActivityCalendarioBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCalendarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(
            pantalla_menu.NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        configurarBotonesNavegacion()
        cargarCalendario()

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
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

    private fun cargarCalendario() {
        val contenedorCalendario = binding.linearLayoutContenedor
        val textViewMensajeVacio = binding.textViewMensajeVacio

        for (i in contenedorCalendario.childCount - 1 downTo 1) {
            val child = contenedorCalendario.getChildAt(i)
            if (child.id != textViewMensajeVacio.id) {
                contenedorCalendario.removeViewAt(i)
            }
        }

        val todasListas = obtenerTodasListas()
            .filter { !it.completada && it.fechaLimite != null }

        if (todasListas.isEmpty()) {
            textViewMensajeVacio.visibility = View.VISIBLE
            textViewMensajeVacio.text = getString(R.string.no_hay_listas_fecha)
            return
        }

        textViewMensajeVacio.visibility = View.GONE

        val listasPorFecha = mutableMapOf<String, MutableList<ListaApp>>()
        val formatoSalida = SimpleDateFormat("EEEE d 'de' MMMM", Locale("es", "ES"))
        val formatoEntrada = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        todasListas.forEach { lista ->
            try {
                val fechaLista = formatoEntrada.parse(lista.fechaLimite!!)
                val fechaKey = formatoSalida.format(fechaLista!!)
                listasPorFecha.getOrPut(fechaKey) { mutableListOf() }.add(lista)
            } catch (_: Exception) {}
        }

        val fechasOrdenadas = listasPorFecha.keys.sortedBy {
            try {
                formatoSalida.parse(it)?.time ?: Long.MAX_VALUE
            } catch (_: Exception) {
                Long.MAX_VALUE
            }
        }

        fechasOrdenadas.forEach { fecha ->
            val listas = listasPorFecha[fecha].orEmpty()

            val colorFondo = when {
                fecha == formatoSalida.format(Date()) -> Color.parseColor("#FFE0E0")
                fecha == formatoSalida.format(
                    Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }.time
                ) -> Color.parseColor("#FFF0E0")
                else -> Color.parseColor("#F0F0F0")
            }

            crearSeccionFecha(contenedorCalendario, fecha, listas, colorFondo)
        }
    }

    private fun crearSeccionFecha(
        contenedor: LinearLayout,
        fecha: String,
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
            ).apply {
                setMargins(0, 0, 0, 16)
            }
        }

        val textViewFecha = TextView(this).apply {
            text = fecha
            textSize = 18f
            setTextColor(Color.BLACK)
            setTypeface(null, android.graphics.Typeface.BOLD)
            setPadding(0, 0, 0, 12)
        }
        seccion.addView(textViewFecha)

        if (listas.isEmpty()) {
            seccion.addView(TextView(this).apply {
                text = getString(R.string.sin_listas)
                textSize = 16f
                setTextColor(Color.GRAY)
                setPadding(20, 0, 0, 0)
            })
        } else {
            listas.forEach { lista ->
                val itemLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(20, 8, 0, 8)
                }

                itemLayout.addView(TextView(this).apply {
                    text = "•"
                    textSize = 16f
                    setPadding(0, 0, 12, 0)
                })

                val textViewLista = TextView(this).apply {
                    text = lista.nombre
                    textSize = 16f
                    setTextColor(Color.BLACK)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    paintFlags = paintFlags or android.graphics.Paint.UNDERLINE_TEXT_FLAG
                    setOnClickListener {
                        startActivity(
                            Intent(this@activity_Calendario, VerListaActivity::class.java)
                                .putExtra(VerListaActivity.EXTRA_LISTA_ID, lista.id)
                        )
                    }
                }
                itemLayout.addView(textViewLista)

                itemLayout.addView(TextView(this).apply {
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

                seccion.addView(itemLayout)
            }
        }

        contenedor.addView(seccion)
    }

    private fun configurarBotonesNavegacion() {
        binding.botonInicio.setOnClickListener {
            startActivity(Intent(this, pantalla_menu::class.java))
        }

        binding.botonAlarma.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarCalendario()
    }
}
