package com.example.musicapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.models.Comment
import com.example.musicapp.models.User
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.toObject
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter (private val context: Context, private var list : ArrayList<DocumentReference>) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        lateinit var img : CircleImageView
        lateinit var usernameCmt: TextView
        lateinit var comment: TextView
        lateinit var dateCmt: TextView

        init {
            img = itemView.findViewById(R.id.circleImageView)
            usernameCmt = itemView.findViewById(R.id.usernameCmt)
            comment = itemView.findViewById(R.id.comment)
            dateCmt = itemView.findViewById(R.id.dateCmt)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment_view, parent, false)
        return CommentAdapter.CommentViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {

        val docCmt = list[position]

        docCmt.addSnapshotListener{value, error ->
            if(value != null){
                val cmt = value.toObject(Comment::class.java)
                holder.comment.text = cmt?.content
                holder.dateCmt.text = cmt?.createAt
                val docRef : DocumentReference?= cmt?.user
                docRef?.addSnapshotListener { value, error ->
                    if(value != null){
                        val user = value.toObject(User::class.java)
                        if (user != null) {
                            holder.usernameCmt.text = user.username
                            val linkAvatar = user.avatar
                            if(linkAvatar.contains("https://")){
                                Picasso.get().load(linkAvatar).into(holder.img)
                            }
                        }
                    }
                    else{
                        throw Error(error?.message ?: error.toString())
                    }
                }
            }

        }
    }

    fun setData(l : ArrayList<DocumentReference>){
        this.list = l
    }
}