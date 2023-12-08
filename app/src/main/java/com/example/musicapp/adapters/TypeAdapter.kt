package com.example.musicapp.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Type

class TypeAdapter(private val context: Context, private val list: ArrayList<Type>) : RecyclerView.Adapter<TypeAdapter.TypeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TypeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_kind_view, parent, false)
        return TypeAdapter.TypeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: TypeViewHolder, position: Int) {

        val type = list[position]

        holder.btn.text = type.name

        holder.btn.setBackgroundColor(Color.parseColor(type.color))
    }


    class TypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var btn : Button
        init {
            btn = itemView.findViewById(R.id.buttonKind)
        }
    }


}
