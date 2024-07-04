package com.example.bookreader

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.bookreader.ui.theme.BookReaderTheme
import com.github.barteksc.pdfviewer.PDFView

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookReaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookReader()
                }
            }
        }
    }
}

@Composable
fun BookReader() {
    var selectedUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult ={
            selectedUri= it
        }
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {context->
                PDFView(context, null)
            },
            update = {pdfView->
                selectedUri?.let {
                    pdfView.fromUri(it)
                        .swipeHorizontal(true)
                        .pageSnap(true)
                        .pageFling(true)
                        .load()
                }

            }
        )

        FloatingActionButton(
            modifier = Modifier
                .size(200.dp)
                .padding(60.dp)
                .align(Alignment.BottomEnd),
            containerColor = Color.Red,
            onClick = {
                pdfPicker.launch(arrayOf("application/pdf"))
            }
        ) {
            Icon(
                imageVector = Icons.Rounded.AddCircle,
                "Select document",
                modifier = Modifier.fillMaxSize().padding(10.dp),
                tint = Color.White)
        }
    }

}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview() {
    BookReaderTheme {
        BookReader()
    }
}