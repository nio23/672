package com.example.zigbeemonitor

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import info.mqtt.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage

class MqttHandler(private val context: Context) {
    companion object{
        val messagesMap = mutableStateMapOf<String, String>()
    }
    private var client: MqttAndroidClient? = null

    fun connect(serverUri: String, clientId: String, onSuccess: () -> Unit, onFailure: ()-> Unit){
        createClient(serverUri, clientId)
        client?.connect(context, object : IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                onSuccess()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                onFailure()
            }

        })
    }
    fun disconnect(onSuccess: () -> Unit, onFailure: () -> Unit){
        client?.disconnect(context,  object : IMqttActionListener{
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                onSuccess()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?
            ) {
                onFailure()
            }

        })
    }
    private fun createClient(serverUri: String, clientId: String){
        client = MqttAndroidClient(context, serverUri, clientId).apply {
            setCallback(object : MqttCallback{
                override fun connectionLost(cause: Throwable?) {
                    Log.d("MqttHandler", "Client lost connection")
                }

                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val msg = String(message!!.payload)
                    Log.d("MqttHandler", "message from $topic>>$msg")
                    if (topic != null) {
                        messagesMap[topic] = msg
                    }
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    TODO("Not yet implemented")
                }

            })
        }
    }
    fun subscribe(topic: String){
        client?.subscribe(topic,1)
    }

    fun unsubscribe(topic: String){
        client?.unsubscribe(topic)
    }

    fun isConnected():Boolean{
        return client != null && client!!.isConnected
    }
}