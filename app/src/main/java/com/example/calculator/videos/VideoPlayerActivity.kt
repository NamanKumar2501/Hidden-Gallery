package com.example.calculator.videos

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.R

class VideoPlayerActivity : AppCompatActivity() {

    lateinit var videoView: VideoView

    // on below line we are creating
    // a variable for our video url.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        var videoUrl = intent.getStringExtra("VIDEO_URL")

        videoView = findViewById(R.id.videoView);

        // on below line we are creating
        // uri for our video view.
        val uri: Uri = Uri.parse(videoUrl)

        // on below line we are setting
        // video uri for our video view.
        videoView.setVideoURI(uri)

        // on below line we are creating variable
        // for media controller and initializing it.
        val mediaController = MediaController(this)

        // on below line we are setting anchor
        // view for our media controller.
        mediaController.setAnchorView(videoView)

        // on below line we are setting media player
        // for our media controller.
        mediaController.setMediaPlayer(videoView)

        // on below line we are setting media
        // controller for our video view.
        videoView.setMediaController(mediaController)

        // on below line we are
        // simply starting our video view.
        videoView.start()



    }
}
