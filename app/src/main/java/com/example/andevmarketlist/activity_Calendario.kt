package com.example.andevmarketlist

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.andevmarketlist.adapters.CalendarioAdapter
import com.example.andevmarketlist.dataclases.DiaCalendario
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class activity_Calendario : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_calendario)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerCalendario = findViewById<RecyclerView>(R.id.recycler_calendario)
        recyclerCalendario.layoutManager = LinearLayoutManager(this)

        val formato = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale("es", "ES"))
        val hoy = LocalDate.now()

        val listaDias = mutableListOf<DiaCalendario>()

        for (i in 0..5) {
            val fechaTexto = hoy.plusDays(i.toLong()).format(formato)

            val eventos = when (i) {
                0 -> listOf("Plazo vence en unas horas")
                1 -> listOf("Plazo vence en un día")
                2 -> listOf("Sin eventos")
                else -> listOf("Sin eventos")
            }

            listaDias.add(DiaCalendario(fecha = fechaTexto, eventos = eventos))
        }

        val adapter = CalendarioAdapter(listaDias)
        recyclerCalendario.adapter = adapter

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
            //val intent = Intent(this, AlarmaActivity::class.java)
            //startActivity(intent)
        }
    }
}