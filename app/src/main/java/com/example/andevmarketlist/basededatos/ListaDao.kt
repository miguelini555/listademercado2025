package com.example.andevmarketlist.basededatos

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ListaDao {

    @Query("SELECT * FROM lista")
    fun getAll(): List<ListaParaRoom>

    @Insert
    fun insertAll(vararg listaParaRoom: ListaParaRoom)

    @Delete
    fun delete(listaParaRoom: ListaParaRoom)
}
