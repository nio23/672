package com.example.videoplayer

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.example.videoplayer.ui.theme.VideoPlayerTheme
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VideoPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FullVideoPlayer(videoId = "E_8LHkn4g-Q")
                    //VideoPlayer()
                }
            }
        }
    }
}

@Composable
fun VideoPlayer(){
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    AndroidView(
        factory = {
            YouTubePlayerView(it).apply {
                addYouTubePlayerListener(object:AbstractYouTubePlayerListener(){
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        youTubePlayer.cueVideo("E_8LHkn4g-Q", 0f)
                    }
                })
                lifecycle.addObserver(this)
            }
        }
    )
}




@Composable
fun FullVideoPlayer(context: Context = LocalContext.current, videoId: String = "E_8LHkn4g-Q", lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current){
    val frameLayout = FrameLayout(context)

    AndroidView(
        factory = {
            initializePlayerView(
                it,
                lifecycleOwner,
                videoId,
                onEnterFullScreen = {view->
                    frameLayout.apply {
                        visibility = View.VISIBLE
                        addView(view)
                    }
                },
                onExitFullScreen = {
                    frameLayout.apply {
                        visibility = View.GONE
                        removeAllViews()
                    }
                }
            )
        }
    )

    AndroidView(
        factory = {
            frameLayout
        }
    )



}



fun initializePlayerView(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    videoId: String,
    onEnterFullScreen: (View) -> Unit,
    onExitFullScreen: () -> Unit
): YouTubePlayerView
{
    val iFramePlayerOptions: IFramePlayerOptions = IFramePlayerOptions.Builder()
        .controls(1)
        .fullscreen(1)
        .build()

    val playerView = YouTubePlayerView(context)
    playerView.apply {
        enableAutomaticInitialization = false

        addFullscreenListener(object: FullscreenListener{
            override fun onEnterFullscreen(
                fullscreenView: View,
                exitFullscreen: () -> Unit
            ) {
                onEnterFullScreen(fullscreenView)
            }

            override fun onExitFullscreen() {
                onExitFullScreen()
            }
        })

        val youTubePlayerListener = object : AbstractYouTubePlayerListener(){
            override fun onReady(youTubePlayer: YouTubePlayer) {
                youTubePlayer.cueVideo(videoId, 0f)
            }


        }

        initialize( youTubePlayerListener, iFramePlayerOptions)
    }
    lifecycleOwner.lifecycle.addObserver(playerView)
    return playerView
}



@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    VideoPlayerTheme {
        //VideoPlayer(videoId = "E_8LHkn4g-Q")
    }
}


