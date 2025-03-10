import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.concurrent.TimeUnit


@SuppressLint("MissingPermission")
class Geolocation(context: Context, private val onReady: () -> Unit) {
    private val TAG = "Geolocation"

    // using fused location provider

    // FusedLocationProviderClient - Main class for receiving location updates.
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    // LocationRequest - Requirements for the location updates, i.e.,
// how often you should receive updates, the priority, etc.
    private lateinit var locationRequest: LocationRequest

    // LocationCallback - Called when FusedLocationProviderClient
// has a new Location
    private lateinit var locationCallback: LocationCallback

    // This will store current location info
    private var currentLocation: Location? = null

    init {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        locationRequest = LocationRequest().apply {
            // Sets the desired interval for
            // active location updates.
            // This interval is inexact.
            interval = TimeUnit.SECONDS.toMillis(1)

            // Sets the fastest rate for active location updates.
            // This interval is exact, and your application will never
            // receive updates more frequently than this value
            fastestInterval = TimeUnit.SECONDS.toMillis(1)

            // Sets the maximum time when batched location
            // updates are delivered. Updates may be
            // delivered sooner than this interval
            maxWaitTime = TimeUnit.SECONDS.toMillis(2)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                p0.lastLocation?.let { location ->
                    currentLocation = location
                    Log.d(TAG, "location cached")
                    onReady()
                } ?: {
                    Log.d(TAG, "Location information isn't available.")
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    fun getLocation(): Location {
        Log.d(TAG, "${currentLocation!!.latitude},${currentLocation!!.longitude}")
        return currentLocation as Location
    }
}
