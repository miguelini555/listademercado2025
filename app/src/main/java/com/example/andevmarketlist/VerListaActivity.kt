package com.example.andevmarketlist

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.dataclases.ListaApp

class VerListaActivity : AppCompatActivity() {

    companion object {
        // AQUI - NUEVO: Constante para pasar datos entre activities
        const val EXTRA_LISTA_ID = "lista_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ver_lista)

        // Obtener ID de la lista desde el Intent
        val listaId = intent.getStringExtra(EXTRA_LISTA_ID)

        if (listaId == null) {
            Toast.makeText(this, "Error: Lista no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // AQUI - MODIFICACION: Buscar la lista por ID
        val lista = buscarListaPorId(listaId)

        if (lista == null) {
            Toast.makeText(this, "Lista no encontrada", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // ========== CONFIGURAR INTERFAZ ==========
        configurarInterfaz(lista)

        // ========== BOTONES DE NAVEGACIÓN ==========
        configurarBotonesNavegacion()

        // ========== WINDOW INSETS ==========
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    // AQUI - NUEVO: Buscar lista por ID
    private fun buscarListaPorId(id: String): ListaApp? {
        // Por ahora buscamos en SharedPreferences
        // Más adelante podríamos usar Room
        val sharedPreferences = getSharedPreferences(
            pantalla_menu.NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        val stringGuardado = sharedPreferences.getString(
            pantalla_menu.NOMBRE_DATO_LISTAS,
            "[]"
        )

        return if (stringGuardado != null) {
            try {
                val listas = kotlinx.serialization.json.Json.decodeFromString<List<ListaApp>>(stringGuardado)
                listas.find { it.id == id }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    // AQUI - NUEVO: Configurar interfaz con datos de la lista
    private fun configurarInterfaz(lista: ListaApp) {
        // Configurar título
        val textViewTitulo = findViewById<TextView>(R.id.textViewTituloLista)
        textViewTitulo.text = lista.nombre

        // Configurar fecha límite
        val textViewFechaLimite = findViewById<TextView>(R.id.textViewFechaLimite)
        if (lista.fechaLimite != null) {
            textViewFechaLimite.text = "Fecha límite: ${lista.fechaLimite}"
        } else {
            textViewFechaLimite.text = "Sin fecha límite"
        }

        // Configurar prioridad
        val textViewPrioridad = findViewById<TextView>(R.id.textViewPrioridad)
        textViewPrioridad.text = when (lista.prioridad) {
            "Alta" -> "Prioridad: Alta"
            "Media" -> "Prioridad: Media"
            else -> "Prioridad: Baja"
        }

        // Configurar productos
        val linearLayoutProductos = findViewById<LinearLayout>(R.id.linearLayoutProductos)
        linearLayoutProductos.removeAllViews()

        if (lista.productos.isEmpty()) {
            val tvVacio = TextView(this).apply {
                text = "No hay productos en esta lista"
                textSize = 16f
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)
                setPadding(0, dpToPx(20), 0, 0)
            }
            linearLayoutProductos.addView(tvVacio)
        } else {
            lista.productos.forEachIndexed { index, producto ->
                val tvProducto = TextView(this).apply {
                    text = "${index + 1}. $producto"
                    textSize = 18f
                    setPadding(dpToPx(16), dpToPx(8), dpToPx(16), dpToPx(8))
                    setTextColor(Color.BLACK)

                    // Alternar colores para mejor legibilidad
                    if (index % 2 == 0) {
                        setBackgroundColor(Color.parseColor("#F5F5F5"))
                    } else {
                        setBackgroundColor(Color.WHITE)
                    }
                }
                linearLayoutProductos.addView(tvProducto)
            }
        }

        // Configurar fecha de creación
        val textViewFechaCreacion = findViewById<TextView>(R.id.textViewFechaCreacion)
        textViewFechaCreacion.text = "Creada: ${lista.fechaCreacion}"

        // Configurar estado (completada o no)
        val textViewEstado = findViewById<TextView>(R.id.textViewEstado)
        if (lista.completada) {
            textViewEstado.text = "COMPLETADA"
            textViewEstado.setTextColor(Color.GREEN)
        } else {
            textViewEstado.text = "PENDIENTE"
            textViewEstado.setTextColor(Color.RED)
        }
    }

    // AQUI - NUEVO: Configurar botones de navegación
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
            val intent = Intent(this, NotificacionesActivity::class.java)
            startActivity(intent)
        }
    }

    // AQUI - NUEVO: Convertir dp a px
    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()
}