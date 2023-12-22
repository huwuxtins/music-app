package com.example.musicapp.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.adapters.SingerAdapter
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Song
import com.example.musicapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView


class ArtistInformationFragment : Fragment() {


    private lateinit var img_back : ImageView
    private lateinit var img_artist : CircleImageView
    private lateinit var txt_nameArtist : TextView
    private lateinit var txt_followers : TextView
    private lateinit var btn_follow : Button
    private lateinit var nameArtist : TextView
    private lateinit var bodArtist : TextView
    private lateinit var nationArtist : TextView
    private lateinit var storyArtist : TextView
    private lateinit var listSongArtist : RecyclerView
    private lateinit var db : FirebaseFirestore
    private lateinit var auth : FirebaseAuth

    private lateinit var art : Artist

    private lateinit var listSong : ArrayList<Song>
    private lateinit var adapterSong : NewSongAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_artist_information, container, false)

        img_back = view.findViewById(R.id.img_back)
        img_artist = view.findViewById(R.id.image_artist)
        txt_nameArtist = view.findViewById(R.id.txt_nameArtist)
        txt_followers = view.findViewById(R.id.txt_followers)
        btn_follow = view.findViewById(R.id.btn_follow)
        nameArtist = view.findViewById(R.id.nameArtist)
        bodArtist = view.findViewById(R.id.bodArtist)
        nationArtist = view.findViewById(R.id.nationArtist)
        storyArtist = view.findViewById(R.id.storyArtist)
        listSongArtist = view.findViewById(R.id.listSongArtist)
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        art = (requireArguments().getSerializable("artist") as Artist?)!!
        listSong = ArrayList()


        adapterSong = activity?.let { view?.let { it1 -> NewSongAdapter(it1.context,listSong,false,null) } }!!
        listSongArtist.adapter = adapterSong
        listSongArtist!!.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)

        showData()

        setFollow()


        img_back.setOnClickListener{
            onBackPressed()
        }

        btn_follow.setOnClickListener{


            val fUser = auth.currentUser
            val email : String = fUser?.email.toString()
            val status : String = btn_follow.text.toString()

            if(status == "Follow"){ //add follow
                val id = art.id.toInt()
                 db.collection("Users").document(email).get()
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            val doc = task.result
                            val user = doc.toObject(User::class.java)
                             var l = user?.favouriteArtist
                            if(l!=null && l.size != 0){
                                l?.add("Artists/" + id)
                                db.collection("Users").document(email).update("favouriteArtist",l)
                                    .addOnSuccessListener {
                                        btn_follow.text = "Unfollow"
                                        btn_follow.setBackgroundColor(resources.getColor(R.color.selected))
                                        updateFollow(id,"add",art.followers)
                                    }
                                    .addOnFailureListener{

                                    }
                            }
                            else{  //chưa có favouriteArtists
                                 l = ArrayList<String>()
                                 l.add("Artists/" + id)
                                 user?.favouriteArtist = l
                                if (user != null) {
                                    db.collection("Users").document(email).set(user)
                                        .addOnSuccessListener {
                                            btn_follow.text = "Unfollow"
                                            btn_follow.setBackgroundColor(resources.getColor(R.color.selected))
                                            updateFollow(id,"add",art.followers)
                                        }

                                        .addOnFailureListener {  }
                                }
                            }

                        }

                    }

            }
            else{ //unfollow
                val idd = art.id.toInt()
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
                                    if(l!=null && l!!.size != 0){
                                        l?.removeAt(getIndex(l!!, "Artists/" + idd))
                                        db.collection("Users").document(email).update("favouriteArtist",l)
                                            .addOnSuccessListener {
                                                btn_follow.text = "Follow"
                                                btn_follow.setBackgroundColor(resources.getColor(R.color.white))
                                                updateFollow(idd,"delete",art.followers)
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

        return view
    }

    fun getIndex(s : ArrayList<String>, k : String) : Int{
        return s.indexOf(k)
    }

    fun updateFollow(id: Int, key: String, old : Int){
            if(key == "add"){
                db.collection("Artists").document(id.toString())
                    .update("followers",old+1)
                    .addOnSuccessListener {
                        var new : Int = old + 1
                        art.followers = old + 1
                        txt_followers.text = new.toString() + " followers"

                    }
                    .addOnFailureListener {  }
            }
        else{
                db.collection("Artists").document(id.toString())
                    .update("followers",old - 1)
                    .addOnSuccessListener {
                        var new : Int = old - 1
                        art.followers = old - 1
                        txt_followers.text = new.toString() + " followers"

                    }
                    .addOnFailureListener {  }
            }
    }



    fun showData(){
        Picasso.get().load(art.avatar).into(img_artist)
        txt_nameArtist.text = art.name
        txt_followers.text = art.followers.toString() + " followers"
        nameArtist.text = art.realName
        bodArtist.text = art.bod
        nationArtist.text = art.nationality
        storyArtist.text = art.description

        val idArt = art.id

        val docRef = db.collection("Songs")

        docRef.get()
            .addOnSuccessListener {result ->
                for(document in result){
                    if(document.exists()){
                        val s = document.toObject(Song::class.java)

                        val songArt : String =  s.artist.toString()

                        val idA : Int= songArt.split("/")[2].toInt()

                        if(idArt.toInt() == idA){
                            listSong.add(s)
                        }
                    }
                }
                adapterSong.setData(listSong)
                adapterSong.notifyDataSetChanged()
            }

    }

    fun onBackPressed() {
        if (parentFragmentManager.backStackEntryCount > 0) {
            parentFragmentManager.popBackStack()
        }
    }

    fun setFollow(){
        val idArt = art.id

        val fUser = auth.currentUser
        val email : String = fUser?.email.toString()
        val docRef = db.collection("Users").document(email).get()
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    val doc = task.result
                    val user = doc.toObject(User::class.java)
                    val listArtUser : ArrayList<String>? = user?.favouriteArtist
                    var check = false
                    if( listArtUser!=null && listArtUser?.size != 0 ){
                        for(s in listArtUser){
                            val id = s.split("/")[1].toInt()
                            if(idArt.toInt() == id){
                                check = true                            }
                        }
                        if(check){
                            btn_follow.text = "Unfollow"
                             btn_follow.setBackgroundColor(resources.getColor(R.color.selected))
                        }
                    }


                }

            }
    }

}