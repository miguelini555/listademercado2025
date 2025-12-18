package com.example.andevmarketlist

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.databinding.ActivityVerListaBinding
import com.example.andevmarketlist.dataclases.ListaApp
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class VerListaActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LISTA_ID = "ID_LISTA"
    }

    private lateinit var binding: ActivityVerListaBinding
    private lateinit var listaActual: ListaApp
    private lateinit var sharedPreferences: SharedPreferences
    private val checkboxesProductos = mutableListOf<CheckBox>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityVerListaBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        configurarInterfaz()
        configurarCheckboxListaCompletada()
        configurarBotonEliminarLista()
        configurarBotonGuardarCambios()
        configurarAgregarProducto()
        configurarBotonesNavegacion()

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

    private fun buscarListaPorId(id: String): ListaApp? {
        val stringGuardado = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        ) ?: return null

        return try {
            val listas = Json.decodeFromString<List<ListaApp>>(stringGuardado)
            listas.find { it.id == id }
        } catch (e: Exception) {
            null
        }
    }

    private fun configurarInterfaz() {
        binding.textViewTituloLista.text = listaActual.nombre
        binding.textViewPrioridad.text = "Prioridad: ${listaActual.prioridad}"
        binding.textViewFechaCreacion.text = "Creada: ${listaActual.fechaCreacion}"

        binding.textViewFechaLimite.text =
            listaActual.fechaLimite?.let { "Fecha límite: $it" } ?: "Sin fecha límite"

        mostrarProductos()
    }

    private fun mostrarProductos() {
        binding.linearLayoutProductos.removeAllViews()
        checkboxesProductos.clear()

        if (listaActual.productos.isEmpty()) {
            val tv = TextView(this).apply {
                text = "No hay productos en esta lista"
                textSize = 16f
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)
                setPadding(0, 40, 0, 0)
            }
            binding.linearLayoutProductos.addView(tv)
            return
        }

        listaActual.productos.forEachIndexed { index, producto ->
            val fila = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(25, 12, 25, 12)
                setBackgroundColor(
                    if (index % 2 == 0) Color.parseColor("#F5F5F5") else Color.WHITE
                )
            }

            val checkBox = CheckBox(this).apply {
                text = producto
                textSize = 16f
                setTextColor(Color.BLACK)
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
                isChecked = listaActual.completada
            }

            checkboxesProductos.add(checkBox)
            fila.addView(checkBox)
            binding.linearLayoutProductos.addView(fila)
        }
    }

    private fun configurarCheckboxListaCompletada() {
        binding.checkBoxListaCompletada.isChecked = listaActual.completada
        actualizarTextoCheckboxLista()

        binding.checkBoxListaCompletada.setOnCheckedChangeListener { _, isChecked ->
            listaActual = listaActual.copy(completada = isChecked)
            actualizarTextoCheckboxLista()
            checkboxesProductos.forEach { it.isChecked = isChecked }
        }
    }

    private fun configurarBotonEliminarLista() {
        binding.botonEliminarLista.setOnClickListener {
            eliminarLista()
        }
    }

    private fun configurarBotonGuardarCambios() {
        binding.botonGuardarCambios.setOnClickListener {
            guardarCambiosLista()
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarAgregarProducto() {
        binding.botonAgregarProducto.setOnClickListener {
            val nuevoProducto = binding.editTextNuevoProducto.text.toString().trim()
            if (nuevoProducto.isEmpty()) {
                Toast.makeText(this, "Escribe un producto", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            listaActual = listaActual.copy(
                productos = listaActual.productos + nuevoProducto
            )

            binding.editTextNuevoProducto.text.clear()
            mostrarProductos()

            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.editTextNuevoProducto.windowToken, 0)

            Toast.makeText(this, "Producto añadido", Toast.LENGTH_SHORT).show()
        }

        binding.editTextNuevoProducto.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.botonAgregarProducto.performClick()
                true
            } else false
        }
    }

    private fun eliminarLista() {
        val stringGuardado = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        ) ?: return

        try {
            val listas = Json.decodeFromString<List<ListaApp>>(stringGuardado)
            val nuevasListas = listas.filter { it.id != listaActual.id }

            sharedPreferences.edit()
                .putString(
                    pantalla_menu.NOMBRE_DATO_LISTAS,
                    Json.encodeToString(nuevasListas)
                )
                .apply()

            Toast.makeText(this, "Lista eliminada", Toast.LENGTH_SHORT).show()
            finish()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al eliminar lista", Toast.LENGTH_SHORT).show()
        }
    }

    private fun guardarCambiosLista() {
        val stringGuardado = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        ) ?: return

        try {
            val listas = Json.decodeFromString<List<ListaApp>>(stringGuardado)
            val nuevasListas = listas.map {
                if (it.id == listaActual.id) listaActual else it
            }

            sharedPreferences.edit()
                .putString(
                    pantalla_menu.NOMBRE_DATO_LISTAS,
                    Json.encodeToString(nuevasListas)
                )
                .apply()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar cambios", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarTextoCheckboxLista() {
        binding.checkBoxListaCompletada.text =
            if (listaActual.completada)
                "Lista completada"
            else
                "Marcar lista como completada"
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
}
