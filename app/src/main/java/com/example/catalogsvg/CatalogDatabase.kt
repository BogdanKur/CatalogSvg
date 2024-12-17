package com.example.catalogsvg

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Catalog::class], version = 1, exportSchema = false)
abstract class CatalogDatabase: RoomDatabase() {
    abstract val dao: CatalogDao

    companion object {
        @Volatile
        private var INSTANCE: CatalogDatabase? = null
        fun getInstance(context: Context): CatalogDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if(instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CatalogDatabase::class.java,
                        "catalog_db"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}