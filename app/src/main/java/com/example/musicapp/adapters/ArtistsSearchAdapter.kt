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
import com.example.musicapp.models.Artist
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ArtistsSearchAdapter(private val context : Context, private var list : ArrayList<Artist>) : RecyclerView.Adapter<ArtistsSearchAdapter.ArtistSearchViewHolder>() {


    class ArtistSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var profile: CircleImageView
        lateinit var name : TextView

        init {
            profile = itemView.findViewById(R.id.profile_image)
            name = itemView.findViewById(R.id.artist_name)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistSearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.iten_search_artist, parent, false)
        return ArtistsSearchAdapter.ArtistSearchViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ArtistSearchViewHolder, position: Int) {
        val art = list[position]
        holder.name.text = art.name
        val link = art.avatar
        Picasso.get().load(link).into(holder.profile);

        holder.itemView.setOnClickListener {
            val activity = it!!.context as AppCompatActivity
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.frgMain, ArtistInformationFragment())
            transaction?.addToBackStack("null")
            transaction?.commit()
        }

    }

    public fun setData(l : ArrayList<Artist>){
        this.list = l
    }
}