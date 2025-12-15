package com.example.andevmarketlist.dataclases

import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Serializable
data class ListaApp(
    val id: String = UUID.randomUUID().toString(),
    val nombre: String,
    val fechaLimite: String? = null,
    val prioridad: String = "Media",
    val productos: List<String> = emptyList(),
    val fechaCreacion: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(
        Date()
    ),
    val completada: Boolean = false
)

