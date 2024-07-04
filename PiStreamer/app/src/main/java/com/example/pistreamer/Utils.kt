package com.example.pistreamer

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast


object Utils {
    private val handler = Handler(Looper.getMainLooper())

    fun postToast(context: Context, text: String){
        handler.post {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
        }
    }
}