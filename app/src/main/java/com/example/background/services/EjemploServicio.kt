package com.example.background.services

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import java.util.*
import kotlin.collections.ArrayList

class EjemploServicio : Service() {
    var timer: Timer?=null
    var binder:IBinder = CustomBinder()

    lateinit var lista:ArrayList<String>
    var array = arrayOf("Pera","Manzana","Platano","Limon","Aguacate")
    var pos = 0

    //Inicia la carga de trabajo
    override fun onCreate() {
        super.onCreate()
        pos = 0
        timer = Timer()
        lista = ArrayList()
        updataListFrutas()//carga de trabajo
    }

    private fun updataListFrutas(){
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if(lista.size>=array.size){
                    lista.removeAt(0)
                }
                lista.add(array[pos%array.size])//modulo para que no busque indices que lo desborder
                pos++
            }
        },0,5000)
    }

    fun obtenerLista():ArrayList<String>{
        return lista
    }

//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        return super.onStartCommand(intent, flags, startId)
//    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

    //CLASE ANIDADA INTERNA
    internal inner class CustomBinder:Binder(){
        val service:EjemploServicio
            get() = this@EjemploServicio
    }
}