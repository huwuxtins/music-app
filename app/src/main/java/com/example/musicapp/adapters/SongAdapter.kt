package com.example.musicapp.adapters

import android.content.Context
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.fragments.SongFragment
import com.example.musicapp.models.Song

class SongAdapter(private val context: Context, private val songs: ArrayList<Song>): RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_song, parent, false)
        return SongViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
//        holder.tvNameSong.text = songs[position].name
//        holder.tvNameArtists.text = songs[position].artist.name
//        holder.imgSong.setImageDrawable(context.resources.getDrawable(R.drawable.alan_walker, null))
//        holder.openTrack(context, songs)
//
//        holder.btnMenu.setOnClickListener{ view ->
//            val popupMenu = PopupMenu(context, view)
//            popupMenu.inflate(R.menu.menu_song) // Inflate your menu resource
//            popupMenu.show()
//
//            // Set click listeners for menu items (if needed)
//            popupMenu.setOnMenuItemClickListener {
//                // Handle menu item click here
//                when (it.itemId) {
//                    R.id.itDownload -> {
//                        Log.e("MyApp", "Downloading")
//                        true
//                    }
//                    R.id.itAddHeart -> {
//                        Log.e("MyApp", "Adding to favourite list")
//                        true
//                    }
//                    R.id.itAddPlaylist -> {
//                        Log.e("MyApp", "Adding to playlist")
//                        true
//                    }
//                    else -> false
//                }
//            }
//        }
    }

    override fun getItemCount() = songs.size
    class SongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnCreateContextMenuListener {
        val imgSong: ImageView
        val tvNameSong: TextView
        val tvNameArtists: TextView
        private val btnHeart: ImageButton
        private val btnPlay: ImageButton
        val btnMenu: ImageButton

        init {
            imgSong = itemView.findViewById(R.id.imgSong)
            tvNameSong = itemView.findViewById(R.id.tvNameSong)
            tvNameArtists = itemView.findViewById(R.id.tvNameArtists)
            btnHeart = itemView.findViewById(R.id.btnHeart)
            btnPlay = itemView.findViewById(R.id.btnPlay)
            btnMenu = itemView.findViewById(R.id.btnMenu)
        }

        fun openTrack(context: Context, songs: ArrayList<Song>){
            imgSong.setOnClickListener{
                val mainActivity = context as MainActivity
                val songFragment = SongFragment(songs[adapterPosition], songs);
                mainActivity.loadFragment(songFragment, "body");
            }
            tvNameSong.setOnClickListener{
                val mainActivity = context as MainActivity
                val songFragment = SongFragment(songs[adapterPosition], songs);
                mainActivity.loadFragment(songFragment, "body");
            }
        }

        override fun onCreateContextMenu(
            menu: ContextMenu?,
            v: View?,
            menuInfo: ContextMenu.ContextMenuInfo?
        ) {
            Log.e("MyApp", "Click on item")
        }
    }

}