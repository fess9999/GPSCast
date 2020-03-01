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

        // setMock(10.0, 53.34, 5f);

        locationManager.addTestProvider (LocationManager.GPS_PROVIDER,
            "requiresNetwork" == "",
            "requiresSatellite" == "",
            "requiresCell" == "",
            "hasMonetaryCost" == "",
            "supportsAltitude" == "",
            "supportsSpeed" == "",
            "supportsBearing" == "",
            android.location.Criteria.POWER_LOW,
            android.location.Criteria.ACCURACY_FINE);

        /*locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

        locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER,
            LocationProvider.AVAILABLE,
            null,System.currentTimeMillis());*/
        //locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER,
          //  LocationProvider.AVAILABLE,
            //null,System.currentTimeMillis());

        try {
            val socket = Socket(address, 8080)
            println("Connected to $address")


        val reader: Scanner = Scanner(socket.getInputStream())
        val writer: OutputStream = socket.getOutputStream()
var counter = 0;
        while (true) {
            writer.write("gps\n".toByteArray())

            var serializedLocation = reader.nextLine()
            println(serializedLocation)

            if (serializedLocation != "null") {
                var location: Location = gson.fromJson(serializedLocation, Location::class.java)
//                println(location.latitude)

                setMock(counter.toDouble(), 53.34, 5f);

                counter += 1;
            }

                Thread.sleep(1000)
        }
        }
        catch (ex : Exception)
        {
            println(ex.message)
            //throw ex
        }
    }

    private fun setMock(latitude: Double, longitude: Double, accuracy: Float) {
        /*locationManager.addTestProvider (LocationManager.GPS_PROVIDER,
            "requiresNetwork" == "",
            "requiresSatellite" == "",
            "requiresCell" == "",
            "hasMonetaryCost" == "",
            "supportsAltitude" == "",
            "supportsSpeed" == "",
            "supportsBearing" == "",
            android.location.Criteria.POWER_LOW,
            android.location.Criteria.ACCURACY_FINE);
*/
        var newLocation = Location(LocationManager.GPS_PROVIDER);

        newLocation.latitude = latitude;
        newLocation.longitude = longitude;
        newLocation.accuracy = accuracy;
        newLocation.altitude = 0.0;
        // newLocation.accuracy = 500.0f;
        newLocation.time = System.currentTimeMillis();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        }
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);

        locationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER,
            LocationProvider.AVAILABLE,
            null,System.currentTimeMillis());

        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);
    }

    fun setAddress(address: String) {
        this.address = address
    }

}