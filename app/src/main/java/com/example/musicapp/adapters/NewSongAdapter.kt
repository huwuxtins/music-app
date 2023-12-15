package com.example.musicapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.controllers.PlaylistController
import com.example.musicapp.fragments.SongFragment
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Playlist
import com.example.musicapp.models.Song
import com.example.musicapp.services.PlayMusicService
import com.google.firebase.firestore.DocumentReference
import com.squareup.picasso.Picasso
import java.time.LocalDate

class NewSongAdapter(private val context: Context, private var songs: ArrayList<Song>) :  RecyclerView.Adapter<NewSongAdapter.NewSongViewHolder>()  {

    private var playlistController: PlaylistController = PlaylistController()
    class NewSongViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),  View.OnCreateContextMenuListener  {
        val imgSong: ImageView
        val tvNameSong: TextView
        val tvNameArtists: TextView
        val btnHeart: ImageButton
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
            val song: Song = songs[adapterPosition]
            imgSong.setOnClickListener{
                val mainActivity = context as MainActivity

                val songFragment = SongFragment(song, songs)
                mainActivity.loadFragment(songFragment, "body")

                val intent = Intent(mainActivity, PlayMusicService::class.java)
                intent.putExtra("song", song)
                intent.action = "NEW_MUSIC_PLAY"
                mainActivity.startForegroundService(intent)
            }
            tvNameSong.setOnClickListener{
                val mainActivity = context as MainActivity
                val songFragment = SongFragment(song, songs)
                mainActivity.loadFragment(songFragment, "body")

                val intent = Intent(mainActivity, PlayMusicService::class.java)
                intent.putExtra("song", song)
                intent.action = "NEW_MUSIC_PLAY"
                mainActivity.startForegroundService(intent)
            }

            btnPlay.setOnClickListener {
                val mainActivity = context as MainActivity
                val songFragment = SongFragment(song, songs)
                mainActivity.loadFragment(songFragment, "body")

                val intent = Intent(mainActivity, PlayMusicService::class.java)
                intent.putExtra("song", song)
                intent.action = "NEW_MUSIC_PLAY"
                mainActivity.startForegroundService(intent)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewSongViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_song, parent, false)
        return NewSongViewHolder(view)
    }

    override fun getItemCount(): Int {
        return songs.size
    }

    override fun onBindViewHolder(holder: NewSongViewHolder, position: Int) {
        val song = songs[position]
        holder.tvNameSong.text = song.name
        val docRef : DocumentReference ?= song.artists

        docRef?.addSnapshotListener { value, error ->
            if (value!=null){
                val artist = value.toObject(Artist::class.java)
                holder.tvNameArtists.text = artist?.name
            }else{
                throw Error(error?.message ?: error.toString())
            }
        }
        Picasso.get().load(song.image).into(holder.imgSong)
        holder.openTrack(context, songs)

        if(!song.isLoved) {
            holder.btnHeart.setImageResource(R.drawable.icon_heart)
        }
        else{
            holder.btnHeart.setImageResource(R.drawable.icon_heart_full)
        }

        holder.btnHeart.setOnClickListener{
            if(!song.isLoved) {
                holder.btnHeart.setImageResource(R.drawable.icon_heart_full)
                playlistController.updatePlaylist("add", song, "Lovely", onComplete = {
                    Toast.makeText(context, "Adding to favorite playlist", Toast.LENGTH_LONG).show()
                }, onFail = {
                    Toast.makeText(context, "Can't add to favorite playlist", Toast.LENGTH_LONG).show()
                })
            }
            else{
                holder.btnHeart.setImageResource(R.drawable.icon_heart)
                playlistController.updatePlaylist("remove", song, "Lovely", onComplete = {
                    Toast.makeText(context, "Removing from favorite playlist", Toast.LENGTH_LONG).show()
                }, onFail = {
                    Toast.makeText(context, "Can't remove from favorite playlist", Toast.LENGTH_LONG).show()
                })
            }
        }

        holder.btnMenu.setOnClickListener{ view ->
            val popupMenu = PopupMenu(context, view)
            popupMenu.inflate(R.menu.menu_song) // Inflate your menu resource
            popupMenu.show()

            // Set click listeners for menu items (if needed)
            popupMenu.setOnMenuItemClickListener {
                // Handle menu item click here
                when (it.itemId) {
                    R.id.itDownload -> {
                        Log.e("MyApp", "Downloading")
                        true
                    }
                    R.id.itAddHeart -> {
                        holder.btnHeart.setImageResource(R.drawable.icon_heart_full)
                        playlistController.updatePlaylist("add", song, "Lovely", onComplete = {
                            Toast.makeText(context, "Adding to favorite playlist", Toast.LENGTH_LONG).show()
                        }, onFail = {
                            Toast.makeText(context, "Can't add to favorite playlist", Toast.LENGTH_LONG).show()
                        })
                        true
                    }
                    R.id.itAddPlaylist -> {
                        val alertDialogBuilder = AlertDialog.Builder(context)
                        alertDialogBuilder.setTitle("Add new playlist")
                        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_playlist, null)
                        alertDialogBuilder.setView(dialogView)

                        alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                            val name = (dialogView.findViewById<EditText>(R.id.idt_name)).text.toString()
                            // Handle positive button click
                            val playlist = Playlist(name, "", LocalDate.now().toString(), "")
                            playlistController.addPlaylist(playlist, onComplete = {
                                Toast.makeText(context, "Add playlist successfully!", Toast.LENGTH_LONG).show()
                            }, onFail = {
                                Toast.makeText(context, "Add playlist failed!", Toast.LENGTH_LONG).show()
                            })
                            dialog.dismiss()
                        }

                        alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                            // Handle negative button click
                            dialog.dismiss()
                        }

                        val alertDialog = alertDialogBuilder.create()

                        // Show the dialog
                        alertDialog.show()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    fun setData(list : ArrayList<Song>){
        this.songs = list
    }

}