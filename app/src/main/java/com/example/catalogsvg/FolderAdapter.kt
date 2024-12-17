package com.example.catalogsvg

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class FolderAdapter(val listOfFolderResponse: List<String>, val listener: FolderClick): RecyclerView.Adapter<FolderAdapter.FolderViewHolder>() {
    class FolderViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val button: Button = view.findViewById(R.id.btnNameFolder)
    }

    fun getAll(): List<String> {
        return listOfFolderResponse
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_folder, parent, false)
        return FolderViewHolder(layoutInflater)
    }

    override fun getItemCount(): Int = listOfFolderResponse.size

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        if(listOfFolderResponse.size > 3) holder.button.setTextSize(14F)
        holder.button.text = listOfFolderResponse[position]
        holder.button.setOnClickListener {
            listener.clickOnFolder(listOfFolderResponse[position])
        }
    }
}