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

        val botonNotificaciones = findViewById<ImageButton>(R.id.botonNotificaciones)

        botonNotificaciones.setOnClickListener {
            val intent = Intent(this, NotificacionesActivity::class.java)
            startActivity(intent)
        }

        botonCalendario.setOnClickListener {
            val intent = Intent(this, activity_Calendario::class.java)
            startActivity(intent)
        }

        botonAgregar.setOnClickListener {
            val intent = Intent(this, PantallaListasActivity::class.java)
            startActivity(intent)
        }

        botonAlarma.setOnClickListener {
            val intent = Intent(this, HistorialActivity::class.java)
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

        gridListas.columnCount = 2
        gridListas.rowCount = GridLayout.UNDEFINED

        listas.forEachIndexed { index, lista ->
            val card = crearCardLista(lista)
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = 300
                columnSpec = GridLayout.spec(index % 2, 1f)
                rowSpec = GridLayout.spec(index / 2, 1f)
                setMargins(4, 4, 4, 4)
            }
            card.layoutParams = params
            gridListas.addView(card)
        }
    }

    private fun crearCardLista(lista: ListaApp): LinearLayout {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.VERTICAL
        card.gravity = Gravity.CENTER
        card.setPadding(16, 16, 16, 16)

        when (lista.prioridad) {
            "Alta" -> card.setBackgroundColor(Color.parseColor("#FFCCCC"))
            "Media" -> card.setBackgroundColor(Color.parseColor("#FFFFCC"))
            else -> card.setBackgroundColor(Color.parseColor("#CCFFCC"))
        }

        val textViewNombre = TextView(this)
        textViewNombre.text = lista.nombre
        textViewNombre.textSize = 14f
        textViewNombre.gravity = Gravity.CENTER
        textViewNombre.setTextColor(Color.BLACK)
        card.addView(textViewNombre)

        lista.productos.take(3).forEach { producto ->
            val tv = TextView(this)
            tv.text = "• $producto"
            tv.textSize = 12f
            card.addView(tv)
        }

        if (lista.productos.size > 3) {
            val tv = TextView(this)
            tv.text = "..."
            tv.textSize = 12f
            card.addView(tv)
        }

        card.setOnClickListener {
            val intent = Intent(this, VerListaActivity::class.java)
            intent.putExtra(VerListaActivity.EXTRA_LISTA_ID, lista.id)
            startActivity(intent)
        }

        return card
    }

    override fun onResume() {
        super.onResume()
        cargarListasEnGrid()
    }
}