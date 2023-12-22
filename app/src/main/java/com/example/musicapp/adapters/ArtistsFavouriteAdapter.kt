package com.example.musicapp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.models.Artist
import com.example.musicapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ArtistsFavouriteAdapter(private var context : Context, private var list : ArrayList<DocumentReference> ) : RecyclerView.Adapter<ArtistsFavouriteAdapter.ArtistsFavouriteHolder>(){

    class ArtistsFavouriteHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val profile_image : CircleImageView
        val artist_name: TextView
        val img_delete : ImageButton


        init{
            profile_image = itemView.findViewById(R.id.profile_image)
            artist_name = itemView.findViewById(R.id.artist_name)
            img_delete = itemView.findViewById(R.id.img_delete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistsFavouriteHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_artist_favourite_view, parent, false)
        return ArtistsFavouriteAdapter.ArtistsFavouriteHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(l : ArrayList<DocumentReference>){
        this.list = l
    }

    private fun getIndex(s : ArrayList<String>, k : String) : Int{
        return s.indexOf(k)
    }

    override fun onBindViewHolder(holder: ArtistsFavouriteHolder, position: Int) {
        val doc = list[position]

        doc.addSnapshotListener{ value, error ->
            if(value != null){
                val art = value.toObject(Artist::class.java)
                Picasso.get().load(art?.avatar).into(holder.profile_image)
                holder.artist_name.text = art?.name
            }

        }

        holder.img_delete.setOnClickListener {
            val doc = list[position]
            doc.addSnapshotListener{ value, error ->
                if(value != null){
                    val art = value.toObject(Artist::class.java)
                    val idd = art?.id

                    val db = FirebaseFirestore.getInstance()
                    val fUser = FirebaseAuth.getInstance().currentUser
                    val email = fUser?.email.toString()

                    val builder = AlertDialog.Builder(context)
                    builder.setMessage("Are you sure you want to unfollow?")
                        .setCancelable(false)
                        .setPositiveButton("Yes") { dialog, id ->
                            db.collection("Users").document(email).get()
                                .addOnCompleteListener{task ->
                                    if(task.isSuccessful){
                                        val doc = task.result
                                        val user = doc.toObject(User::class.java)
                                        var l = user?.favouriteArtist
                                        if(l!=null){

                                            val index = getIndex(l, "Artists/"+idd)

                                            l?.removeAt(index)

                                            db.collection("Users").document(email).update("favouriteArtist",l)
                                                .addOnSuccessListener {
                                                    list.removeAt(index)
                                                    notifyItemRemoved(index)
                                                }
                                                .addOnFailureListener{

                                                }
                                        }
                                    }
                                }
                        }
                        .setNegativeButton("No") { dialog, id ->
                            dialog.dismiss()
                        }
                    val alert = builder.create()
                    alert.show()
                }
            }
        }
    }
}