package com.example.andevmarketlist.basededatos

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(ListaParaRoom::class), version = 1)
abstract class ListaDataBase : RoomDatabase() {
    abstract fun listaDao(): ListaDao
}
