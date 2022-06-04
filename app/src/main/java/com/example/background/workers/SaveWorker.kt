package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI
import com.example.background.OUTPUT_PATH
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SaveWorker(context: Context, p: WorkerParameters): Worker(context,p) {

    private val titulo = "ImageNueva"
    private val dateFormatter = SimpleDateFormat(
        "yyyy.MM.dd 'at' HH.mm.ss z", Locale.getDefault()
    )

    override fun doWork(): Result {
        makeStatusNotification("Guardando Image",applicationContext)

        val resolver = applicationContext.contentResolver

        return try{
            val uriImagen = inputData.getString(KEY_IMAGE_URI)

            val imageBitmap = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(uriImagen))
            )
            val imageURL = MediaStore.Images.Media.insertImage(
                resolver,imageBitmap,titulo,dateFormatter.format(Date())
            )
            if(!imageURL.isNullOrEmpty()){
                val out = workDataOf(KEY_IMAGE_URI to imageURL)
                Result.success(out)
            }else{
                Result.failure()
            }

        }catch (throwable:Throwable){
            Result.failure()
        }
    }
}