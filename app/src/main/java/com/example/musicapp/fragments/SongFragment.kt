package com.example.musicapp.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.adapters.CommentAdapter
import com.example.musicapp.adapters.NewPlaylistAdapter
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.adapters.TrackViewPagerAdapter
import com.example.musicapp.controllers.PlaylistController
import com.example.musicapp.dialog.LoadingDialog
import com.example.musicapp.models.Artist
import com.example.musicapp.models.Comment
import com.example.musicapp.models.Playlist
import com.example.musicapp.models.Song
import com.example.musicapp.services.PlayMusicService
import com.example.musicapp.utils.FileDownloadTask
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class SongFragment(private val song: Song, private var songs: ArrayList<Song>): Fragment(R.layout.fragment_song){
//    Recycler view
    private lateinit var rcvSong: RecyclerView
//    ViewPage
    private lateinit var vpSong: ViewPager2
//    Button
    private lateinit var btnPlay: ImageButton
    private lateinit var btnPause: ImageButton
    private lateinit var btnNext: ImageButton
    private lateinit var btnPre: ImageButton
    private lateinit var btnRandom: ImageButton
    private lateinit var btnLoop: ImageButton
    private lateinit var btnCmt : ImageButton
    private lateinit var btnHeart: ImageButton
//    TextView
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
//    Seekbar
    private lateinit var sbrMusic: SeekBar

    private lateinit var btnMenu : ImageButton

    private lateinit var storage: FirebaseStorage

    lateinit var dialog : LoadingDialog

    lateinit var db : FirebaseFirestore

    lateinit var auth : FirebaseAuth

    private lateinit var commentAdapter : CommentAdapter
    private val playlistController = PlaylistController()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_song, container, false)
        rcvSong = view.findViewById(R.id.rcvSong)
        vpSong = view.findViewById(R.id.vpSong)

        btnPlay = view.findViewById(R.id.btnPlay)
        btnPause = view.findViewById(R.id.btnPause)
        btnNext = view.findViewById(R.id.btnNext)
        btnPre = view.findViewById(R.id.btnPre)
        btnRandom = view.findViewById(R.id.btnRandom)
        btnLoop = view.findViewById(R.id.btnLoop)
        sbrMusic = view.findViewById(R.id.sbrMusic)
        tvStartTime = view.findViewById(R.id.tvStartTime)
        tvEndTime = view.findViewById(R.id.tvEndTime)
        btnMenu = view.findViewById(R.id.btnMenuSong)
        btnCmt = view.findViewById(R.id.img_showCmt)
        btnHeart = view.findViewById(R.id.btn_heart)

        dialog = LoadingDialog(requireActivity())
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()

        val fUser = auth.currentUser
        val email = fUser?.email.toString()

        val popupMenu = PopupMenu(context, btnMenu)
        popupMenu.inflate(R.menu.menu_song2)

        btnCmt.setOnClickListener {
            showComment()
        }

        btnMenu.setOnClickListener{
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                // Handle menu item click here
                when (it.itemId) {
                    R.id.itAddPlaylist -> {
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
                        true
                    }
                    R.id.itDownload -> {
                        Toast.makeText(view.context, "Downloading", Toast.LENGTH_SHORT).show()
                        val sharedPreferences = view.context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
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
                            when {
                                ContextCompat.checkSelfPermission(
                                view.context,
                                Manifest.permission.ACCESS_MEDIA_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                // You can use the API that requires the permission.
                                val fileDownloadTask = FileDownloadTask(song.name, "")
                                fileDownloadTask.execute(uri.toString())
                                Toast.makeText(view.context, "Downloaded", Toast.LENGTH_SHORT).show()
                            }
                                else -> {
                                }
                            }
                        }
                        true
                    }

                    R.id.itShare -> {

                        val mp3Ref = storage.reference.child(song.link)
                        val urlTask: Task<Uri> = mp3Ref.downloadUrl
                        urlTask.addOnSuccessListener(OnSuccessListener<Uri> { uri ->
                            val downloadUrl = uri.toString()
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, "Song: " +song.name + ", url: " + downloadUrl)
                            intent.putExtra(Intent.EXTRA_TITLE, song.name)

                            val chooser =  Intent.createChooser(intent, "Share using")
                            startActivity(chooser)

                        })


                        true
                    }

                    R.id.itArtist -> {

                        val art = song.artist
                        val doc : DocumentReference = db.document(art)

                        doc.addSnapshotListener{ value, error ->
                            if(value != null){
                                val artist = value.toObject(Artist::class.java)
                                val activity = getActivity()
                                val transaction = activity?.supportFragmentManager?.beginTransaction()
                                val fragment : ArtistInformationFragment =  ArtistInformationFragment()
                                val bundle = Bundle()
                                bundle.putSerializable("artist", artist)
                                fragment.arguments = bundle
                                transaction?.replace(R.id.frgMain, fragment)
                                transaction?.addToBackStack("null")
                                transaction?.commit()
                            }
                        }
                        true
                    }

                    else -> false
                }
            }

        }

        val intent = Intent(activity, PlayMusicService::class.java)
        btnPlay.setOnClickListener{
            intent.putExtra("song", song)
            intent.putExtra("songs", songs)
            intent.action = "MUSIC_RESUME"
            btnPlay.visibility = View.INVISIBLE
            btnPause.visibility = View.VISIBLE

            activity?.startForegroundService(intent)
        }

        btnPause.setOnClickListener{
            intent.putExtra("song", song)
            intent.putExtra("songs", songs)
            intent.action = "MUSIC_PAUSE"
            btnPause.visibility = View.INVISIBLE
            btnPlay.visibility = View.VISIBLE

            activity?.startForegroundService(intent)
        }

        btnLoop.setOnClickListener{
            intent.action = "MUSIC_LOOP"
            intent.putExtra("song", song)
            intent.putExtra("songs", songs)
            activity?.startForegroundService(intent)
            if(btnLoop.backgroundTintList != ContextCompat.getColorStateList(view.context, R.color.selected_btn)){
                Toast.makeText(context, "Looping...", Toast.LENGTH_SHORT).show()
                btnLoop.backgroundTintList = ContextCompat.getColorStateList(view.context, R.color.selected_btn)
            }
            else{
                Toast.makeText(context, "End looping", Toast.LENGTH_SHORT).show()
                btnLoop.backgroundTintList = ContextCompat.getColorStateList(view.context, android.R.color.transparent)
            }
        }

        val mainActivity = context as MainActivity

        btnNext.setOnClickListener{
            val positionOfCurrentSong = songs.indexOf(song)
            var positionOfNextSong = 0
            if(positionOfCurrentSong < songs.size - 1){
                positionOfNextSong = positionOfCurrentSong + 1
            }
            intent.putExtra("songs", songs)
            intent.putExtra("song", songs[positionOfNextSong])
            intent.action = "NEW_MUSIC_PLAY"
            val songFragment = SongFragment(songs[positionOfNextSong], songs)
            mainActivity.loadFragment(songFragment, "music")
            mainActivity.startForegroundService(intent)
        }

        btnPre.setOnClickListener{
            val positionOfCurrentSong = songs.indexOf(song)
            var positionOfPreSong = 0
            if(positionOfCurrentSong > 0){
                positionOfPreSong = positionOfCurrentSong -1
            }
            intent.putExtra("songs", songs)
            intent.putExtra("song", songs[positionOfPreSong])
            intent.action = "NEW_MUSIC_PLAY"
            val songFragment = SongFragment(songs[positionOfPreSong], songs)
            mainActivity.loadFragment(songFragment, "music")
            mainActivity.startForegroundService(intent)
        }

        btnRandom.setOnClickListener{
            songs = ArrayList(songs.shuffled())
            rcvSong.adapter = context?.let { NewSongAdapter(it, songs, true, null) }
            rcvSong.adapter?.notifyDataSetChanged()
        }

        btnHeart.setOnClickListener{
            if(!song.isLoved) {
                btnHeart.setImageResource(R.drawable.heart_full)
                playlistController.updatePlaylist("add", song, "Lovely_$email", onComplete = {
                    Toast.makeText(context, "Adding to favorite playlist", Toast.LENGTH_LONG).show()
                }, onFail = {
                    Toast.makeText(context, "Can't add to favorite playlist", Toast.LENGTH_LONG).show()
                })
                song.isLoved = true
            }
            else{
                btnHeart.setImageResource(R.drawable.heart)
                playlistController.updatePlaylist("remove", song, "Lovely_$email", onComplete = {
                    Toast.makeText(context, "Removing from favorite playlist", Toast.LENGTH_LONG).show()
                }, onFail = {
                    Toast.makeText(context, "Can't remove from favorite playlist", Toast.LENGTH_LONG).show()
                })
                song.isLoved = false
            }
        }

        val fragmentList = listOf(DetailSongFragment(song), TrackFragment(song), LyricsFragment(song))

        sbrMusic.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (seekBar != null) {
                    tvStartTime.text = convertToMinute(seekBar.progress)
                    tvEndTime.text = convertToMinute(seekBar.max - seekBar.progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.e("MyApp", "You're stopping changing seekbar")
                if (seekBar != null) {
                    Intent().also { intent ->
                        intent.action = "UPDATE_MUSIC"
                        intent.putExtra("currentDuration", seekBar.progress)
                        context?.sendBroadcast(intent)
                    }
                }
            }
        })

        vpSong.adapter =
            activity?.let { TrackViewPagerAdapter(fragmentList, it.supportFragmentManager, lifecycle) }
        vpSong.currentItem = 1

        registerForContextMenu(rcvSong)
        rcvSong.adapter = context?.let { NewSongAdapter(it, songs, true, null) }
        rcvSong.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        playlistController.updatePlaylist("add", song, "ListenedPlaylist_$email", onComplete = {
            Log.e("MyApp", "Add to ListenedPlaylist successfully")
        }, onFail = {
            Log.e("MyApp", "Add to ListenedPlaylist failed")
        })
        return view
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            val mainActivity = context as MainActivity
            when (intent.action){
                "com.example.UPDATE_SEEKBAR" -> {
                    val currentPosition = intent.getIntExtra("currentPosition", 0)
                    val max = intent.getIntExtra("max", 0)

                    sbrMusic.max = max
                    sbrMusic.progress = currentPosition
                }
                "com.example.PAUSE_MUSIC" -> {
                    btnPause.visibility = View.INVISIBLE
                    btnPlay.visibility = View.VISIBLE
                }
                "com.example.PLAY_MUSIC" -> {
                    btnPlay.visibility = View.INVISIBLE
                    btnPause.visibility = View.VISIBLE
                }
                "com.example.CHANGE_MUSIC" -> {
                    val status = intent.getStringExtra("status")
                    val positionOfCurrentSong = songs.indexOf(song)
                    var position = 0
                    if(status == "next"){
                        if(positionOfCurrentSong < songs.size - 1){
                            position = positionOfCurrentSong + 1
                        }
                    }
                    else if(status == "pre"){
                        if(positionOfCurrentSong > 0){
                            position = positionOfCurrentSong - 1
                        }
                    }

                    val songFragment = SongFragment(songs[position], songs)
                    mainActivity.loadFragment(songFragment, "music")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(receiver, IntentFilter("com.example.UPDATE_SEEKBAR"))
        requireActivity().registerReceiver(receiver, IntentFilter("com.example.CHANGE_MUSIC"))
        requireActivity().registerReceiver(receiver, IntentFilter("com.example.PAUSE_MUSIC"))
        requireActivity().registerReceiver(receiver, IntentFilter("com.example.PLAY_MUSIC"))
    }

    override fun onPause() {
        super.onPause()
        requireActivity().unregisterReceiver(receiver)
    }

    fun convertToMinute(millisecond: Int): String {
        val second = millisecond / 1000
        val m = second / 60
        val s = second % 60
        return String.format("%02d:%02d", m, s)
    }

    private fun showComment(){
        val bottomDialogComment = BottomSheetDialog(requireActivity(),R.style.CustomBottomSheetDialogTheme)
        val bottomCommentView =LayoutInflater.from(context).inflate(R.layout.layout_comment, view?.findViewById(R.id.commentContainer), false)
        val listCmt = song.getCommentsUser()
        if(listCmt != null ){
            commentAdapter  = context?.let { CommentAdapter(it,listCmt,song) }!!
            val recyclerView : RecyclerView = bottomCommentView.findViewById(R.id.listCmt)
            val title : TextView = bottomCommentView.findViewById(R.id.textView11)
            title.text = "Comment "+ "(" + listCmt.size + ")"
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = commentAdapter
            recyclerView.layoutManager =  layoutManager
            recyclerView.addItemDecoration(
                DividerItemDecoration(
                    context,
                    layoutManager.orientation
                )
            )

            val imgSend : ImageButton = bottomCommentView.findViewById(R.id.imageButton)
            val edt_cmt : EditText = bottomCommentView.findViewById(R.id.edt_cmt)

            imgSend.setOnClickListener{
                val builder = AlertDialog.Builder(context)
                builder.setMessage("Are you sure you want to send your comment?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, id ->
                        val content = edt_cmt.text.toString()
                        if(content == ""){
                            Toast.makeText(context,"Please enter your comment",Toast.LENGTH_SHORT).show()
                        }
                        else{
                            if(content.length > 40){
                                Toast.makeText(context,"Comment length is limited to 40 characters",Toast.LENGTH_SHORT).show()
                            }
                            else{
                                this.dialog = LoadingDialog(requireActivity())

                                this.dialog.ShowDialog("Send Comment")

                                val docRefCmt = db.collection("Comments").document()
                                val id = docRefCmt.id

                                val fUser = auth.currentUser
                                val email = fUser?.email.toString()

                                val userRef : DocumentReference = db.collection("Users").document(email)

                                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy")
                                val current = LocalDateTime.now().format(formatter)


                                val newComment = Comment(id.toString(),userRef,current,content) // tao comment moi

                                db.collection("Comments").document(id).set(newComment)
                                    .addOnSuccessListener { doc ->
                                        var listOldCmt  : ArrayList<String>? = song.comments
                                        val songRef = db.collection("Songs").document(song.id.toString())

                                        if(listOldCmt == null || listOldCmt.size == 0){
                                            listOldCmt = ArrayList<String>()
                                            listOldCmt?.add("Comments/"+ id)
                                            song.comments = listOldCmt
                                            songRef.set(song)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context,"Send comment successfully",Toast.LENGTH_SHORT).show()
                                                    edt_cmt.setText("")
                                                    song.getCommentsUser()
                                                        ?.let { it1 -> commentAdapter.setData(it1) }

                                                    commentAdapter.notifyDataSetChanged()

                                                    title.text = "Comment "+ "(" + listOldCmt?.size + ")"

                                                    this.dialog.HideDialog()
                                                }

                                                .addOnFailureListener{
                                                    Toast.makeText(context,"Send comment failed",Toast.LENGTH_SHORT).show()
                                                    this.dialog.HideDialog()
                                                }
                                        }
                                        else{
                                            listOldCmt?.add("Comments/"+ id)
                                            songRef.update("comments",listOldCmt)
                                                .addOnSuccessListener {
                                                    Toast.makeText(context,"Send comment successfully",Toast.LENGTH_SHORT).show()
                                                    edt_cmt.setText("")

                                                    song.comments   = listOldCmt
                                                    song.getCommentsUser()
                                                        ?.let { it1 -> commentAdapter.setData(it1) }

                                                    commentAdapter.notifyDataSetChanged()

                                                    title.text = "Comment "+ "(" + listOldCmt?.size + ")"

                                                    this.dialog.HideDialog()

                                                }
                                                .addOnFailureListener{
                                                    Toast.makeText(context,"Send comment failed",Toast.LENGTH_SHORT).show()
                                                    this.dialog.HideDialog()
                                                }
                                        }
                                    }

                                    .addOnFailureListener{e ->
                                        Toast.makeText(context,"Send comment failed",Toast.LENGTH_SHORT).show()
                                        this.dialog.HideDialog()
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

            bottomDialogComment.setContentView(bottomCommentView)
            bottomDialogComment.show()
        }else{
          //  Toast.makeText(context,"No comments yet", Toast.LENGTH_SHORT).show()
        }

    }
}
