package com.example.andevmarketlist.basededatos

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lista")
data class ListaParaRoom(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "unTextoColumna") val unTextoColumna: String,
    @ColumnInfo(name = "unNumeroColumna") val unNumeroColumna: Int,
    @ColumnInfo(name = "unBooleanColumna") val unBooleanColumna: Boolean
)

