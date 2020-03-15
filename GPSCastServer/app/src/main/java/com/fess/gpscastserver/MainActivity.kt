package com.fess.gpscastserver

import ServerThread
import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.fess.gpscastserver.BackgroundService.LocationServiceBinder
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var reference = WeakReference<MainActivity>(this);
        val serverThread =
            ServerThread(getSystemService(LOCATION_SERVICE) as LocationManager, reference)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0);
        }

        // serverThread.start()
        val intent = Intent(this.application, BackgroundService::class.java)
        this.application.startService(intent)
        this.application
            .bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)

//        gpsService!!.startTracking();
//        mTracking = true;
    }

    private val serviceConnection: ServiceConnection = BackGroundServiceConnection()
    var gpsService: BackgroundService? = null
    var mTracking = false

    inner class BackGroundServiceConnection : ServiceConnection {

        override fun onServiceDisconnected(className: ComponentName?) {
            if (className!!.className == "BackgroundService") {
                gpsService = null;
            }
        }

        override fun onServiceConnected(className: ComponentName?, service: IBinder?) {
            val name: String = className!!.className;
            if (name.endsWith("BackgroundService")) {
                gpsService = (service as LocationServiceBinder).service
                gpsService!!.startTracking();
                mTracking = true;
                //btnStartTracking.setEnabled(true)
                //txtStatus.setText("GPS Ready")
            }
        }

    }
}



