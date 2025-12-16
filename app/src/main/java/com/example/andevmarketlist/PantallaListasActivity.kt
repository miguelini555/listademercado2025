package com.example.andevmarketlist

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.dataclases.ListaApp
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.*

class PantallaListasActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val productosList = mutableListOf<String>()
    private var fechaLimiteSeleccionada: String? = null
    private var prioridadSeleccionada: String = "Media"

    companion object {
        val NOMBRE_FICHERO_SHARED_PREFERENCES = "MarketListApp"
        val NOMBRE_DATO_LISTAS = "DatoListas"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_listas)

        sharedPreferences = getSharedPreferences(
            NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        val botonFecha = findViewById<Button>(R.id.button_fecha)
        botonFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val mesReal = month + 1
                    fechaLimiteSeleccionada = "%02d/%02d/%04d".format(dayOfMonth, mesReal, year)
                    botonFecha.text = "Fecha: $fechaLimiteSeleccionada"
                },
                anio,
                mes,
                dia
            )
            datePicker.show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botonCalendario = findViewById<ImageButton>(R.id.botonCalendario)
        val botonInicio = findViewById<ImageButton>(R.id.botonInicio)
        val botonAlarma = findViewById<ImageButton>(R.id.botonAlarma)
        val botonGuardar = findViewById<Button>(R.id.button_guardar)

        val editTextProducto = findViewById<EditText>(R.id.edittext_nombreproducto)
        val listaProducts = findViewById<LinearLayout>(R.id.listaProducts)
        val botonAgregar = findViewById<Button>(R.id.button_agregar)
        val editNombreLista = findViewById<EditText>(R.id.edittext_nombrelista)

        botonCalendario.setOnClickListener {
            val intent = Intent(this, activity_Calendario::class.java)
            startActivity(intent)
        }

        botonInicio.setOnClickListener {
            val intent = Intent(this, pantalla_menu::class.java)
            startActivity(intent)
        }

        botonAlarma.setOnClickListener {
            val intent = Intent(this, NotificacionesActivity::class.java)
            startActivity(intent)
        }

        botonGuardar.setOnClickListener {
            val nombreLista = editNombreLista.text.toString().trim()
            guardarListaEnSharedPreferences(nombreLista)
        }

        botonAgregar.setOnClickListener {
            val texto = editTextProducto.text.toString().trim()

            if (texto.isEmpty()) {
                Toast.makeText(this, "Ingresa un producto primero", Toast.LENGTH_SHORT).show()
            } else {
                productosList.add(texto)

                val nuevoProducto = TextView(this)
                nuevoProducto.text = "- $texto"
                nuevoProducto.textSize = 18f
                nuevoProducto.setPadding(10, 10, 10, 10)

                listaProducts.addView(nuevoProducto)
                editTextProducto.text.clear()
            }
        }

        val botonPrioridad = findViewById<Button>(R.id.button_prioridad)
        botonPrioridad.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.menu_prioridad, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.prioridad_baja -> {
                        botonPrioridad.text = "Prioridad: Baja"
                        prioridadSeleccionada = "Baja"
                    }
                    R.id.prioridad_media -> {
                        botonPrioridad.text = "Prioridad: Media"
                        prioridadSeleccionada = "Media"
                    }
                    R.id.prioridad_alta -> {
                        botonPrioridad.text = "Prioridad: Alta"
                        prioridadSeleccionada = "Alta"
                    }
                }
                true
            }
            popup.show()
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

        val editor = sharedPreferences.edit()

        val listasActuales = obtenerListasGuardadas()

        val nuevaLista = ListaApp(
            id = UUID.randomUUID().toString(),
            nombre = nombreLista,
            fechaLimite = fechaLimiteSeleccionada,
            prioridad = prioridadSeleccionada,
            productos = productosList.toList(),
            fechaCreacion = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
            completada = false
        )

        val todasListas = listasActuales + nuevaLista
        val listaString = Json.encodeToString(todasListas)

        editor.putString(NOMBRE_DATO_LISTAS, listaString)
        editor.apply()

        Toast.makeText(this, "Lista guardada", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, pantalla_menu::class.java)
        startActivity(intent)
    }

    private fun obtenerListasGuardadas(): List<ListaApp> {
        val stringGuardado: String? = sharedPreferences.getString(
            NOMBRE_DATO_LISTAS,
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

    private fun guardarDatosSharedPreferences(nombreDelDato: String, datoAGuardar: String) {
        val editor = sharedPreferences.edit()
        editor.putString(nombreDelDato, datoAGuardar)
        editor.apply()
    }

    private fun guardarDatosSharedPreferences(listas: List<ListaApp>) {
        val listaString = Json.encodeToString(listas)
        guardarDatosSharedPreferences(NOMBRE_DATO_LISTAS, listaString)
    }
}