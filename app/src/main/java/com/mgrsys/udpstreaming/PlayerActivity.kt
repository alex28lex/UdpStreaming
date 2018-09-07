package com.mgrsys.udpstreaming

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText

import com.mgrsys.authorization.udpstreaming.R
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer


class PlayerActivity : AppCompatActivity() {

    private var shouldAutoPlay: Boolean = false
    lateinit var mMediaPlayer: MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_player)
        shouldAutoPlay = true
        findViewById<Button>(R.id.initStream).setOnClickListener(View.OnClickListener {
            play()
            KeyboardUtil.hideKeyboard(findViewById<Button>(R.id.initStream))
        })
        findViewById<Button>(R.id.stopStream).setOnClickListener(View.OnClickListener {
            stop()
            KeyboardUtil.hideKeyboard(findViewById<Button>(R.id.stopStream))
        })
        findViewById<View>(R.id.clearUdpView).setOnClickListener(View.OnClickListener {

            findViewById<EditText>(R.id.address).setText("")
        })
        findViewById<View>(R.id.clearBufferView).setOnClickListener(View.OnClickListener {

            findViewById<EditText>(R.id.buffer).setText("")
        })



    }

    fun play() {
        val options = ArrayList<String>()
        options.add("--file-caching=2000")
        options.add("-vvv")

        val mLibVLC = LibVLC(applicationContext, options)
        mMediaPlayer = MediaPlayer(mLibVLC)

        val media = Media(mLibVLC, Uri.parse(findViewById<EditText>(R.id.address).text.toString()))
        media.setHWDecoderEnabled(true, false)
        media.addOption(":network-caching=150")
        media.addOption(":clock-jitter=0")
        media.addOption(":clock-synchro=0")

        mMediaPlayer.media = media
        mMediaPlayer.play()
    }

    fun stop() {
        mMediaPlayer.release()
    }


}
