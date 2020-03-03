package com.fess.gpscastclient

import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var socketThread : SocketThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        socketThread = SocketThread(getSystemService(LOCATION_SERVICE) as LocationManager)
    }

    fun connectButtonClick(view: View)
    {
        socketThread.setAddress(this.ip_editText.text.toString())
        socketThread.start()
    }
}
