package com.example.catalogsvg

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class CatalogViewModel: ViewModel() {
    var listOfFiles = mutableListOf<String>()
    fun putFileInRoom(dao: CatalogDao, currentItem: Catalog) {
        viewModelScope.launch {
            dao.insert(currentItem)
        }
    }
    suspend fun getAll(dao: CatalogDao): List<Catalog> {
        return dao.getAll()
    }
}