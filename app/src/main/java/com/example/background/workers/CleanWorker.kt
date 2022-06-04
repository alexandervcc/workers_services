package com.example.background.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.background.OUTPUT_PATH
import java.io.File

class CleanWorker(context: Context, p: WorkerParameters): Worker(context,p) {
    override fun doWork(): Result {
        makeStatusNotification("Limpieza de datos viejos",applicationContext)

        return try{
            val outDirectory = File(applicationContext.filesDir, OUTPUT_PATH)
            if(outDirectory.exists()){
                val entradas = outDirectory.listFiles()
                if(entradas!=null){
                    for(ent in entradas){
                        val nombre = ent.name
                        if(nombre.isNotEmpty() && nombre.endsWith(".png")){
                            val borrado = ent.delete()
                        }
                    }
                }
            }

            Result.success()
        }catch (throwable:Throwable){
            Result.failure()
        }
    }
}