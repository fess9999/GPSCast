package com.fess.gpscastclient

import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.os.SystemClock
import com.google.gson.Gson
import java.io.OutputStream
import java.lang.Exception
import java.net.Socket
import java.util.*
import kotlin.reflect.typeOf

class SocketThread(locationManager: LocationManager) : Thread() {

    private val locationManager: LocationManager = locationManager
    private lateinit var address: String
    private val gson: Gson = Gson()

    override fun run() {
        super.run()

        locationManager.addTestProvider(
            LocationManager.GPS_PROVIDER,
            "requiresNetwork" == "",
            "requiresSatellite" == "",
            "requiresCell" == "",
            "hasMonetaryCost" == "",
            "supportsAltitude" == "",
            "supportsSpeed" == "",
            "supportsBearing" == "",
            android.location.Criteria.POWER_LOW,
            android.location.Criteria.ACCURACY_FINE
        );


        val socket = Socket(address, 8080)
        println("Connected to $address")

        val reader = Scanner(socket.getInputStream())
        val writer: OutputStream = socket.getOutputStream()

        try {
            while (true) {
                writer.write("gps\n".toByteArray())

                var serializedLocation = reader.nextLine()
                println(serializedLocation)

                if (serializedLocation != "null") {
                    var location: Location = gson.fromJson(serializedLocation, Location::class.java)
                    setMock(location);
                }

                Thread.sleep(1000)
            }
        } catch (ex: Exception) {
            println(ex.message)
        }
    }

    private fun setMock(location: Location) {
        var newLocation = Location(LocationManager.GPS_PROVIDER);

        newLocation.latitude = location.latitude;
        newLocation.longitude = location.longitude;
        newLocation.accuracy = location.accuracy;
        newLocation.altitude = location.altitude;
        newLocation.time = location.time;
        newLocation.speed = location.speed;
        newLocation.extras = location.extras;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        }

        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

        locationManager.setTestProviderStatus(
            LocationManager.GPS_PROVIDER,
            LocationProvider.AVAILABLE,
            null,
            System.currentTimeMillis()
        );

        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);
    }

    fun setAddress(address: String) {
        this.address = address
    }

}