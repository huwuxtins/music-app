package com.example.musicapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.models.Playlist

class NewPlaylistAdapter(private val context: Context, private val playlists: ArrayList<Playlist>): RecyclerView.Adapter<NewPlaylistAdapter.NewPlaylistViewHolder>() {

    private var onItemClickListener: OnItemClickListener? = null
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewPlaylistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.viewholder_new_playlist, parent, false)
        return NewPlaylistViewHolder(view)
    }
    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }

    override fun onBindViewHolder(holder: NewPlaylistViewHolder, position: Int) {
        holder.tvNamePlaylist.text = playlists[position].name
        holder.imgPlaylist.setImageDrawable(context.resources.getDrawable(R.drawable.image_playlist, null))
        holder.itemView.setOnClickListener{
            onItemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount() = playlists.size

    class NewPlaylistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvNamePlaylist: TextView
        var imgPlaylist: ImageView

        init {
            tvNamePlaylist = itemView.findViewById(R.id.tvNamePlaylist)
            imgPlaylist = itemView.findViewById(R.id.imgAvatarPlaylist)
        }
    }
}