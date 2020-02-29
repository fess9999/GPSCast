package com.fess.gpscastserver

import android.annotation.SuppressLint
import android.location.LocationManager
import com.google.gson.Gson
import org.json.JSONObject
import org.json.JSONStringer
import java.io.OutputStream
import java.net.Socket
import java.nio.charset.Charset
import java.util.*

class ClientHandler(client: Socket, locationManager: LocationManager?) {

    private val client: Socket = client
    private val locationManager: LocationManager? = locationManager
    private val reader: Scanner = Scanner(client.getInputStream())
    private val writer: OutputStream = client.getOutputStream()
    private var running: Boolean = false
    private val gson = Gson()

    @SuppressLint("MissingPermission")
    fun run() {

        running = true

        while (running) {
            try {

                val text = reader.nextLine()
                println("Client said $text")

                var location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                println("Location: $location");
                writer.write((gson.toJson(location) + '\n').toByteArray(Charset.defaultCharset()))

                if (text == "EXIT"){
                    shutdown()
                    continue
                }
            } catch (ex: Exception) {
                // TODO: Implement exception handling
                println(ex.message);
                shutdown()
            } finally {

            }

        }
    }

    fun shutdown() {
        running = false
        client.close()
        println("${client.inetAddress.hostAddress} closed the connection")
    }

}
