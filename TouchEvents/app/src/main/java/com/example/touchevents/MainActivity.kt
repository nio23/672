@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.example.touchevents

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.touchevents.ui.theme.TouchEventsTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TouchEventsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TouchText()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TouchText(){
    var mColor by remember {
        mutableStateOf(Color.Transparent)
    }
    var mText by remember {
        mutableStateOf("Tap to trigger events")
    }
    var dragOffsetY by remember {
        mutableStateOf(0f)
    }
    Text(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .offset { IntOffset(0, dragOffsetY.roundToInt()) }
            .border(2.dp, Color.Black, CircleShape)
            .background(mColor, CircleShape)
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState {
                    dragOffsetY += it
                }
            )
            .combinedClickable(
                onClick = {
                    mColor = Color.Cyan
                    mText = "onClick triggered"
                },
                onDoubleClick = {
                    mColor = Color.Green
                    mText = "onDoubleClick triggered"
                },
                onLongClick = {
                    mColor = Color.Yellow
                    mText = "onLongClick triggered"
                }
            ),
        text = mText,
        textAlign = TextAlign.Center,
        fontSize = 28.sp
    )
}

/*@Composable
fun TouchText() {
    var mColor by remember {
        mutableStateOf(Color.Transparent)
    }
    var mText by remember {
        mutableStateOf("Tap to trigger events")
    }
    var dragOffsetY by remember {
        mutableStateOf(0f)
    }

    Text(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .offset { IntOffset(0, dragOffsetY.roundToInt()) }
            .border(2.dp, Color.Black, CircleShape)
            .background(mColor, CircleShape)
            .clickable {
                mColor = Color.Cyan
            }
            .combinedClickable(
                onClick = {
                    mColor = Color.Cyan
                    mText = "onClick triggered"
                },
                onDoubleClick = {
                    mColor = Color.Green
                    mText = "onDoubleClick triggered"
                },
                onLongClick = {
                    mColor = Color.Yellow
                    mText = "onLongClick triggered"
                }
            )
            .draggable(
                orientation = Orientation.Vertical,
                state = rememberDraggableState {
                    dragOffsetY += it
                }
            ),
        text = mText,
        textAlign = TextAlign.Center,
        fontSize = 28.sp
    )

}*/


@Composable
fun GreetingPreview() {
    TouchEventsTheme {
        TouchText()
    }
}