package com.example.andevmarketlist

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.databinding.ActivityPantallaListasBinding
import com.example.andevmarketlist.dataclases.ListaApp
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class PantallaListasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPantallaListasBinding
    private lateinit var sharedPreferences: SharedPreferences

    private val productosList = mutableListOf<String>()
    private var fechaLimiteSeleccionada: String? = null
    private var prioridadSeleccionada: String = "Media"

    companion object {
        const val NOMBRE_FICHERO_SHARED_PREFERENCES = "MarketListApp"
        const val NOMBRE_DATO_LISTAS = "DatoListas"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPantallaListasBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(
            NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        configurarFecha()
        configurarPrioridad()
        configurarBotonesNavegacion()
        configurarAgregarProducto()
        configurarGuardarLista()

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

    private fun configurarFecha() {
        binding.buttonFecha.setOnClickListener {
            val calendario = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val mesReal = month + 1
                    fechaLimiteSeleccionada =
                        "%02d/%02d/%04d".format(day, mesReal, year)
                    binding.buttonFecha.text = "Fecha: $fechaLimiteSeleccionada"
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun configurarPrioridad() {
        binding.buttonPrioridad.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.menu_prioridad, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.prioridad_baja -> {
                        prioridadSeleccionada = "Baja"
                        binding.buttonPrioridad.text = "Prioridad: Baja"
                    }
                    R.id.prioridad_media -> {
                        prioridadSeleccionada = "Media"
                        binding.buttonPrioridad.text = "Prioridad: Media"
                    }
                    R.id.prioridad_alta -> {
                        prioridadSeleccionada = "Alta"
                        binding.buttonPrioridad.text = "Prioridad: Alta"
                    }
                }
                true
            }
            popup.show()
        }
    }

    private fun configurarBotonesNavegacion() {
        binding.botonCalendario.setOnClickListener {
            startActivity(Intent(this, activity_Calendario::class.java))
        }

        binding.botonInicio.setOnClickListener {
            startActivity(Intent(this, pantalla_menu::class.java))
        }

        binding.botonAlarma.setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
        }
    }

    private fun configurarAgregarProducto() {
        binding.buttonAgregar.setOnClickListener {
            val texto = binding.edittextNombreproducto.text.toString().trim()

            if (texto.isEmpty()) {
                Toast.makeText(this, "Ingresa un producto primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            productosList.add(texto)

            val nuevoProducto = TextView(this).apply {
                text = "- $texto"
                textSize = 18f
                setPadding(10, 10, 10, 10)
            }

            binding.listaProducts.addView(nuevoProducto)
            binding.edittextNombreproducto.text.clear()
        }
    }

    private fun configurarGuardarLista() {
        binding.buttonGuardar.setOnClickListener {
            val nombreLista = binding.edittextNombrelista.text.toString().trim()
            guardarListaEnSharedPreferences(nombreLista)
        }
    }

    private fun guardarListaEnSharedPreferences(nombreLista: String) {
        if (nombreLista.isEmpty()) {
            Toast.makeText(this, "Ingresa un nombre para la lista", Toast.LENGTH_SHORT).show()
            return
        }

        if (productosList.isEmpty()) {
            Toast.makeText(this, "Agrega al menos un producto", Toast.LENGTH_SHORT).show()
            return
        }

        val listasActuales = obtenerListasGuardadas()

        val nuevaLista = ListaApp(
            id = UUID.randomUUID().toString(),
            nombre = nombreLista,
            fechaLimite = fechaLimiteSeleccionada,
            prioridad = prioridadSeleccionada,
            productos = productosList.toList(),
            fechaCreacion = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date()),
            completada = false
        )

        val todasListas = listasActuales + nuevaLista
        val listaString = Json.encodeToString(todasListas)

        sharedPreferences.edit()
            .putString(NOMBRE_DATO_LISTAS, listaString)
            .apply()

        Toast.makeText(this, "Lista guardada", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, pantalla_menu::class.java))
        finish()
    }

    private fun obtenerListasGuardadas(): List<ListaApp> {
        val stringGuardado = sharedPreferences.getString(
            NOMBRE_DATO_LISTAS,
            "[]"
        )

        return try {
            Json.decodeFromString(stringGuardado ?: "[]")
        } catch (e: Exception) {
            emptyList()
        }
    }
}
