package com.example.musicapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.fragments.PlaylistFragment
import com.example.musicapp.models.Playlist

class PlaylistAdapter(private val context: Context, private val playlists: ArrayList<Playlist>): RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_playlist, parent, false)
        return PlaylistViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.tvNamePlaylist.text = playlists[position].name
        holder.imgPlaylist.setImageDrawable(context.resources.getDrawable(R.drawable.alan_walker, null))
        holder.clickItem(context, playlists)
    }

    override fun getItemCount() = playlists.size

    class PlaylistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvNamePlaylist: TextView
        var imgPlaylist: ImageView

        init {
            tvNamePlaylist = itemView.findViewById(R.id.tvNamePlaylist)
            imgPlaylist = itemView.findViewById(R.id.imgAvatarPlaylist)
        }

        fun clickItem(context: Context, playlists: ArrayList<Playlist>){
            itemView.setOnClickListener{
                val mainActivity = context as MainActivity
                val playlistFragment = PlaylistFragment(playlists[adapterPosition]);
                mainActivity.loadFragment(playlistFragment, "body");
            }
        }

    }
}