package com.fess.gpscastclient

import android.location.Location
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.OutputStream
import java.lang.Exception
import java.lang.ref.WeakReference
import java.net.Socket
import java.util.*
import kotlin.reflect.typeOf

class Wrapper(reference: WeakReference<MainActivity>, message: String) : Runnable
{
    private val reference: WeakReference<MainActivity> = reference;
    private val text : String = message;

    override fun run() {
        val activity = reference.get()
        val logTextView = activity!!.logTextView
        logTextView.text = "${logTextView.text}\n$text"
    }
}
class SocketThread(locationManager: LocationManager, reference: WeakReference<MainActivity>) : Thread() {

    private val locationManager: LocationManager = locationManager
    private lateinit var address: String
    private val gson: Gson = Gson()
    private val reference: WeakReference<MainActivity> = reference;

    private fun log(message: String) {

        val mainHandler = Handler(Looper.getMainLooper());

        val myRunnable = Wrapper(reference, message)
        mainHandler.post(myRunnable)

        /*val activity = reference.get()
        val logTextView = activity!!.logTextView
        logTextView.text = "${logTextView.text}\n${message}"*/
    }

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

        var socket: Socket? = null;

        try {

            log("Connecting to $address ...")
            socket = Socket(address, 8080)
            log("Connected to $address")
        }
        catch (exception: Exception)
        {
            log("Exception ${exception.message}")
            return;
        }

        val reader = Scanner(socket!!.getInputStream())
        val writer: OutputStream = socket.getOutputStream()

        try {
            while (true) {
                log("Sending...")
                writer.write("gps\n".toByteArray())
                log("Sent")

                var serializedLocation = reader.nextLine()

                if (serializedLocation != "null") {

                    var location: Location = gson.fromJson(serializedLocation, Location::class.java)
                    log("Location ${location.latitude} ${location.longitude} ${location.speed}")

                    setMock(location);
                }
                else {
                    log("null location")
                }

                sleep(1000)
            }
        } catch (ex: Exception) {
            log("Exception ${ex.message}")
        }
    }

    private fun setMock(location: Location) {
        log("1")
        var newLocation = Location(LocationManager.GPS_PROVIDER);

        newLocation.latitude = location.latitude;
        newLocation.longitude = location.longitude;
        //newLocation.accuracy = location.accuracy;
        //newLocation.altitude = location.altitude;
        newLocation.time = System.currentTimeMillis();
        //newLocation.time = location.time;
        //newLocation.speed = location.speed;
        //newLocation.extras = location.extras;
        log("2")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            newLocation.elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        }
        log("3")
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        log("4")
        locationManager.setTestProviderStatus(
            LocationManager.GPS_PROVIDER,
            LocationProvider.AVAILABLE,
            null,
            System.currentTimeMillis()
        );
        log("5")
        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, newLocation);
        log("6")
    }

    fun setAddress(address: String) {
        this.address = address
    }

}