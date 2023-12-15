package com.example.musicapp.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.musicapp.R
import com.example.musicapp.adapters.NewSongAdapter
import com.example.musicapp.adapters.TrackViewPagerAdapter
import com.example.musicapp.models.Song
import com.example.musicapp.services.PlayMusicService
import com.google.android.material.bottomsheet.BottomSheetDialog

class SongFragment(private val song: Song, private val songs: ArrayList<Song>): Fragment(R.layout.fragment_song){
//    Recycler view
    private lateinit var rcvSong: RecyclerView
//    ViewPage
    private lateinit var vpSong: ViewPager2
//    Button
    private lateinit var btnPlay: ImageButton
    private lateinit var btnPause: ImageButton
//    TextView
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
//    Seekbar
    private lateinit var sbrMusic: SeekBar

    private lateinit var btnMenu : ImageButton

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
        sbrMusic = view.findViewById(R.id.sbrMusic)
        tvStartTime = view.findViewById(R.id.tvStartTime)
        tvEndTime = view.findViewById(R.id.tvEndTime)
        btnMenu = view.findViewById(R.id.btnMenuSong)

        val popupMenu = PopupMenu(context, btnMenu)
        popupMenu.inflate(R.menu.menu_song2)

        btnMenu.setOnClickListener{
            popupMenu.show()
            popupMenu.setOnMenuItemClickListener {
                // Handle menu item click here
                when (it.itemId) {
                    R.id.itComment -> {
                        showComment()
                        true
                    }

                    R.id.itContact -> {

                        true
                    }

                    else -> false
                }
            }

        }



        btnPlay.setOnClickListener{
            val intent = Intent(activity, PlayMusicService::class.java)
            intent.putExtra("song", song)
            intent.action = "MUSIC_PLAY"
            btnPlay.visibility = View.INVISIBLE
            btnPause.visibility = View.VISIBLE

            activity?.startForegroundService(intent)
        }

        btnPause.setOnClickListener{
            val intent = Intent(activity, PlayMusicService::class.java)
            intent.action = "MUSIC_STOP"
            btnPause.visibility = View.INVISIBLE
            btnPlay.visibility = View.VISIBLE

            activity?.startForegroundService(intent)
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
                Log.e("MyApp", "You're changing seekbar")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                Log.e("MyApp", "You're stopping changing seekbar")
                if (seekBar != null) {
                    Intent().also { intent ->
                        intent.action = "com.example.updateMedia"
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
        rcvSong.adapter = context?.let { NewSongAdapter(it, songs) }
        rcvSong.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        return view
    }

    private val receiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == "com.example.updateSeekBar") {
                val currentPosition = intent.getIntExtra("currentPosition", 0)
                val max = intent.getIntExtra("max", 0)

                sbrMusic.max = max
                sbrMusic.progress = currentPosition
            }
        }
    }

    // ... (existing code)

    override fun onResume() {
        super.onResume()
        requireActivity().registerReceiver(receiver, IntentFilter("com.example.updateSeekBar"))
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


    fun showComment(){
        val bottomDialogComment = BottomSheetDialog(requireActivity(),R.style.CustomBottomSheetDialogTheme)
        val bottomCommentView =LayoutInflater.from(context).inflate(R.layout.layout_comment, view?.findViewById(R.id.commentContainer), false)


        bottomDialogComment.setContentView(bottomCommentView)
        bottomDialogComment.show()
    }
}
