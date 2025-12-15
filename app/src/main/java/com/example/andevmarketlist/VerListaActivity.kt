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

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences(
            pantalla_menu.NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        // Obtener ID de la lista desde el Intent
        val listaId = intent.getStringExtra(EXTRA_LISTA_ID)

        if (listaId == null) {
            Toast.makeText(this, "Error: Lista no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Buscar la lista por ID
        val lista = buscarListaPorId(listaId)

        if (lista == null) {
            Toast.makeText(this, "Lista no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        listaActual = lista

        // ========== CONFIGURAR INTERFAZ ==========
        configurarInterfaz(lista)

        // ========== CONFIGURAR CHECKBOX DE LISTA COMPLETADA ==========
        configurarCheckboxListaCompletada()

        // ========== CONFIGURAR BOTON ELIMINAR LISTA ==========
        configurarBotonEliminarLista()

        // ========== BOTONES DE NAVEGACION ==========
        configurarBotonesNavegacion()

        // ========== WINDOW INSETS ==========
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
        // Configurar titulo
        val textViewTitulo = findViewById<TextView>(R.id.textViewTituloLista)
        textViewTitulo.text = lista.nombre

        // Configurar fecha limite
        val textViewFechaLimite = findViewById<TextView>(R.id.textViewFechaLimite)
        if (lista.fechaLimite != null) {
            textViewFechaLimite.text = "Fecha limite: ${lista.fechaLimite}"
        } else {
            textViewFechaLimite.text = "Sin fecha limite"
        }

        // Configurar prioridad
        val textViewPrioridad = findViewById<TextView>(R.id.textViewPrioridad)
        textViewPrioridad.text = "Prioridad: ${lista.prioridad}"

        // Configurar fecha de creacion
        val textViewFechaCreacion = findViewById<TextView>(R.id.textViewFechaCreacion)
        textViewFechaCreacion.text = "Creada: ${lista.fechaCreacion}"

        // Configurar productos con checkboxes
        val linearLayoutProductos = findViewById<LinearLayout>(R.id.linearLayoutProductos)
        linearLayoutProductos.removeAllViews()
        checkboxesProductos.clear()

        if (lista.productos.isEmpty()) {
            val tvVacio = TextView(this).apply {
                text = "No hay productos en esta lista"
                textSize = 16f
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)
                setPadding(0, 40, 0, 0)
            }
            linearLayoutProductos.addView(tvVacio)
        } else {
            lista.productos.forEachIndexed { index, producto ->
                // Crear LinearLayout horizontal para cada producto
                val productoLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(25, 12, 25, 12)

                    // Alternar colores de fondo
                    if (index % 2 == 0) {
                        setBackgroundColor(Color.parseColor("#F5F5F5"))
                    } else {
                        setBackgroundColor(Color.WHITE)
                    }
                }

                // Checkbox para el producto
                val checkBoxProducto = CheckBox(this).apply {
                    text = producto
                    textSize = 16f
                    setTextColor(Color.BLACK)
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }

                // Guardar referencia al checkbox
                checkboxesProductos.add(checkBoxProducto)

                // Agregar checkbox al layout del producto
                productoLayout.addView(checkBoxProducto)

                // Agregar layout del producto al contenedor principal
                linearLayoutProductos.addView(productoLayout)
            }
        }
    }

    private fun configurarCheckboxListaCompletada() {
        val checkBoxListaCompletada = findViewById<CheckBox>(R.id.checkBoxListaCompletada)

        // Establecer estado actual de la lista
        checkBoxListaCompletada.isChecked = listaActual.completada

        // Cambiar texto segun estado
        actualizarTextoCheckboxLista(checkBoxListaCompletada)

        // Listener para cuando cambia el estado
        checkBoxListaCompletada.setOnCheckedChangeListener { _, isChecked ->
            // Actualizar el estado en el objeto
            listaActual = listaActual.copy(completada = isChecked)

            // Actualizar texto
            actualizarTextoCheckboxLista(checkBoxListaCompletada)

            // Si se marca la lista como completada, marcar todos los productos
            if (isChecked) {
                checkboxesProductos.forEach { checkbox ->
                    checkbox.isChecked = true
                }
            }

            // Guardar cambios
            guardarCambiosLista()

            // Mostrar mensaje
            val mensaje = if (isChecked) {
                "Lista marcada como completada"
            } else {
                "Lista marcada como pendiente"
            }
            Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        }
    }

    private fun configurarBotonEliminarLista() {
        val botonEliminar = findViewById<Button>(R.id.botonEliminarLista)

        botonEliminar.setOnClickListener {
            eliminarLista()
        }
    }

    private fun eliminarLista() {
        val stringGuardado = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        )

        if (stringGuardado != null) {
            try {
                // Obtener todas las listas
                val todasListas = Json.decodeFromString<List<ListaApp>>(stringGuardado)

                // Filtrar para eliminar la lista actual
                val listasActualizadas = todasListas.filter { it.id != listaActual.id }

                // Guardar de nuevo
                val editor = sharedPreferences.edit()
                val listaString = Json.encodeToString(listasActualizadas)
                editor.putString(pantalla_menu.NOMBRE_DATO_LISTAS, listaString)
                editor.apply()

                // Mostrar mensaje
                Toast.makeText(this, "Lista eliminada", Toast.LENGTH_SHORT).show()

                // Regresar al menu principal
                finish()

            } catch (e: Exception) {
                Toast.makeText(this, "Error al eliminar lista", Toast.LENGTH_SHORT).show()
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

    private fun guardarCambiosLista() {
        val stringGuardado = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        )

        if (stringGuardado != null) {
            try {
                // Obtener todas las listas
                val todasListas = Json.decodeFromString<List<ListaApp>>(stringGuardado)

                // Reemplazar la lista actualizada
                val listasActualizadas = todasListas.map { lista ->
                    if (lista.id == listaActual.id) {
                        listaActual
                    } else {
                        lista
                    }
                }

                // Guardar de nuevo
                val editor = sharedPreferences.edit()
                val listaString = Json.encodeToString(listasActualizadas)
                editor.putString(pantalla_menu.NOMBRE_DATO_LISTAS, listaString)
                editor.apply()

            } catch (e: Exception) {
                Toast.makeText(this, "Error al guardar cambios: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
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
            // Regresar al menu principal
            finish()
        }

        botonAlarma.setOnClickListener {
            val intent = Intent(this, NotificacionesActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        // Guardar cambios antes de regresar
        guardarCambiosLista()
        super.onBackPressed()
    }
}