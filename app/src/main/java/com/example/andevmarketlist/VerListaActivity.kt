package com.example.andevmarketlist

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.dataclases.ListaApp
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class VerListaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LISTA_ID = "ID_LISTA"
    }

    private lateinit var listaActual: ListaApp
    private lateinit var sharedPreferences: SharedPreferences
    private val checkboxesProductos = mutableListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_lista)

        sharedPreferences = getSharedPreferences(
            pantalla_menu.NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        val listaId = intent.getStringExtra(EXTRA_LISTA_ID)

        if (listaId == null) {
            Toast.makeText(this, "Error: Lista no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val lista = buscarListaPorId(listaId)

        if (lista == null) {
            Toast.makeText(this, "Lista no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        listaActual = lista

        configurarInterfaz(lista)

        configurarCheckboxListaCompletada()

        configurarBotonEliminarLista()

        configurarBotonGuardarCambios()

        configurarAgregarProducto()

        configurarBotonesNavegacion()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun buscarListaPorId(id: String): ListaApp? {
        val stringGuardado = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        )

        return if (stringGuardado != null) {
            try {
                val listas = Json.decodeFromString<List<ListaApp>>(stringGuardado)
                listas.find { it.id == id }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    private fun configurarInterfaz(lista: ListaApp) {
        val textViewTitulo = findViewById<TextView>(R.id.textViewTituloLista)
        textViewTitulo.text = lista.nombre

        val textViewFechaLimite = findViewById<TextView>(R.id.textViewFechaLimite)
        if (lista.fechaLimite != null) {
            textViewFechaLimite.text = "Fecha límite: ${lista.fechaLimite}"
        } else {
            textViewFechaLimite.text = "Sin fecha límite"
        }

        val textViewPrioridad = findViewById<TextView>(R.id.textViewPrioridad)
        textViewPrioridad.text = "Prioridad: ${lista.prioridad}"

        val textViewFechaCreacion = findViewById<TextView>(R.id.textViewFechaCreacion)
        textViewFechaCreacion.text = "Creada: ${lista.fechaCreacion}"

        mostrarProductos()
    }

    private fun mostrarProductos() {
        val linearLayoutProductos = findViewById<LinearLayout>(R.id.linearLayoutProductos)
        linearLayoutProductos.removeAllViews()
        checkboxesProductos.clear()

        if (listaActual.productos.isEmpty()) {
            val tvVacio = TextView(this).apply {
                text = "No hay productos en esta lista"
                textSize = 16f
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)
                setPadding(0, 40, 0, 0)
            }
            linearLayoutProductos.addView(tvVacio)
        } else {
            listaActual.productos.forEachIndexed { index, producto ->
                val productoLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(25, 12, 25, 12)

                    if (index % 2 == 0) {
                        setBackgroundColor(Color.parseColor("#F5F5F5"))
                    } else {
                        setBackgroundColor(Color.WHITE)
                    }
                }

                val checkBoxProducto = CheckBox(this).apply {
                    text = producto
                    textSize = 16f
                    setTextColor(Color.BLACK)
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )

                    if (listaActual.completada) {
                        isChecked = true
                    }
                }

                checkboxesProductos.add(checkBoxProducto)

                productoLayout.addView(checkBoxProducto)

                linearLayoutProductos.addView(productoLayout)
            }
        }
    }

    private fun configurarCheckboxListaCompletada() {
        val checkBoxListaCompletada = findViewById<CheckBox>(R.id.checkBoxListaCompletada)

        checkBoxListaCompletada.isChecked = listaActual.completada

        actualizarTextoCheckboxLista(checkBoxListaCompletada)

        checkBoxListaCompletada.setOnCheckedChangeListener { _, isChecked ->
            listaActual = listaActual.copy(completada = isChecked)

            actualizarTextoCheckboxLista(checkBoxListaCompletada)

            if (isChecked) {
                checkboxesProductos.forEach { checkbox ->
                    checkbox.isChecked = true
                }
            } else {
                checkboxesProductos.forEach { checkbox ->
                    checkbox.isChecked = false
                }
            }

        }
    }

    private fun configurarBotonEliminarLista() {
        val botonEliminar = findViewById<Button>(R.id.botonEliminarLista)

        botonEliminar.setOnClickListener {
            eliminarLista()
        }
    }

    private fun configurarBotonGuardarCambios() {
        val botonGuardar = findViewById<Button>(R.id.botonGuardarCambios)

        botonGuardar.setOnClickListener {
            guardarCambiosLista()
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarAgregarProducto() {
        val editTextNuevoProducto = findViewById<EditText>(R.id.editTextNuevoProducto)
        val botonAgregar = findViewById<Button>(R.id.botonAgregarProducto)

        botonAgregar.setOnClickListener {
            val nuevoProducto = editTextNuevoProducto.text.toString().trim()

            if (nuevoProducto.isEmpty()) {
                Toast.makeText(this, "Escribe un producto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val productosActualizados = listaActual.productos.toMutableList()
            productosActualizados.add(nuevoProducto)
            listaActual = listaActual.copy(productos = productosActualizados)

            editTextNuevoProducto.text.clear()

            mostrarProductos()

            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(editTextNuevoProducto.windowToken, 0)

            Toast.makeText(this, "Producto añadido", Toast.LENGTH_SHORT).show()
        }

        editTextNuevoProducto.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {
                botonAgregar.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun eliminarLista() {
        val stringGuardado = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        )

        if (stringGuardado != null) {
            try {
                val todasListas = Json.decodeFromString<List<ListaApp>>(stringGuardado)

                val listasActualizadas = todasListas.filter { it.id != listaActual.id }

                val editor = sharedPreferences.edit()
                val listaString = Json.encodeToString(listasActualizadas)
                editor.putString(pantalla_menu.NOMBRE_DATO_LISTAS, listaString)
                editor.apply()

                Toast.makeText(this, "Lista eliminada", Toast.LENGTH_SHORT).show()

                finish()

            } catch (e: Exception) {
                Toast.makeText(this, "Error al eliminar lista", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCambiosLista() {
        val stringGuardado = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        )

        if (stringGuardado != null) {
            try {
                val todasListas = Json.decodeFromString<List<ListaApp>>(stringGuardado)

                val listasActualizadas = todasListas.map { lista ->
                    if (lista.id == listaActual.id) {
                        listaActual
                    } else {
                        lista
                    }
                }

                val editor = sharedPreferences.edit()
                val listaString = Json.encodeToString(listasActualizadas)
                editor.putString(pantalla_menu.NOMBRE_DATO_LISTAS, listaString)
                editor.apply()

            } catch (e: Exception) {
                Toast.makeText(this, "Error al guardar cambios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun actualizarTextoCheckboxLista(checkBox: CheckBox) {
        val texto = if (listaActual.completada) {
            "Lista completada"
        } else {
            "Marcar lista como completada"
        }
        checkBox.text = texto
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
}