package com.example.background.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.text.TextUtils
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.background.KEY_IMAGE_URI


class BlurWorker(context:Context,p:WorkerParameters): Worker(context,p) {
    override fun doWork(): Result {
        val apCtx = applicationContext

        val resUri = inputData.getString(KEY_IMAGE_URI)

        makeStatusNotification("Blur Imagen Iniciado",apCtx)

        sleep()

        return try{
//            val imagen = BitmapFactory.decodeResource(apCtx.resources, R.drawable.android_cupcake)

            if(TextUtils.isEmpty(resUri)){
                makeStatusNotification("URI equivocada",apCtx)
                throw IllegalArgumentException("URI invalida")
            }

            val resolver = apCtx.contentResolver
            val imagen = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resUri))
            )

            val imagenActualizada = blurBitmap(imagen,apCtx)

            val imagenURI = writeBitmapToFile(apCtx,imagenActualizada)

            val dataSalida = workDataOf(KEY_IMAGE_URI to imagenURI.toString())

            makeStatusNotification("Blur Finalizado",apCtx)
            Result.success(dataSalida)
        }catch (t:Throwable){
            makeStatusNotification("Blur ha Fallado",apCtx)
            Result.failure()
        }

    }
}