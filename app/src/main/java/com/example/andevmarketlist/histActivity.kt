package com.example.andevmarketlist

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.dataclases.ListaApp
import com.example.andevmarketlist.databinding.ActivityHistBinding
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class histActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHistBinding.inflate(layoutInflater)
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
        val data = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        )

        return try {
            Json.decodeFromString(data ?: "[]")
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun cargarHistorial() {
        val contenedor = binding.contenedorHistorial
        val mensajeVacio = binding.textViewMensajeVacio

        contenedor.removeAllViews()

        val listasCompletadas = obtenerTodasListas().filter { it.completada }

        if (listasCompletadas.isEmpty()) {
            mensajeVacio.visibility = View.VISIBLE
            return
        }

        mensajeVacio.visibility = View.GONE

        listasCompletadas.forEach {
            contenedor.addView(crearCard(it))
        }
    }

    private fun crearCard(lista: ListaApp): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(20, 20, 20, 20)
            setBackgroundColor(Color.parseColor("#E8E8E8"))

            addView(TextView(context).apply {
                text = lista.nombre
                textSize = 18f
            })

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
            finish()
        }

        binding.botonAlarma.setOnClickListener {
            cargarHistorial()
            Toast.makeText(this, "Historial actualizado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        cargarHistorial()
    }
}
