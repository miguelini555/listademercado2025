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