package com.example.mediaplayer

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mediaplayer.ui.theme.MediaPlayerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediaPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    VideoPlayer()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VideoPlayer(){
    var mediaUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val selectMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            mediaUri = it
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            modifier = Modifier.weight(2f),
            factory = {context->
                val mediaController = MediaController(context)
                VideoView(context).apply {
                    setMediaController(mediaController)
                    mediaController.setAnchorView(this)
                }
            },
            update = {videoView->
                mediaUri?.let {uri->
                    videoView.setVideoURI(uri)
                    videoView.start()
                }
            }
        )

        Button(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize(),
            onClick = {
                selectMedia.launch("video/*")
            }
        ) {
            Text(
                text = "Choose a video"
            )
        }
    }
}



/*@Preview(showBackground = true, showSystemUi = true)
@Composable
fun VideoPlayer() {
    var mediaUri by remember {
        mutableStateOf<Uri?>(null)
    }
    
    val selectMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            mediaUri = it
        }
    )


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            modifier = Modifier
                .weight(2f),
            factory = {context->
                val mediaController = MediaController(context)
                VideoView(context).apply {
                    setMediaController(mediaController)
                    mediaController.setAnchorView(this)
                }
            },
            update = {videoView->
                mediaUri?.let {uri->
                    videoView.setVideoURI(uri)
                    videoView.start()
                }
            }
        )

        Button(
            modifier = Modifier
                .weight(1f)
                .wrapContentSize(),
            onClick = {
                selectMedia.launch("video/*")
            }
        ) {
            Text(
                text = "Choose a video"
            )
        }
    }

}*/

*/
@Composable
fun GreetingPreview() {
    MediaPlayerTheme {
        VideoPlayer()
    }
}