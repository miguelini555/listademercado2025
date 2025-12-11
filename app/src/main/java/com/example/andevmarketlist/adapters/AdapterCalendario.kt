package com.example.andevmarketlist.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.andevmarketlist.R
import com.example.andevmarketlist.dataclases.DiaCalendario

class CalendarioAdapter(
    private val listaDias: List<DiaCalendario>
) : RecyclerView.Adapter<CalendarioAdapter.DiaViewHolder>() {

    inner class DiaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textFecha: TextView = itemView.findViewById(R.id.text_fecha)
        val contenedorEventos: LinearLayout = itemView.findViewById(R.id.contenedor_eventos)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.dia_calendario, parent, false)
        return DiaViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaViewHolder, position: Int) {
        val dia = listaDias[position]
        holder.textFecha.text = dia.fecha

        holder.contenedorEventos.removeAllViews()

        dia.eventos.forEach { textoEvento ->
            val tv = TextView(holder.itemView.context)
            tv.text = textoEvento
            tv.setPadding(12, 8, 12, 8)
            tv.setBackgroundColor(Color.WHITE)
            tv.setTextColor(Color.BLACK)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.topMargin = 4
            tv.layoutParams = params

            holder.contenedorEventos.addView(tv)
        }
    }

    override fun getItemCount(): Int = listaDias.size
}
