package com.example.musicapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.fragments.ArtistInformationFragment
import com.example.musicapp.fragments.ArtistsFragment
import com.example.musicapp.models.Artist

class ArtistAdapter(private val context: Context, private val artists: ArrayList<Artist>) :
    RecyclerView.Adapter<ArtistAdapter.ArtistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_artist, parent, false)
        return ArtistViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.tvNameArtist.text = artists[position].name
        holder.imgArtist.setImageDrawable(context.resources.getDrawable(R.drawable.alan_walker, null))
        holder.itemView.setOnClickListener{

        }
    }

    override fun getItemCount() = artists.size

    class ArtistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgArtist: ImageView
        val tvNameArtist: TextView

        init {
            imgArtist = itemView.findViewById(R.id.imgArtist)
            tvNameArtist = itemView.findViewById(R.id.tvNameArtist)
        }
    }

}