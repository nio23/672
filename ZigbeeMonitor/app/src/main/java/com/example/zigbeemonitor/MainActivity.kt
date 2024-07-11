package com.example.zigbeemonitor

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DeviceThermostat
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.zigbeemonitor.ui.theme.ZigbeeMonitorTheme
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ZigbeeMonitorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun Preview() {
    ZigbeeMonitorTheme {
        MainScreen()
    }
}

@Composable
fun MainScreen(){
    val connSettings = remember {
        mutableStateOf(ConnectionSettings())
    }

    val topic = remember {
        mutableStateOf("zigbee2mqtt/office/temp")
    }

    val isConnected = remember {
        mutableStateOf(false)
    }

    val sonoffData = getData(topic.value)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        SonoffFrame(snzb = sonoffData)
        ConnectionProperties(connSettings = connSettings, topic = topic, isConnected = isConnected.value)
        ConnectionBtn(connSettings = connSettings.value, topic = topic.value, isConnected = isConnected)
    }
}

fun getData(topic: String): SonoffSNZB{
    return if(MqttHandler.messagesMap.containsKey(topic))
        Gson().fromJson(MqttHandler.messagesMap[topic], SonoffSNZB::class.java)
    else SonoffSNZB()
}

@Composable
fun SonoffFrame(snzb: SonoffSNZB = SonoffSNZB()){
    //val snzb = getSonoffData(topic)
    Column(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxHeight(0.5f),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            content = {
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier
                            .weight(4f)
                            .scale(5f),
                        imageVector = Icons.Rounded.DeviceThermostat,
                        contentDescription = null,
                        tint = Color.Red
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "${snzb.temperature} \u2103",
                        fontSize = 35.sp
                    )
                }

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier
                            .weight(4f)
                            .scale(5f),
                        imageVector = Icons.Rounded.WaterDrop,
                        contentDescription = null,
                        tint = Color.Cyan
                    )
                    Text(
                        modifier = Modifier.weight(1f),
                        text = "${snzb.humidity} %",
                        fontSize = 35.sp
                    )
                }

            }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            //{"battery":82,"humidity":51.44,"linkquality":98,"temperature":39.3,"voltage":2900}
            Text(text = "Battery", fontSize = 24.sp)
            Text(text = "${snzb.battery} %", fontSize = 24.sp)
        }
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = "Voltage", fontSize = 24.sp)
            Text(text = "${snzb.voltage} mV", fontSize = 24.sp)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Link Quality", fontSize = 24.sp)
            Text(text = "${snzb.linkquality} lqi", fontSize = 24.sp)
        }

    }
}

@Composable
fun ConnectionBtn(connSettings: ConnectionSettings, topic: String, isConnected: MutableState<Boolean>){
    val context = LocalContext.current
    val mqttHandler = MqttHandler(context)
    val handler = Handler(Looper.getMainLooper())

    Button(
        onClick = {
            if (!connSettings.isValid()) {
                Toast.makeText(context, "Invalid connection settings", Toast.LENGTH_SHORT).show()
                return@Button
            }
            if(!isConnected.value) {
                mqttHandler.connect(
                    connSettings.getUri(),
                    connSettings.clientId,
                    onSuccess = {
                        mqttHandler.subscribe(topic)
                        isConnected.value = true
                        handler.post{
                            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onFailure = {
                        isConnected.value = false
                        handler.post{
                            Toast.makeText(context, "Failed to connect", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }else{
                mqttHandler.unsubscribe(topic)
                mqttHandler.disconnect(
                    onSuccess = {
                        isConnected.value = false
                        handler.post{
                            Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onFailure = {
                        isConnected.value = true
                        handler.post{
                            Toast.makeText(context, "Failed to disconnect", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    ) {
        Text(
            text = if (isConnected.value) "Disconnect" else "Connect"
        )
    }
}



@Composable
fun ConnectionProperties(
    connSettings: MutableState<ConnectionSettings>,
    topic: MutableState<String>,
    isConnected: Boolean
){
    OutlinedTextField(
        value = connSettings.value.server,
        label = {
                Text(text = "Server")
        },
        onValueChange = {
            connSettings.value = connSettings.value.copy(server = it)
        },
        enabled = !isConnected
    )
    OutlinedTextField(
        value = connSettings.value.port,
        label = {
                Text(text = "Port")
        },
        onValueChange = {
            connSettings.value = connSettings.value.copy(port = it)
        },
        enabled = !isConnected
    )
    OutlinedTextField(
        value = connSettings.value.clientId,
        label = {
            Text(text = "Client")
        },
        onValueChange = {
            connSettings.value = connSettings.value.copy(clientId = it)
        },
        enabled = !isConnected
    )
    OutlinedTextField(
        value = topic.value,
        label = {
            Text(text = "Topic")
        },
        onValueChange = {
            topic.value = it
        },
        enabled = !isConnected
    )


}