package com.example.andevmarketlist

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.dataclases.ListaApp
import com.example.andevmarketlist.databinding.ActivityHistorialBinding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class HistorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(
            pantalla_menu.NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        configurarBotonesNavegacion()
        cargarHistorial()

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
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun cargarHistorial() {
        val contenedor = binding.linearLayoutContenedor
        contenedor.removeAllViews()

        val listasCompletadas = obtenerTodasListas()
            .filter { it.completada }
            .sortedByDescending { it.fechaCreacion }

        if (listasCompletadas.isEmpty()) {
            val tvMensaje = TextView(this).apply {
                text = "No hay listas completadas en el historial"
                textSize = 18f
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)
                setPadding(0, 100, 0, 0)
            }
            contenedor.addView(tvMensaje)
            return
        }

        listasCompletadas.forEachIndexed { index, lista ->
            contenedor.addView(crearCardListaCompletada(lista, index))
        }
    }

    private fun crearCardListaCompletada(
        lista: ListaApp,
        index: Int
    ): LinearLayout {

        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(25, 20, 25, 20)
            setBackgroundColor(Color.parseColor("#F0F0F0"))

            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 8, 16, 16)
            }

            addView(TextView(context).apply {
                text = lista.nombre
                textSize = 18f
                setTextColor(Color.BLACK)
                setTypeface(null, android.graphics.Typeface.BOLD)
                setPadding(0, 0, 0, 12)
            })

            val infoLayout = LinearLayout(context).apply {
                orientation = LinearLayout.VERTICAL
            }

            infoLayout.addView(TextView(context).apply {
                text = "Completada"
                textSize = 14f
                setTextColor(Color.parseColor("#4CAF50"))
            })

            infoLayout.addView(TextView(context).apply {
                text = "Creada: ${lista.fechaCreacion}"
                textSize = 14f
                setTextColor(Color.DKGRAY)
            })

            infoLayout.addView(TextView(context).apply {
                text = "Prioridad: ${lista.prioridad}"
                textSize = 14f
                setTextColor(Color.DKGRAY)
            })

            if (lista.productos.isNotEmpty()) {
                val productosTexto =
                    if (lista.productos.size > 3)
                        lista.productos.take(3).joinToString(", ") + ", ..."
                    else
                        lista.productos.joinToString(", ")

                infoLayout.addView(TextView(context).apply {
                    text = "Productos: $productosTexto"
                    textSize = 14f
                    setTextColor(Color.DKGRAY)
                })
            }

            addView(infoLayout)

            setOnClickListener {
                startActivity(
                    Intent(context, VerListaActivity::class.java)
                        .putExtra(VerListaActivity.EXTRA_LISTA_ID, lista.id)
                )
            }
        }
    }

    private fun configurarBotonesNavegacion() {
        binding.botonCalendario.setOnClickListener {
            startActivity(Intent(this, activity_Calendario::class.java))
        }

        binding.botonInicio.setOnClickListener {
            startActivity(Intent(this, pantalla_menu::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        cargarHistorial()
    }
}
