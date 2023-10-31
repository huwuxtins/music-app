package com.example.musicapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.models.Song

class SongAdapter(private val context: Context, private val songs: ArrayList<Song>): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.tvNameSong.text = songs[position].name
        holder.tvNameArtists.text = songs[position].getArtists()
        holder.imgSong.setImageDrawable(context.resources.getDrawable(R.drawable.alan_walker, null))
    }

    override fun getItemCount() = songs.size
    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgSong: ImageView
        val tvNameSong: TextView
        val tvNameArtists: TextView

        init {
            imgSong = itemView.findViewById(R.id.imgSong)
            tvNameSong = itemView.findViewById(R.id.tvNameSong)
            tvNameArtists = itemView.findViewById(R.id.tvNameArtists)
        }
    }

}