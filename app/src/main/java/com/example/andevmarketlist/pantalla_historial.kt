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
import com.example.andevmarketlist.databinding.ActivityPantallaHistorialBinding
import com.example.andevmarketlist.dataclases.ListaApp
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class PantallaHistorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPantallaHistorialBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPantallaHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(
            pantalla_menu.NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        configurarBotonesNavegacion()
        cargarHistorial()

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

    private fun cargarHistorial() {
        val contenedor = binding.contenedorHistorial
        val mensajeVacio = binding.textViewMensajeVacio

        contenedor.removeAllViews()

        val listasCompletadas = obtenerTodasListas()
            .filter { it.completada }
            .sortedByDescending {
                try {
                    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .parse(it.fechaCreacion)
                } catch (e: Exception) {
                    Date()
                }
            }

        if (listasCompletadas.isEmpty()) {
            mensajeVacio.visibility = View.VISIBLE
            return
        }

        mensajeVacio.visibility = View.GONE

        listasCompletadas.forEachIndexed { index, lista ->
            val card = crearCardListaCompletada(lista, index)
            contenedor.addView(card)
        }
    }

    private fun crearCardListaCompletada(
        lista: ListaApp,
        index: Int
    ): LinearLayout {

        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(Color.parseColor("#E8E8E8"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply { bottomMargin = 16 }
        }

        val fila1 = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        val nombre = TextView(this).apply {
            text = lista.nombre
            textSize = 18f
            setTextColor(Color.BLACK)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val completada = TextView(this).apply {
            text = "✓ Completada"
            textSize = 14f
            setTextColor(Color.parseColor("#4CAF50"))
            gravity = Gravity.END
        }

        fila1.addView(nombre)
        fila1.addView(completada)
        card.addView(fila1)

        card.addView(TextView(this).apply {
            text = "Creada: ${lista.fechaCreacion}"
            textSize = 14f
            setTextColor(Color.DKGRAY)
            setPadding(0, 8, 0, 8)
        })

        lista.fechaLimite?.let {
            card.addView(TextView(this).apply {
                text = "Fecha límite: $it"
                textSize = 14f
                setTextColor(Color.DKGRAY)
                setPadding(0, 0, 0, 8)
            })
        }

        card.addView(TextView(this).apply {
            text = "Prioridad: ${lista.prioridad}"
            textSize = 14f
            setTextColor(Color.DKGRAY)
            setPadding(0, 0, 0, 8)
        })

        if (lista.productos.isNotEmpty()) {
            val productosTexto = lista.productos.take(3).joinToString(", ")
            val textoFinal = if (lista.productos.size > 3) {
                "$productosTexto, ..."
            } else {
                productosTexto
            }

            card.addView(TextView(this).apply {
                text = "Productos: $textoFinal"
                textSize = 14f
                setTextColor(Color.DKGRAY)
            })
        }

        card.setOnClickListener {
            startActivity(
                Intent(this, VerListaActivity::class.java)
                    .putExtra(VerListaActivity.EXTRA_LISTA_ID, lista.id)
            )
        }

        return card
    }

    private fun configurarBotonesNavegacion() {
        binding.botonCalendario.setOnClickListener {
            startActivity(Intent(this, activity_Calendario::class.java))
        }

        binding.botonInicio.setOnClickListener {
            startActivity(Intent(this, pantalla_menu::class.java))
        }

        binding.botonAlarma.setOnClickListener {
            cargarHistorial()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarHistorial()
    }
}
