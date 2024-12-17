package com.example.catalogsvg

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface CatalogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(catalog: Catalog)
    @Update
    suspend fun update(currentItem: Catalog)
    @Delete
    suspend fun delete(currentItem: Catalog)
    @Query("SELECT * FROM catalog_db WHERE id = :id")
    fun get(id: Long): LiveData<Catalog>
    @Query("SELECT * FROM catalog_db")
    suspend fun getAll(): List<Catalog>
}