/*
package com.example.andevmarketlist

import android.app.DatePickerDialog
import java.util.Calendar
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class PantallaListasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_listas)
//12341234
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
                    val fechaSeleccionada = "%02d/%02d/%04d".format(dayOfMonth, mesReal, year)
                    botonFecha.text = fechaSeleccionada
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
        val listaProductos = findViewById<LinearLayout>(R.id.listaProductos)
        val botonAgregar = findViewById<Button>(R.id.button_agregar)

        botonCalendario.setOnClickListener {
            val intent = Intent(this, activity_Calendario::class.java)
            startActivity(intent)
        }

        botonInicio.setOnClickListener {
            val intent = Intent(this, pantalla_menu::class.java)
            startActivity(intent)
        }

        botonGuardar.setOnClickListener {
            val intent = Intent(this, pantalla_menu::class.java)
            startActivity(intent)
        }

        botonAlarma.setOnClickListener {
            val intent = Intent(this, pantalla_historial::class.java)
            startActivity(intent)
        }

        botonAgregar.setOnClickListener {
            val texto = editTextProducto.text.toString().trim()

            if (texto.isEmpty()) {
                Toast.makeText(this, "Ingresa un producto primero", Toast.LENGTH_SHORT).show()
            } else {
                val nuevoProducto = TextView(this)
                nuevoProducto.text = "- $texto"
                nuevoProducto.textSize = 18f
                nuevoProducto.setPadding(10, 10, 10, 10)

                listaProductos.addView(nuevoProducto)
                editTextProducto.text.clear()
            }

        }

        val botonPrioridad = findViewById<Button>(R.id.button_prioridad)

        botonPrioridad.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.menu_prioridad, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.prioridad_baja -> botonPrioridad.text = "Baja"
                    R.id.prioridad_media -> botonPrioridad.text = "Media"
                    R.id.prioridad_alta -> botonPrioridad.text = "Alta"
                }
                true
            }

            popup.show()
        }


    }
}
 */

/*
package com.example.andevmarketlist

import android.app.DatePickerDialog
import java.util.Calendar
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID


class PantallaListasActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val productosTemp = mutableListOf<String>()

    companion object {
        val NOMBRE_FICHERO_SHARED_PREFERENCES = "MarketListApp"
        val NOMBRE_DATO_LISTAS = "DatoListas"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_listas)
//12341234
        val botonFecha = findViewById<Button>(R.id.button_fecha)

        sharedPreferences = this.getSharedPreferences(
            NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        botonFecha.setOnClickListener {
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val mesReal = month + 1
                    val fechaSeleccionada = "%02d/%02d/%04d".format(dayOfMonth, mesReal, year)
                    botonFecha.text = fechaSeleccionada
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
        val listaProductos = findViewById<LinearLayout>(R.id.listaProductos)
        val botonAgregar = findViewById<Button>(R.id.button_agregar)

        botonCalendario.setOnClickListener {
            val intent = Intent(this, activity_Calendario::class.java)
            startActivity(intent)
        }

        botonInicio.setOnClickListener {
            val intent = Intent(this, pantalla_menu::class.java)
            startActivity(intent)
        }

        botonGuardar.setOnClickListener {
            guardarListaEnSharedPreferences()
        }

        botonAgregar.setOnClickListener {
            val texto = editTextProducto.text.toString().trim()

            if (texto.isEmpty()) {
                Toast.makeText(this, "Ingresa un producto primero", Toast.LENGTH_SHORT).show()
            } else {
                val nuevoProducto = TextView(this)
                nuevoProducto.text = "- $texto"
                nuevoProducto.textSize = 18f
                nuevoProducto.setPadding(10, 10, 10, 10)

                listaProductos.addView(nuevoProducto)
                editTextProducto.text.clear()
            }
        }

        val botonPrioridad = findViewById<Button>(R.id.button_prioridad)

        botonPrioridad.setOnClickListener { view ->
            val popup = PopupMenu(this, view)
            popup.menuInflater.inflate(R.menu.menu_prioridad, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.prioridad_baja -> botonPrioridad.text = "Baja"
                    R.id.prioridad_media -> botonPrioridad.text = "Media"
                    R.id.prioridad_alta -> botonPrioridad.text = "Alta"
                }
                true
            }

            popup.show()
        }

        private fun guardarListaEnSharedPreferences() {
            // Validar
            if (productosTemp.isEmpty()) {
                Toast.makeText(this, "Agrega al menos un producto", Toast.LENGTH_SHORT).show()
                return
            }

            val botonFecha = findViewById<Button>(R.id.button_fecha)
            val botonPrioridad = findViewById<Button>(R.id.button_prioridad)

            val nombreLista = "Lista del día"
            val fechaLimite = botonFecha.text.toString()
            val prioridad = botonPrioridad.text.toString()

            // Crear objeto ListaApp
            val nuevaLista = ListaApp(
                nombre = nombreLista,
                fechaLimite = if (fechaLimite != "Fecha_Limite") fechaLimite else null,
                prioridad = if (prioridad != "Prioridad") prioridad else "Media",
                productos = productosTemp.toList()
            )

            // 1. Obtener listas existentes
            val listasExistentes = obtenerTodasListas().toMutableList()
            listasExistentes.add(nuevaLista)

            // 2. Convertir a JSON (COMO EN CLASE: Json.encodeToString)
            val listaString = Json.encodeToString(listasExistentes)

            // 3. Guardar en SharedPreferences (PATRÓN IDÉNTICO a clase)
            val editor = sharedPreferences.edit()
            editor.putString(NOMBRE_DATO_LISTAS, listaString)
            editor.apply()

            Toast.makeText(this, "✅ Lista guardada exitosamente", Toast.LENGTH_SHORT).show()

    }
}*/



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

        // Inicializar SharedPreferences (PATRÓN DE CLASE)
        sharedPreferences = getSharedPreferences(
            NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        // ========== SELECTOR DE FECHA ==========
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

        // ========== WINDOW INSETS ==========
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // ========== BOTONES DE NAVEGACIÓN ==========
        val botonCalendario = findViewById<ImageButton>(R.id.botonCalendario)
        val botonInicio = findViewById<ImageButton>(R.id.botonInicio)
        val botonAlarma = findViewById<ImageButton>(R.id.botonAlarma)
        val botonGuardar = findViewById<Button>(R.id.button_guardar)

        // ========== AGREGAR PRODUCTOS ==========
        val editTextProducto = findViewById<EditText>(R.id.edittext_nombreproducto)
        val listaProducts = findViewById<LinearLayout>(R.id.listaProducts)
        val botonAgregar = findViewById<Button>(R.id.button_agregar)
        val editNombreLista = findViewById<EditText>(R.id.edittext_nombrelista)

        // ========== LISTENERS DE BOTONES ==========
        botonCalendario.setOnClickListener {
            val intent = Intent(this, activity_Calendario::class.java)
            startActivity(intent)
        }

        botonInicio.setOnClickListener {
            val intent = Intent(this, pantalla_menu::class.java)
            startActivity(intent)
        }

        // AQUI - MODIFICACION: Ahora usa función con patrón de clase
        botonGuardar.setOnClickListener {
            val nombreLista = editNombreLista.text.toString().trim()
            guardarListaEnSharedPreferences(nombreLista)
        }

        botonAlarma.setOnClickListener {
            val intent = Intent(this, NotificacionesActivity::class.java)
            startActivity(intent)
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

        // ========== MENÚ DE PRIORIDAD ==========
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

    // AQUI - MODIFICACION: Función de guardar siguiendo patrón de clase
    private fun guardarListaEnSharedPreferences(nombreLista: String) {
        // Validaciones (como en clase)
        if (nombreLista.isEmpty()) {
            Toast.makeText(this, "Ingresa un nombre para la lista", Toast.LENGTH_SHORT).show()
            return
        }

        if (productosList.isEmpty()) {
            Toast.makeText(this, "Agrega al menos un producto", Toast.LENGTH_SHORT).show()
            return
        }

        val editor = sharedPreferences.edit()

        // Obtener listas actuales (PATRÓN DE CLASE)
        val listasActuales = obtenerListasGuardadas()

        // Crear nueva lista
        val nuevaLista = ListaApp(
            id = UUID.randomUUID().toString(),
            nombre = nombreLista,
            fechaLimite = fechaLimiteSeleccionada,
            prioridad = prioridadSeleccionada,
            productos = productosList.toList(),
            fechaCreacion = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date()),
            completada = false
        )

        // Combinar listas
        val todasListas = listasActuales + nuevaLista
        val listaString = Json.encodeToString(todasListas)

        // Guardar (PATRÓN DE CLASE EXACTO)
        editor.putString(NOMBRE_DATO_LISTAS, listaString)
        editor.apply() // En clase usan apply() no commit()

        Toast.makeText(this, "Lista '$nombreLista' guardada", Toast.LENGTH_SHORT).show()

        // Regresar al menú principal
        val intent = Intent(this, pantalla_menu::class.java)
        startActivity(intent)
    }

    // AQUI - MODIFICACION: Exactamente como en clase
    private fun obtenerListasGuardadas(): List<ListaApp> {
        val stringGuardado: String? = sharedPreferences.getString(
            NOMBRE_DATO_LISTAS,
            "[]"
        )

        // PATRÓN DE CLASE EXACTO:
        // 1. Obtener string
        // 2. Mostrar en UI (si aplica) - en clase usan binding.textMostrarSharedAlmacenado.text
        // 3. Convertir y retornar

        return if (stringGuardado != null) {
            try {
                Json.decodeFromString<List<ListaApp>>(stringGuardado)
            } catch (e: Exception) {
                // En clase muestran mensaje de error
                // Aquí retornamos lista vacía como en patrón de clase
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    // AQUI - NUEVO: Función adicional siguiendo patrón de clase
    // (Opcional, para consistencia con código de clase)
    private fun guardarDatosSharedPreferences(nombreDelDato: String, datoAGuardar: String) {
        val editor = sharedPreferences.edit()
        editor.putString(nombreDelDato, datoAGuardar)
        editor.apply() // PATRÓN DE CLASE: apply() en lugar de commit()
    }

    // AQUI - NUEVO: Versión específica para listas
    private fun guardarDatosSharedPreferences(listas: List<ListaApp>) {
        val listaString = Json.encodeToString(listas)
        guardarDatosSharedPreferences(NOMBRE_DATO_LISTAS, listaString)
    }
}