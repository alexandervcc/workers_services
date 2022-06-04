package com.example.background.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import com.example.background.R
import com.example.background.services.EjemploServicio

class ServiceActivity : AppCompatActivity() {
    var servicio:EjemploServicio? = null

    lateinit var boton:Button
    lateinit var lista:ListView

    lateinit var frutas:ArrayList<String>
    lateinit var adapter:ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        boton = findViewById(R.id.buttonService01)
        lista = findViewById(R.id.listView01)

        bindiarServicio()

        frutas = ArrayList()
        adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,frutas)

        lista.adapter = adapter

        boton.setOnClickListener {
            obtenerDatosServicio()
        }
    }

    fun bindiarServicio(){
        bindService(Intent(this,EjemploServicio::class.java),serviceConnection, BIND_AUTO_CREATE)
    }

    fun obtenerDatosServicio(){
        if(servicio!=null){
            val listaFrutas : List<String>  = servicio!!.obtenerLista()
            frutas.clear()
            frutas.addAll(listaFrutas)
            adapter.notifyDataSetChanged()
        }
    }

    private val serviceConnection:ServiceConnection = object:ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            servicio = (p1 as EjemploServicio.CustomBinder).service
            Toast.makeText(this@ServiceActivity,"Conectado",Toast.LENGTH_SHORT).show()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            servicio = null
        }
    }

}