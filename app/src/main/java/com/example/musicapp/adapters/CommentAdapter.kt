package com.example.musicapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.dialog.LoadingDialog
import com.example.musicapp.models.Comment
import com.example.musicapp.models.Song
import com.example.musicapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class CommentAdapter (private val context: Context, private var list : ArrayList<DocumentReference>, private var song: Song) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        lateinit var img : CircleImageView
        lateinit var usernameCmt: TextView
        lateinit var comment: TextView
        lateinit var dateCmt: TextView
        lateinit var img_delete : ImageButton

        init {
            img = itemView.findViewById(R.id.circleImageView)
            usernameCmt = itemView.findViewById(R.id.usernameCmt)
            comment = itemView.findViewById(R.id.comment)
            dateCmt = itemView.findViewById(R.id.dateCmt)
            img_delete = itemView.findViewById(R.id.img_delete)
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

        val fUser = FirebaseAuth.getInstance() .currentUser
        val email = fUser?.email.toString()

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

                            if(email != user.email){
                                holder.img_delete.visibility = View.GONE
                            }
                        }
                    }
                    else{
                        throw Error(error?.message ?: error.toString())
                    }
                }
            }

        }


        holder.img_delete.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setMessage("Are you sure you want to delete your comment ?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->

                     val cmt = list[position]
                     val listCmt = song.comments
                     listCmt?.removeAt(position)

                     val db = FirebaseFirestore.getInstance()
                     val songRef = db.collection("Songs").document(song.id.toString())

                     songRef.update("comments", listCmt)
                         .addOnSuccessListener {
                             docCmt.addSnapshotListener{ value, error ->
                                 if(value != null){
                                     val cmt = value.toObject(Comment::class.java)
                                     if (cmt != null) {
                                         db.collection("Comments").document(cmt.id)
                                             .delete()
                                             .addOnSuccessListener {
                                                 list.removeAt(position)
                                                 notifyItemRemoved(position)
                                                 Toast.makeText(context,"Your comment has been deleted", Toast.LENGTH_SHORT).show()
                                             }
                                     }
                                 }
                             }
                         }
                         .addOnFailureListener{
                             Toast.makeText(context,"Error system", Toast.LENGTH_SHORT).show()
                         }
                }

                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }




    }

    fun setData(l : ArrayList<DocumentReference>){
        this.list = l
    }
}