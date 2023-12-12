package com.example.musicapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.models.Album
import com.example.musicapp.models.Artist
import com.google.firebase.firestore.DocumentReference
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AlbumAdapter (private val context: Context, private var list: ArrayList<Album>) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {


    class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        lateinit var imgAlbum : ImageView
        lateinit var nameAlbum : TextView
        lateinit var nameArtist : TextView

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
        Picasso.get().load(album.image).into(holder.imgAlbum);
    }


    public fun setData( l : ArrayList<Album>){
        this.list = l
    }


}