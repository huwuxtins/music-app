package com.example.musicapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.adapters.SingerAdapter
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Song
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


        img_back.setOnClickListener{
            onBackPressed()
        }

        return view
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

}