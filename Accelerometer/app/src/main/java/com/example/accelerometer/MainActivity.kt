package com.example.accelerometer

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.accelerometer.ui.theme.AccelerometerTheme

class MainActivity : ComponentActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var mXAxis = mutableStateOf(0f)
    private var mYAxis = mutableStateOf(0f)
    private var mZAxis = mutableStateOf(0f)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeSensor()
        setContent {
            AccelerometerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AccelerometerValues(mXAxis, mYAxis, mZAxis)
                }
            }
        }
    }

    private fun initializeSensor() {
        //Δημιουργία ενός instance του SensorManager
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        //Ελέγχουμε αν υπάρχει ο αισθητήρας και τον καταχωρούμε για να λαμβάνουμε ενδείξεις
        if (sensor != null)
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_FASTEST,
                SensorManager.SENSOR_DELAY_FASTEST
            )

    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if(p0?.sensor?.type == Sensor.TYPE_ACCELEROMETER){
            mXAxis.value = p0.values[0]
            mYAxis.value = p0.values[1]
            mZAxis.value = p0.values[2]
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        return
    }

    override fun onDestroy() {
        sensorManager.unregisterListener(this)
        super.onDestroy()
    }
}


@Composable
fun AccelerometerValues(
    xAxis: MutableState<Float> = mutableStateOf(0f),
    yAxis: MutableState<Float> = mutableStateOf(0f),
    zAxis: MutableState<Float> = mutableStateOf(0f)
){
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(50.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "xAxis: %.5f".format(xAxis.value),
            fontSize = 18.sp,
            color = Color.Blue
        )
        Text(
            text = "yAxis: %.5f".format(yAxis.value),
            fontSize = 18.sp,
            color = Color.Blue
        )
        Text(
            text = "zAxis: %.5f".format(zAxis.value),
            fontSize = 18.sp,
            color = Color.Blue
        )
    }
}


@Composable
fun Square(upDown: MutableState<Float> = mutableStateOf(0f), sides: MutableState<Float> = mutableStateOf(0f)){
    val s by remember{ sides}
    val up by remember{ upDown }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Canvas(
            modifier = Modifier
                .padding(20.dp)
                .size(300.dp)
                .graphicsLayer {
                    rotationX = up * 3f
                    rotationY = s * 3f
                    translationX = s * -10
                    translationY = up * 10
                    translationX = s * -10
                    translationY = up * 10
                }
        ){
            drawRect(
                Color.Red,
                size = size
            )
        }
        Text(
            text = "up/down ${up.toInt()}\nleft/right ${s.toInt()}",
            fontSize = 18.sp,
            color = Color.Blue,
            textAlign = TextAlign.Center,
            modifier = Modifier
        )
    }


}

@Preview(showBackground = true)
@Composable
fun AccelerometerPreview() {
    AccelerometerTheme {
        //Square()
        AccelerometerValues()
    }
}
