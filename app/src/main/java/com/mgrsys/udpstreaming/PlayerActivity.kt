package com.mgrsys.udpstreaming

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.*
import com.mgrsys.authorization.udpstreaming.R

class PlayerActivity : AppCompatActivity(), TransferListener<UdpDataSource> {

    private lateinit var player: SimpleExoPlayer
    private var shouldAutoPlay: Boolean = false
    private lateinit var uri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        shouldAutoPlay = true
        findViewById<Button>(R.id.initStream).setOnClickListener(View.OnClickListener {
            releasePlayer()
            initPlayer()
            KeyboardUtil.hideKeyboard(findViewById<Button>(R.id.initStream))
        })

    }

    private fun initPlayer() {
        uri = Uri.parse(findViewById<EditText>(R.id.address).text.toString())
        // this part returns nullable object (doing lookup)
        val simpleExoPlayerView = findViewById<SimpleExoPlayerView>(R.id.player_view)

        // Default instances
        val bandwidthMeter = DefaultBandwidthMeter()
        val trackSelectionFactory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector = DefaultTrackSelector(trackSelectionFactory)

        // Here we setup UdpDataSource
        // This part will probably need to setup TransferListener (might be implemented by this class itself
        // This listener is pass as 2nd argument to DefaultDataSourceFactory as well as to UdpDataSource
        val myDataSourceFactory = DefaultDataSourceFactory(this, null, { CustomUdpDataSource(null, 2000, 100000) })

        // Extractor factory & media source
        val extractorsFactory = DefaultExtractorsFactory()
        val mediaSource = ExtractorMediaSource(uri, myDataSourceFactory, extractorsFactory, null, null)

        // Main initialization side-effects
        simpleExoPlayerView.requestFocus()
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        player.prepare(mediaSource)
        player.playWhenReady = shouldAutoPlay;
        simpleExoPlayerView.player = player
    }

    // UDP stream
    // might not be necessary
    override fun onTransferStart(source: UdpDataSource?, dataSpec: DataSpec?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onTransferEnd(source: UdpDataSource?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBytesTransferred(source: UdpDataSource?, bytesTransferred: Int) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    // Activity
    private fun releasePlayer() {
        if (::player.isInitialized) {
            player.release()
            shouldAutoPlay = player.playWhenReady
        }

    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        // initPlayer()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

}
