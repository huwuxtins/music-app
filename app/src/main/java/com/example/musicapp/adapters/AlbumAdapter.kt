package com.example.musicapp.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.fragments.AlbumFragment
import com.example.musicapp.models.Album
import com.example.musicapp.models.Artist
import com.google.firebase.firestore.DocumentReference
import com.squareup.picasso.Picasso

class AlbumAdapter (private val context: Context, private var list: ArrayList<Album>) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {


    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var imgAlbum : ImageView
        var nameAlbum : TextView
        var nameArtist : TextView

        init {
            imgAlbum = itemView.findViewById(R.id.imgAlbum)
            nameAlbum = itemView.findViewById(R.id.txt_nameAlbum)
            nameArtist = itemView.findViewById(R.id.txt_artistAlbum)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_album_view, parent, false)
        return AlbumAdapter.AlbumViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = list[position]
        holder.nameAlbum.text = album.name
        val docRef : DocumentReference?= album.artist
        docRef?.addSnapshotListener { value, error ->
            if (value!=null){
                val artist = value.toObject(Artist::class.java)
                holder.nameArtist.text = artist?.name +" - " + album.postYear
            }else{
                throw Error(error?.message ?: error.toString())
            }
        }
        holder.itemView.setOnClickListener{
            val mainActivity = context as MainActivity
            mainActivity.loadFragment(AlbumFragment(album), "body")
        }

        try{
            Picasso.get().load(album.image).into(holder.imgAlbum)
        }
        catch (e: Exception){
            Log.e("MyApp", "You're offline, error: $e")
        }
    }


    fun setData( l : ArrayList<Album>){
        this.list = l
    }


}