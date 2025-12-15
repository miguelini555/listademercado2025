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
            val card = crearCardLista(lista, index)
            val params = GridLayout.LayoutParams().apply {
                width = 0
                height = dpToPx(150)  // AQUI - IMPORTANTE: Altura fija
                columnSpec = GridLayout.spec(index % 2, 1f)
                rowSpec = GridLayout.spec(index / 2, 1f)
                setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4))  // AQUI - IMPORTANTE: Márgenes
            }
            card.layoutParams = params
            gridListas.addView(card)
        }
    }

    // AQUI - VERSIÓN COMPLETA Y FUNCIONAL
    private fun crearCardLista(lista: ListaApp, index: Int): LinearLayout {
        return LinearLayout(this).apply {
            // AQUI - IMPORTANTE: Padding interno
            setPadding(dpToPx(12), dpToPx(12), dpToPx(12), dpToPx(12))

            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER

            // AQUI - IMPORTANTE: Color según prioridad
            setBackgroundColor(
                when (lista.prioridad) {
                    "Alta" -> Color.parseColor("#FFCCCC")  // Rojo claro
                    "Media" -> Color.parseColor("#FFFFCC") // Amarillo claro
                    else -> Color.parseColor("#CCFFCC")    // Verde claro
                }
            )

            // AQUI - IMPORTANTE: Elevación para sombra
            elevation = 10f
            // Nombre
            addView(TextView(this@pantalla_menu).apply {
                text = lista.nombre
                textSize = 14f
                gravity = Gravity.CENTER
                setTextColor(Color.BLACK)
                maxLines = 2
            }
            )

            // Productos
            addView(TextView(this@pantalla_menu).apply {
                text = "${lista.productos.size} productos"
                textSize = 12f
                gravity = Gravity.CENTER
                setTextColor(Color.DKGRAY)
            })

            // Prioridad
            addView(TextView(this@pantalla_menu).apply {
                text = when (lista.prioridad) {
                    "Alta" -> "Alta"
                    "Media" -> "Media"
                    else -> "Baja"
                }
                textSize = 11f
                gravity = Gravity.CENTER
                setTextColor(Color.DKGRAY)
            })

            // Fecha límite
            if (lista.fechaLimite != null) {
                addView(TextView(this@pantalla_menu).apply {
                    text = "${lista.fechaLimite}"
                    textSize = 10f
                    gravity = Gravity.CENTER
                    setTextColor(Color.RED)
                })
            }

            // AQUI - IMPORTANTE: Click listener funcional
            setOnClickListener {
                val intent = Intent(this@pantalla_menu, VerListaActivity::class.java).apply {
                    putExtra(VerListaActivity.EXTRA_LISTA_ID, lista.id)
                }
                startActivity(intent)
            }
        }
    }

    // AQUI - IMPORTANTE: Función para convertir dp a px
    private fun dpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

    override fun onResume() {
        super.onResume()
        cargarListasEnGrid()
    }
}