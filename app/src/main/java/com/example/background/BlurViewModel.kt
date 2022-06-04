/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.background

import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.background.workers.BlurWorker
import com.example.background.workers.CleanWorker
import com.example.background.workers.SaveWorker


class BlurViewModel(application: Application) : ViewModel() {

    internal var imageUri: Uri? = null
    internal var outputUri: Uri? = null
    internal val outWorkData:LiveData<List<WorkInfo>>

    private val workManager = WorkManager.getInstance(application)

    init {
        imageUri = getImageUri(application.applicationContext)

        outWorkData = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
    }

    //estados del workrequest ->WorkIno: BLOCKED, CANCELLED, ENQUEED, FAILED, RUNNING, SUCCEDED


    internal fun applyBlur(blurLevel: Int) {
        var wRequestChain = workManager
            .beginUniqueWork(
                IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequest.from(CleanWorker::class.java)
            )

        for(i in 0 until blurLevel){
            val blurBuilder = OneTimeWorkRequestBuilder<BlurWorker>()
            if(i==0){
                blurBuilder.setInputData(crearInputDatosUri())
            }
            wRequestChain = wRequestChain.then(blurBuilder.build())
        }


//        val blurRequest = OneTimeWorkRequestBuilder<BlurWorker>()
//            .setInputData(crearInputDatosUri())
//            .build()
//        wRequestChain = wRequestChain.then(blurRequest)

        val wSave = OneTimeWorkRequest.Builder(SaveWorker::class.java).build()
        val wSave2 = OneTimeWorkRequestBuilder<SaveWorker>()
            .addTag(TAG_OUTPUT)
            .build()

        wRequestChain = wRequestChain.then(wSave)

        wRequestChain.enqueue()

        //UNICO WORKER CON PASO DE DATOS
//        val blurRequest = OneTimeWorkRequestBuilder<BlurWorker>()
//            .setInputData(crearInputDatosUri())
//            .build()
//        workManager.enqueue(blurRequest)

        //UNICO WORKER
        //workManager.enqueue(OneTimeWorkRequest.from(BlurWorker::class.java))
    }

    private fun uriOrNull(uriString: String?): Uri? {
        return if (!uriString.isNullOrEmpty()) {
            Uri.parse(uriString)
        } else {
            null
        }
    }

    private fun getImageUri(context: Context): Uri {
        val resources = context.resources

        val imageUri = Uri.Builder()
            .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
            .authority(resources.getResourcePackageName(R.drawable.android_cupcake))
            .appendPath(resources.getResourceTypeName(R.drawable.android_cupcake))
            .appendPath(resources.getResourceEntryName(R.drawable.android_cupcake))
            .build()

        return imageUri
    }

    internal fun setOutputUri(outputImageUri: String?) {
        outputUri = uriOrNull(outputImageUri)
    }

    private fun crearInputDatosUri():Data{
        val builder = Data.Builder()
        imageUri.let {
            builder.putString(KEY_IMAGE_URI,imageUri.toString())
        }
        return builder.build()
    }



    class BlurViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(BlurViewModel::class.java)) {
                BlurViewModel(application) as T
            } else {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}