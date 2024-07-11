package com.example.zigbeemonitor

data class ConnectionSettings(
    val server: String = "192.168.10.61",
    val port: String = "1883",
    val clientId: String = "AndroidClient"
){
    fun getUri():String{
        return "tcp://$server:$port"
    }

    fun isValid(): Boolean{
        return server.isNotBlank() && port.isNotBlank() && clientId.isNotBlank()
    }
}
