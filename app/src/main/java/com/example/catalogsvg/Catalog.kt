package com.example.catalogsvg

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity("catalog_db", indices = [Index(value = ["name"], unique = true)])
data class Catalog(
    @PrimaryKey(true)
    var id: Long = 0L,
    @ColumnInfo("name")
    var name: String,
    @ColumnInfo("path")
    var path: String
)
