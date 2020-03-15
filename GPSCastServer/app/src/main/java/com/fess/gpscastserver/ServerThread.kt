import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationManager
import android.os.Handler
import android.os.Looper
import com.fess.gpscastserver.MainActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import java.io.OutputStream
import java.lang.ref.WeakReference
import java.net.ServerSocket
import java.nio.charset.Charset
import java.util.*

class Wrapper(reference: WeakReference<MainActivity>, message: String) : Runnable {
    private val reference: WeakReference<MainActivity> = reference;
    private val text: String = message;

    override fun run() {
        val activity = reference.get()
        val logTextView = activity!!.logTextView
        logTextView.text = "${logTextView.text}\n$text"
    }
}

class ServerThread(reference: WeakReference<MainActivity>) :
    Thread() {

    private val gson = Gson()
    private val reference: WeakReference<MainActivity> = reference;
    private var knownLocation: Location? = Location(LocationManager.GPS_PROVIDER);

    private fun log(message: String) {

        val mainHandler = Handler(Looper.getMainLooper());

        val myRunnable = Wrapper(reference, message)
        mainHandler.post(myRunnable)
    }

    fun setLocation(location: Location?) {
        knownLocation = location;
    }

    @SuppressLint("MissingPermission")
    override fun run() {
        super.run()

        val serverSocket = ServerSocket(8080)
        log("Server is listening on port 8080")

        while (true) {
            val client = serverSocket.accept()

            log("New client connected")

            Thread {

                val reader = Scanner(client.getInputStream())
                val writer: OutputStream = client.getOutputStream()

                var text = ""
                do {
                    try {
                        text = reader.nextLine()
//                        var location =
//                            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)

                        writer.write((gson.toJson(knownLocation) + '\n').toByteArray(Charset.defaultCharset()))
                    } catch (exception: Exception) {
                        log("Exception: ${exception.message}")
                    }
                } while (text != "bye")

                log("Client dropped")
            }.start()
        }
    }
}