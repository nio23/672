package com.example.pistreamer

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.pistreamer.ui.theme.PiStreamerTheme
import com.longdo.mjpegviewer.MjpegView
import com.example.pistreamer.Utils.postToast


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PiStreamerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    StreamScreen()
                }
            }
        }
    }
}

@Composable
fun StreamScreen(modifier: Modifier = Modifier) {
    val mContext = LocalContext.current
    val mjpegListener = MjpegImplementation(mContext)

    val url = "http://192.168.10.196:8000"
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted->
        if (isGranted)
            mjpegListener.capturePhoto()
        else
            postToast(mContext, "Permission not granted")

    }
    Column(
        modifier = modifier
    )
    {
        AndroidView(
            modifier = Modifier.fillMaxWidth(),
            factory = { context ->
                MjpegView(context).apply {
                    mode = MjpegView.MODE_FIT_WIDTH
                    isAdjustHeight = true
                    supportPinchZoomAndPan = true
                    setUrl("$url/stream.mjpg")
                    startStream()
                    stateChangeListener = mjpegListener
                }
            }
        )
        Row {
            var isCapturing by remember {
                mutableStateOf(false)
            }
            Button(
                onClick = {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        val isPermitted = checkForExternalStoragePermission(mContext)
                        if (!isPermitted)
                            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        else
                            mjpegListener.capturePhoto()
                    }else{
                        mjpegListener.capturePhoto()
                    }
                    request(mContext, Request.Method.GET, "$url/capture_image")
                }
            ) {
                Text(text = "Capture photo")
            }
            Button(
                onClick = {
                    isCapturing = !isCapturing
                    if (isCapturing){
                        request(mContext, Request.Method.GET, "$url/start_encoder")
                    }else{
                        request(mContext, Request.Method.GET,"$url/stop_encoder")
                    }
                }
            ) {
                val mText = if (!isCapturing) "Start video recording" else "Stop recording"
                Text(text = mText)
            }
        }

    }

}

fun request(context: Context, method: Int, requestUrl: String){
    val queue = Volley.newRequestQueue(context)
    val stringRequest = StringRequest(
        method, requestUrl,
        {
            Log.d("MainActivity", "Response from $requestUrl $it")
        },
        {
            Log.d("MainActivity", "Url error $it")
        }
    )
    queue.add(stringRequest)
}

fun checkForExternalStoragePermission(context: Context): Boolean{
    val permissionCheckResult = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    return permissionCheckResult == PackageManager.PERMISSION_GRANTED
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PiStreamerTheme {
        StreamScreen()
    }
}