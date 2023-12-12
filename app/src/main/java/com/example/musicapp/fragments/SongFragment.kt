package com.example.musicapp.fragments

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.musicapp.R
import com.example.musicapp.activities.MainActivity
import com.example.musicapp.adapters.SongAdapter
import com.example.musicapp.adapters.TrackViewPagerAdapter
import com.example.musicapp.models.Song
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Timer
import java.util.TimerTask

class SongFragment(private val song: Song, private val songs: ArrayList<Song>): Fragment(R.layout.fragment_song){
//    Recycler view
    private lateinit var rcvSong: RecyclerView
//    ViewPage
    private lateinit var vpSong: ViewPager2
//    Button
    private lateinit var btnPlay: ImageButton
//    TextView
    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
//    Seekbar
    private lateinit var sbrMusic: SeekBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_song, container, false)
        rcvSong = view.findViewById(R.id.rcvSong)
        vpSong = view.findViewById(R.id.vpSong)

        val mainActivity = context as MainActivity

        btnPlay = view.findViewById(R.id.btnPlay)
        sbrMusic = view.findViewById(R.id.sbrMusic)
        tvStartTime = view.findViewById(R.id.tvStartTime)
        tvEndTime = view.findViewById(R.id.tvEndTime)

        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference.child(song.link)

        btnPlay.setOnClickListener{
            Log.e("MyApp", "Initialize: " + mainActivity.mediaPlayer.isPlaying.toString())
            if(mainActivity.mediaPlayer.isPlaying){
                mainActivity.mediaPlayer.stop()
                btnPlay.setImageDrawable(context?.resources?.getDrawable(R.drawable.icon_play, null))
            }
            else{
                btnPlay.setImageDrawable(context?.resources?.getDrawable(R.drawable.icon_pause, null))

                val intent = Intent(activity, Song::class.java)
                intent.putExtra("song", song)

                activity?.startForegroundService(intent)

                storageRef.downloadUrl.addOnSuccessListener {
                    mainActivity.mediaPlayer = MediaPlayer.create(context, it)
                    sbrMusic.max = mainActivity.mediaPlayer.duration
                            tvEndTime.text = convertToMinute(mainActivity.mediaPlayer.duration)
                    mainActivity.mediaPlayer.start()

                    Timer().scheduleAtFixedRate(object: TimerTask() {
                        override fun run() {
                            sbrMusic.progress = mainActivity.mediaPlayer.currentPosition
                        }
                    }, 0, 500)

                    sbrMusic.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
                        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                            if (seekBar != null) {
                                tvStartTime.text = convertToMinute(seekBar.progress)
                                tvEndTime.text = convertToMinute(mainActivity.mediaPlayer.duration - seekBar.progress)
                            }
                            mainActivity.mediaPlayer.seekTo(progress)
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
                            Log.e("MyApp", "You're changing seekbar")
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
                            Log.e("MyApp", "You're stopping changing seekbar")
                        }
                    })
                }
            }
        }

        val fragmentList = listOf(DetailSongFragment(song), TrackFragment(song), LyricsFragment(song))

        vpSong.adapter =
            activity?.let { TrackViewPagerAdapter(fragmentList, it.supportFragmentManager, lifecycle) }
        vpSong.currentItem = 1

        registerForContextMenu(rcvSong)
        rcvSong.adapter = context?.let { SongAdapter(it, songs) }
        rcvSong.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        return view
    }

    fun convertToMinute(millisecond: Int): String {
        val second = millisecond / 1000
        val m = second / 60
        val s = second % 60
        return String.format("%02d:%02d", m, s)
    }
}
