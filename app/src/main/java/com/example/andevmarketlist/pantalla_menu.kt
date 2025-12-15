/*
package com.example.andevmarketlist

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class pantalla_menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_menu)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botonCalendario = findViewById<ImageButton>(R.id.botonCalendario)
        val botonInicio = findViewById<ImageButton>(R.id.botonInicio)
        val botonAlarma = findViewById<ImageButton>(R.id.botonAlarma)
        val botonAgregar = findViewById<ImageButton>(R.id.botonAgregar)

        botonCalendario.setOnClickListener {
            val intent = Intent(this, activity_Calendario::class.java)
            startActivity(intent)
        }

        botonInicio.setOnClickListener {
            val intent = Intent(this, pantalla_menu::class.java)
            startActivity(intent)
        }

        botonAgregar.setOnClickListener {
            val intent = Intent(this, PantallaListasActivity::class.java)
            startActivity(intent)
        }
    }
}

 */
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
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class pantalla_menu : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        val NOMBRE_FICHERO_SHARED_PREFERENCES = "MarketListApp"
        val NOMBRE_DATO_LISTAS = "DatoListas"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pantalla_menu)

        sharedPreferences = getSharedPreferences(
            NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        val botonCalendario = findViewById<ImageButton>(R.id.botonCalendario)
        val botonInicio = findViewById<ImageButton>(R.id.botonInicio)
        val botonAlarma = findViewById<ImageButton>(R.id.botonAlarma)
        val botonAgregar = findViewById<ImageButton>(R.id.botonAgregar)

        botonCalendario.setOnClickListener {
            val intent = Intent(this, activity_Calendario::class.java)
            startActivity(intent)
        }

        botonInicio.setOnClickListener {
            cargarListasEnGrid()
        }

        botonAgregar.setOnClickListener {
            val intent = Intent(this, PantallaListasActivity::class.java)
            startActivity(intent)
        }

        botonAlarma.setOnClickListener {
            val intent = Intent(this, NotificacionesActivity::class.java)
            startActivity(intent)
        }

        cargarListasEnGrid()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun obtenerTodasListas(): List<ListaApp> {
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

    private fun cargarListasEnGrid() {
        val gridListas = findViewById<GridLayout>(R.id.gridListas)
        gridListas.removeAllViews()

        val listas = obtenerTodasListas()
            .filter { !it.completada }
            .sortedByDescending { it.fechaCreacion }
        /*
        if (listas.isEmpty()) {
            val tvMensaje = TextView(this).apply {
                text = "No hay listas activas\n\nToca el botón + para crear una"
                textSize = 16f
                gravity = Gravity.CENTER
                setTextColor(Color.GRAY)

                layoutParams = GridLayout.LayoutParams().apply {
                    width = GridLayout.LayoutParams.MATCH_PARENT
                    height = dpToPx(200)
                    columnSpec = GridLayout.spec(0, 2, 1f)
                }
            }
            gridListas.addView(tvMensaje)
            return
        }
        */
        gridListas.columnCount = 2
        gridListas.rowCount = GridLayout.UNDEFINED

        listas.forEachIndexed { index, lista ->
            val card = crearCardLista(lista)
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 300 // AQUI - IMPORTANTE: Altura fija
                columnSpec = GridLayout.spec(index % 2, 1f)
                rowSpec = GridLayout.spec(index / 2, 1f)
                setMargins(4, 4, 4, 4)  // AQUI - IMPORTANTE: Márgenes
            }
            card.layoutParams = params
            gridListas.addView(card)
        }
    }

    // AQUI - VERSIÓN COMPLETA Y FUNCIONAL
    private fun crearCardLista(lista: ListaApp): LinearLayout {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.setPadding(16, 16, 16, 16)

        // Color según prioridad
        when (lista.prioridad) {
            "Alta" -> card.setBackgroundColor(Color.parseColor("#FFCCCC"))
            "Media" -> card.setBackgroundColor(Color.parseColor("#FFFFCC"))
            else -> card.setBackgroundColor(Color.parseColor("#CCFFCC"))
        }

        // Nombre de la lista
        val textViewNombre = TextView(this)
        textViewNombre.text = lista.nombre
        textViewNombre.textSize = 14f
        textViewNombre.gravity = Gravity.CENTER
        textViewNombre.setTextColor(Color.BLACK)
        card.addView(textViewNombre)

        // Productos (máximo 3)
        lista.productos.take(3).forEach { producto ->
            val tv = TextView(this)
            tv.text = "• $producto"
            tv.textSize = 12f
            card.addView(tv)
        }

        // Solo "..." si hay más de 3
        if (lista.productos.size > 3) {
            val tv = TextView(this)
            tv.text = "..."
            tv.textSize = 12f
            card.addView(tv)
        }

        // Click
        card.setOnClickListener {
            val intent = Intent(this, VerListaActivity::class.java)
            // Usar la constante de VerListaActivity
            intent.putExtra(VerListaActivity.EXTRA_LISTA_ID, lista.id)
            startActivity(intent)
        }

        return card
    }

    // AQUI - IMPORTANTE: Función para convertir dp a px
    // private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    override fun onResume() {
        super.onResume()
        cargarListasEnGrid()
    }
}