import android.annotation.SuppressLint
import android.location.LocationManager
import android.os.Handler
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.ServerSocket
import java.nio.charset.Charset
import java.util.*

class ServerThread(locationManager: LocationManager) : Thread() {
    private val locationManager: LocationManager = locationManager
    private val gson = Gson()

    @SuppressLint("MissingPermission")
    override fun run() {
        super.run()

        val serverSocket = ServerSocket(8080)
        println("Server is listening on port 8080")
        while (true) {
            val client = serverSocket.accept()
            println("New client connected")

            Thread {

                val reader = Scanner(client.getInputStream())
                val writer: OutputStream = client.getOutputStream()

                var text: String
                try {
                    do {
                        text = reader.nextLine()
                        var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                        println("Location: $location");
                        writer.write((gson.toJson(location) + '\n').toByteArray(Charset.defaultCharset()))

                    } while (text != "bye")
                } catch (exception: Exception) {
                    writer.close()
                }

                println("Client dropped")
            }.start()
        }
    }
}