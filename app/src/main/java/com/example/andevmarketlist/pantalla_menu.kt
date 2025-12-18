package com.example.andevmarketlist

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.andevmarketlist.databinding.ActivityPantallaMenuBinding
import com.example.andevmarketlist.dataclases.ListaApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class pantalla_menu : AppCompatActivity() {

    private lateinit var binding: ActivityPantallaMenuBinding
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        const val NOMBRE_FICHERO_SHARED_PREFERENCES = "MarketListApp"
        const val NOMBRE_DATO_LISTAS = "DatoListas"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPantallaMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(
            NOMBRE_FICHERO_SHARED_PREFERENCES,
            MODE_PRIVATE
        )

        configurarMenuPopup()
        configurarBotones()
        cargarListasEnGrid()

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

    private fun configurarMenuPopup() {
        binding.btnMenu.setOnClickListener {
            val popupMenu = PopupMenu(this, binding.btnMenu)
            popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.menu_mapa -> {
                        startActivity(Intent(this, MapaActivity::class.java))
                        true
                    }

                    R.id.menu_logout -> {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        true
                    }

                    else -> false
                }
            }

            popupMenu.show()
        }
    }

    private fun configurarBotones() {

        binding.botonNotificaciones.setOnClickListener {
            startActivity(Intent(this, NotificacionesActivity::class.java))
        }

        binding.botonCalendario.setOnClickListener {
            startActivity(Intent(this, activity_Calendario::class.java))
        }

        binding.botonAgregar.setOnClickListener {
            startActivity(Intent(this, PantallaListasActivity::class.java))
        }

        binding.botonAlarma.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        binding.botonInicio.setOnClickListener {
            cargarListasEnGrid()
        }
    }

    private fun obtenerTodasListas(): List<ListaApp> {
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

    private fun cargarListasEnGrid() {
        val grid = binding.gridListas
        grid.removeAllViews()

        val listas = obtenerTodasListas()
            .filter { !it.completada }
            .sortedByDescending { it.fechaCreacion }

        grid.columnCount = 2
        grid.rowCount = GridLayout.UNDEFINED

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
            grid.addView(card)
        }
    }

    private fun crearCardLista(lista: ListaApp): LinearLayout {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(16, 16, 16, 16)

            when (lista.prioridad) {
                "Alta" -> setBackgroundColor(Color.parseColor("#FFCCCC"))
                "Media" -> setBackgroundColor(Color.parseColor("#FFFFCC"))
                else -> setBackgroundColor(Color.parseColor("#CCFFCC"))
            }
        }

        card.addView(TextView(this).apply {
            text = lista.nombre
            textSize = 14f
            gravity = Gravity.CENTER
            setTextColor(Color.BLACK)
        })

        lista.productos.take(3).forEach { producto ->
            card.addView(TextView(this).apply {
                text = "• $producto"
                textSize = 12f
            })
        }

        if (lista.productos.size > 3) {
            card.addView(TextView(this).apply {
                text = "..."
                textSize = 12f
            })
        }

        card.setOnClickListener {
            startActivity(
                Intent(this, VerListaActivity::class.java)
                    .putExtra(VerListaActivity.EXTRA_LISTA_ID, lista.id)
            )
        }

        return card
    }

    override fun onResume() {
        super.onResume()
        cargarListasEnGrid()
    }
}
