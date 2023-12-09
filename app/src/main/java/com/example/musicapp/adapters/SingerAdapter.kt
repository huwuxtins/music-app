package com.example.musicapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Type
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class SingerAdapter(private val context: Context, private var list: ArrayList<Artist>) :  RecyclerView.Adapter<SingerAdapter.SingerViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artist_view, parent, false)
        return SingerAdapter.SingerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: SingerViewHolder, position: Int) {  // hien thi anh va ten cac nghe si
        if(position <= 7){
            val art = list[position]
            holder.name.text = art.name
            val link = art.avatar
            Picasso.get().load(link).into(holder.profile);
        }
    }


    public fun setData(list : ArrayList<Artist>){
        this.list = list
    }

    class SingerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var profile: CircleImageView
        lateinit var name : TextView
        init {
            profile = itemView.findViewById(R.id.profile_image)
            name = itemView.findViewById(R.id.artist_name)
        }
    }


}