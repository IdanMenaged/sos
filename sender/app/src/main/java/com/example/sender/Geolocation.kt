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
class Geolocation(context: Context) {
    private val TAG = "Geolocation"

    // todo: delete when gms definitely 100% works
    // using lolcation api
    /*
    private var locationByGps: Location? = null
    private var locationByNetwork: Location? = null

    @SuppressLint("MissingPermission")
    fun getLocation(): Location? {
        var currentLocation: Location? = null
        var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE)
                as LocationManager
        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        val gpsLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByGps = location
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }
        val networkLocationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationByNetwork = location
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (hasGps) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                0F,
                gpsLocationListener
            )
        }

        if (hasNetwork) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                5000,
                0F,
                networkLocationListener
            )
        }

        val lastKnownLocationByGps =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        lastKnownLocationByGps?.let {
            locationByGps = lastKnownLocationByGps
        }

        val lastKnownLocationByNetwork =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lastKnownLocationByNetwork?.let {
            locationByNetwork = lastKnownLocationByNetwork
        }

        if (locationByGps != null && locationByNetwork != null) {
            if (locationByGps!!.accuracy > locationByNetwork!!.accuracy) {
                currentLocation = locationByGps
            } else {
                currentLocation = locationByNetwork
            }
        }
        if (currentLocation != null) {
            Log.d("Geolocation", "lat: ${currentLocation.latitude} " +
                    "lon: ${currentLocation.longitude}")
        }
        else {
            Log.e("Geolocation", "location could not be retrieved")
        }
        return currentLocation
    }
    */

    // using fused location provider
    // todo: why work only sometimes??
    // todo: make service

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
            // todo: magic numbers
            interval = TimeUnit.SECONDS.toMillis(1) // todo: maybe make interval even shorter

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
                    // todo: show app as loading until location is cached
                    Log.d(TAG, "location cached")
                } ?: {
                    Log.d(TAG, "Location information isn't available.")
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    fun getLocation(): Location? {
        if (currentLocation == null) {
            Log.e(TAG, "error getting location")
        }
        else {
            Log.d(TAG, "${currentLocation!!.latitude},${currentLocation!!.longitude}")
        }
        return currentLocation
    }
}
