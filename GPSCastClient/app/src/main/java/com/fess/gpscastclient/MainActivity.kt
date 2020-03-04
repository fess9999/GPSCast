package com.fess.gpscastclient

import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    private lateinit var socketThread : SocketThread

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun connectButtonClick(view: View)
    {
        socketThread = SocketThread(getSystemService(LOCATION_SERVICE) as LocationManager, WeakReference(this))
        socketThread.setAddress(this.ip_editText.text.toString())
        socketThread.start()
    }
}
