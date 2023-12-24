package com.example.musicapp.adapters

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
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
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.controllers.PlaylistController
import com.example.musicapp.fragments.SongFragment
import com.example.musicapp.models.Playlist
import com.example.musicapp.models.Song
import com.example.musicapp.services.PlayMusicService
import com.example.musicapp.utils.FileDownloadTask
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import java.time.LocalDate

class NewSongAdapter(private val context: Context, private var songs: ArrayList<Song>, private val isSongFragment: Boolean?, private val playlist: Playlist?) :  RecyclerView.Adapter<NewSongAdapter.NewSongViewHolder>()  {

    private var playlistController: PlaylistController = PlaylistController()

    lateinit var auth : FirebaseAuth
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

        fun openTrack(context: Context, songs: ArrayList<Song>, stackName: String){
            val song: Song = songs[adapterPosition]
            try {
                val mainActivity = context as MainActivity
                val songFragment = SongFragment(song, songs)

                val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val songId = sharedPreferences.getLong("song_id", -1)

                val intent = Intent(context, PlayMusicService::class.java)
                intent.putExtra("song", song)
                intent.putExtra("songs", songs)
                intent.action = "NEW_MUSIC_PLAY"

                if(songId == song.id){
                    intent.action = "MUSIC_CONTINUE"
                }

                imgSong.setOnClickListener{
                    mainActivity.loadFragment(songFragment, stackName)
                    mainActivity.startForegroundService(intent)
                }
                tvNameSong.setOnClickListener{
                    mainActivity.loadFragment(songFragment, stackName)
                    mainActivity.startForegroundService(intent)
                }

                btnPlay.setOnClickListener {
                    mainActivity.loadFragment(songFragment, stackName)
                    mainActivity.startForegroundService(intent)
                }
            }
            catch (e: Exception){
                Log.e("MyApp", "You're staying in other activity, error: $e")
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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onBindViewHolder(holder: NewSongViewHolder, position: Int) {
        auth = FirebaseAuth.getInstance()

        val fUser = auth.currentUser
        val email = fUser?.email.toString()

        val song = songs[position]
        holder.tvNameSong.text = song.name
        holder.tvNameArtists.text = song.artistName
        if(!song.isDownloaded){
            Picasso.get().load(song.image).into(holder.imgSong)
        }
        var stackName = "body"
        if(isSongFragment == true){
            stackName = "music"
        }
        holder.openTrack(context, songs, stackName)

        if(!song.isLoved) {
            holder.btnHeart.setImageResource(R.drawable.icon_heart)
        }
        else{
            holder.btnHeart.setImageResource(R.drawable.icon_heart_full)
        }

        holder.btnHeart.setOnClickListener{
            if(!song.isLoved) {
                holder.btnHeart.setImageResource(R.drawable.icon_heart_full)
                playlistController.updatePlaylist("add", song, "Lovely_$email", onComplete = {
                    Toast.makeText(context, "Adding to favorite playlist", Toast.LENGTH_LONG).show()
                }, onFail = {
                    Toast.makeText(context, "Can't add to favorite playlist", Toast.LENGTH_LONG).show()
                })
                song.isLoved = true
            }
            else{
                holder.btnHeart.setImageResource(R.drawable.icon_heart)
                playlistController.updatePlaylist("remove", song, "Lovely_$email", onComplete = {
                    Toast.makeText(context, "Removing from favorite playlist", Toast.LENGTH_LONG).show()
                }, onFail = {
                    Toast.makeText(context, "Can't remove from favorite playlist", Toast.LENGTH_LONG).show()
                })
                song.isLoved = false
            }
        }

        holder.btnMenu.setOnClickListener{ view ->
            val popupMenu = PopupMenu(context, view)
            popupMenu.inflate(R.menu.menu_song) // Inflate your menu resource
            if(song.isInPlaylist){
                val itemAddPlaylist = popupMenu.menu.findItem(R.id.itAddPlaylist)
                itemAddPlaylist.title = "Remove from this playlist"
            }
            popupMenu.show()

            // Set click listeners for menu items (if needed)
            popupMenu.setOnMenuItemClickListener { it ->
                // Handle menu item click here
                when (it.itemId) {
                    R.id.itDownload -> {
                        Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show()
                        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                        song.isDownloaded = true
                        sharedPreferences.edit()
                            .putString(
                                "downloaded_song_${song.id}",
                                "${song.id}_${song.name}_${song.type}_${song.lyric}_${song.postAt}_${song.artist}_${song.artistName}_${song.isLoved}_${song.isDownloaded}")
                            .apply()

                        val storage = FirebaseStorage.getInstance()
                        val storageRef: StorageReference = storage.reference.child(song.link)

                        context as MainActivity
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            when {ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.ACCESS_MEDIA_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED -> {
                                    // You can use the API that requires the permission.
                                    val fileDownloadTask = FileDownloadTask(song.id.toString(), "")
                                    fileDownloadTask.execute(uri.toString())
                                    Toast.makeText(context, "Downloaded", Toast.LENGTH_SHORT).show()
                                }
                                else -> {
                                }
                            }
                        }
                        true
                    }
                    R.id.itAddHeart -> {
                        holder.btnHeart.setImageResource(R.drawable.icon_heart_full)
                        playlistController.updatePlaylist("add", song, "Lovely_$email", onComplete = {
                            Toast.makeText(context, "Adding to favorite playlist", Toast.LENGTH_SHORT).show()
                        }, onFail = {
                            Toast.makeText(context, "Can't add to favorite playlist", Toast.LENGTH_SHORT).show()
                        })
                        true
                    }
                    R.id.itAddPlaylist -> {
                        if(song.isInPlaylist){
                            playlistController.updatePlaylist("remove", song, "${playlist?.name}_$email", onComplete = {
                                Toast.makeText(context, "Remove ${song.name} from this playlist", Toast.LENGTH_SHORT).show()
                                playlist?.songs?.remove(song)
                                notifyDataSetChanged()
                            }, onFail = {
                                Toast.makeText(context, "Can't remove ${song.name} from this playlist", Toast.LENGTH_SHORT).show()
                            })
                        }
                        else{
                            val alertDialogBuilder = AlertDialog.Builder(context)
                            alertDialogBuilder.setTitle("Add new playlist")
                            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_new_playlist, null)
                            val rcvPlaylists = dialogView.findViewById<RecyclerView>(R.id.rcv_playlists)

                            alertDialogBuilder.setView(dialogView)

                            alertDialogBuilder.setPositiveButton("OK") { dialog, _ ->
                                val name = (dialogView.findViewById<EditText>(R.id.idt_name)).text.toString()
                                // Handle positive button click
                                val playlist = Playlist(name, email, LocalDate.now().toString(), "")
                                playlistController.addPlaylist(playlist, onComplete = {
                                    Toast.makeText(context, "Add playlist successfully!", Toast.LENGTH_SHORT).show()
                                    playlistController.updatePlaylist("add", song, name+"_$email", onComplete = {
                                        Toast.makeText(context, "Adding song to playlist successfully!", Toast.LENGTH_SHORT).show()
                                    }, onFail = {
                                        Toast.makeText(context, "Adding song to playlist failed!", Toast.LENGTH_SHORT).show()
                                    })
                                }, onFail = {
                                    Toast.makeText(context, "Name's playlist was existed", Toast.LENGTH_SHORT).show()
                                })
                                dialog.dismiss()
                            }

                            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                                // Handle negative button click
                                dialog.dismiss()
                            }

                            val alertDialog = alertDialogBuilder.create()

                            playlistController.getAllPlaylist(email, onComplete = { arraylist ->
                                val playlistAdapter = NewPlaylistAdapter(view.context, arraylist)
                                rcvPlaylists.adapter = playlistAdapter
                                rcvPlaylists.layoutManager =
                                    LinearLayoutManager(
                                        view.context,
                                        LinearLayoutManager.VERTICAL,
                                        false
                                    )
                                playlistAdapter.setOnItemClickListener(object : NewPlaylistAdapter.OnItemClickListener {
                                    override fun onItemClick(position: Int) {
                                        val chosenPlaylist = arraylist[position]
                                        Toast.makeText(context, "Choose ${chosenPlaylist.name}", Toast.LENGTH_SHORT).show()
                                        playlistController.updatePlaylist("add", song, chosenPlaylist.name+"_$email", onComplete = {
                                            Toast.makeText(context, "Adding song to playlist successfully!", Toast.LENGTH_SHORT).show()
                                        }, onFail = {
                                            Toast.makeText(context, "Adding song to playlist failed!", Toast.LENGTH_SHORT).show()
                                        })
                                        alertDialog.dismiss()
                                    }
                                })
                            })
                            // Show the dialog
                            alertDialog.show()
                        }

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