package com.example.androidasyhc

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.local_server.LocalServer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val server = LocalServer(this)
        server.start()

        val addresses = server.addresses

        if (addresses.isNotEmpty()){
            var string = "Server Addresses : \n"
            for ( i in addresses)
                string+=i
            et_address.text = string
        }
        else
            et_address.text = "Device not connected to a Private network Please turn on WIFI of Hotspot and launch the app"
    }
}