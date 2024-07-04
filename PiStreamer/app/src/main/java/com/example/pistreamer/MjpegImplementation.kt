package com.example.pistreamer

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import com.example.pistreamer.Utils.postToast
import com.longdo.mjpegviewer.MjpegViewError
import com.longdo.mjpegviewer.MjpegViewStateChangeListener
import java.io.IOException


class MjpegImplementation(private val context: Context): MjpegViewStateChangeListener {
    private val TAG = "MjpegImplementation"
    private var shouldCapturePhoto = false


    override fun onStreamDownloadStart() {

    }

    override fun onStreamDownloadStop() {

    }

    override fun onServerConnected() {

    }

    override fun onMeasurementChanged(rect: Rect?) {

    }

    override fun onNewFrame(image: Bitmap?) {
        image?.let {
            if(shouldCapturePhoto){
                savePhoto(it)
                shouldCapturePhoto = false
            }
        }
    }

    override fun onError(error: MjpegViewError?) {

    }

    private fun savePhoto(image: Bitmap){
        val photoCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            //Android 10
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        }else{
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val timestamp = System.currentTimeMillis()/1000
        val newImageDetails = ContentValues().apply {
            put(MediaStore.Images.Media.DATE_ADDED, timestamp)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        }
        val resolver = context.contentResolver
        val imageUri = resolver.insert(photoCollection, newImageDetails)


        if (imageUri != null) {
            try {
                resolver.openOutputStream(imageUri)?.use { outputStream ->
                    image.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                val updatedRows = resolver.update(imageUri, newImageDetails, null, null)
                val message = if(updatedRows > 0) "Saved" else "Not saved"
                postToast(context,message)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun capturePhoto(){
        shouldCapturePhoto = true
    }


}