package com.example.cup2

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        val videoUrl = intent.getStringExtra("VIDEO_URL") ?: ""
        val videoTitle = intent.getStringExtra("VIDEO_TITLE") ?: ""

        val videoView = findViewById<VideoView>(R.id.videoView)
        val webView = findViewById<WebView>(R.id.webView)
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        val tvVideoTitle = findViewById<TextView>(R.id.tvVideoTitle)

        tvVideoTitle.text = videoTitle

        if (videoUrl.contains("youtube.com") || videoUrl.contains("youtu.be")) {
            // YouTube video → use iframe WebView
            videoView.visibility = View.GONE
            webView.visibility = View.VISIBLE

            playYouTubeWithWebView(videoUrl,webView, progressBar)

        } else {
            // Normal video → use VideoView
            webView.visibility = View.GONE
            videoView.visibility = View.VISIBLE

            playRegularVideo(videoUrl, videoView, progressBar)
        }
    }

    private fun playYouTubeWithWebView(videoUrl: String, webView: WebView, progressBar: ProgressBar) {

        val videoId = "5Ukha_E-sKE" // or extract dynamically

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.mediaPlaybackRequiresUserGesture = false

        webView.webChromeClient = object : WebChromeClient() {}
        webView.webViewClient = WebViewClient()

        val html = """
        <html>
        <body style="margin:0;padding:0;background:black;">
            <iframe 
                width="100%" 
                height="100%" 
                src="https://www.youtube.com/embed/$videoId?autoplay=1&playsinline=1" 
                frameborder="0" 
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
                allowfullscreen>
            </iframe>
        </body>
        </html>
    """.trimIndent()

        webView.loadDataWithBaseURL(
            "https://www.youtube.com",
            html,
            "text/html",
            "utf-8",
            null
        )

        progressBar.visibility = View.GONE
    }

    private fun playRegularVideo(
        url: String,
        videoView: VideoView,
        progressBar: ProgressBar
    ) {
        val mediaController = MediaController(this)
        mediaController.setAnchorView(videoView)
        videoView.setMediaController(mediaController)

        videoView.setVideoURI(Uri.parse(url))

        videoView.setOnPreparedListener {
            progressBar.visibility = View.GONE
            videoView.start()
        }

        videoView.setOnErrorListener { _, _, _ ->
            progressBar.visibility = View.GONE
            Toast.makeText(this, "Error playing video", Toast.LENGTH_SHORT).show()
            true
        }
    }
}