package com.fess.gpscastserver

import ServerThread
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import java.lang.ref.WeakReference


class BackgroundService : Service() {

    inner class LocationListener(provider: String?) : android.location.LocationListener {

        private val lastLocation: Location? = null
        private val TAG = "LocationListener"
        private var mLastLocation: Location? = Location(provider)

        override fun onLocationChanged(location: Location?) {
            mLastLocation = location;
            server.setLocation(location);
            Log.i(TAG, "LocationChanged: $location");
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
            Log.e(TAG, "onStatusChanged: $status");
        }

        override fun onProviderEnabled(provider: String?) {
            Log.e(TAG, "onProviderEnabled: $provider");
        }

        override fun onProviderDisabled(provider: String?) {
            Log.e(TAG, "onProviderDisabled: $provider");
        }

    }

    inner class LocationServiceBinder : Binder() {
        val service: BackgroundService = this@BackgroundService
    }

    private lateinit var server: ServerThread
    private val binder: LocationServiceBinder = LocationServiceBinder()
    private val TAG = "BackgroundService"
    private var mLocationListener: LocationListener? = null
    private var mLocationManager: LocationManager? = null
    private val notificationManager: NotificationManager? = null

    private val LOCATION_INTERVAL = 500
    private val LOCATION_DISTANCE = 10

    override fun onBind(intent: Intent?): IBinder? {
        return binder;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    override fun onCreate() {
        Log.i(TAG, "onCreate");
        startForeground(12345678, getNotification());
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mLocationManager != null) {
            try {
                mLocationManager!!.removeUpdates(mLocationListener)
            } catch (ex: Exception) {
                Log.i(TAG, "fail to remove location listeners, ignore", ex)
            }
        }
    }

    private fun initializeLocationManager() {
        if (mLocationManager == null) {
            mLocationManager =
                applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    fun startTracking(reference: WeakReference<MainActivity>) {
        server = ServerThread(reference);
        initializeLocationManager()
        mLocationListener = LocationListener(LocationManager.GPS_PROVIDER)
        try {
            mLocationManager!!.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                LOCATION_INTERVAL.toLong(),
                LOCATION_DISTANCE.toFloat(),
                mLocationListener
            )

            server.start();
        } catch (ex: SecurityException) { // Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (ex: IllegalArgumentException) { // Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    fun stopTracking() {
        onDestroy()
    }

    private fun getNotification(): Notification? {
        val channel =
            NotificationChannel("channel_01", "My Channel", NotificationManager.IMPORTANCE_DEFAULT)
        val notificationManager = getSystemService(
            NotificationManager::class.java
        )
        notificationManager.createNotificationChannel(channel)
        val builder: Notification.Builder =
            Notification.Builder(applicationContext, "channel_01").setAutoCancel(true)
        return builder.build()
    }


}