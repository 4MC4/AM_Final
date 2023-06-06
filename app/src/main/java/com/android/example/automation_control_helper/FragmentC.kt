package com.android.example.automation_control_helper

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class FragmentC : Fragment() {

    private lateinit var btnGetLocation: Button
    private lateinit var tvLocation: TextView
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_c, container, false)

        btnGetLocation = view.findViewById(R.id.btn_get_location)
        tvLocation = view.findViewById(R.id.tv_location)

        locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        btnGetLocation.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Jeśli mamy zgodę na dostęp do lokalizacji, pobieramy ją
                getLocation()
            } else {
                // Jeśli nie mamy zgodę na dostęp do lokalizacji, wyświetlamy prośbę o zgodę
                requestPermissions(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }

        return view
    }

    private fun getLocation() {
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude
                val locationText = "Latitude: $latitude, Longitude: $longitude"
                tvLocation.text = locationText
                tvLocation.visibility = View.VISIBLE
            }

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        // Uzyskaj dostęp do lokalizacji z GPS lub sieci
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Pobierz ostatnią znaną lokalizację
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                locationListener.onLocationChanged(lastKnownLocation)
            }

            // Zarejestruj nasłuchiwanie na aktualizacje lokalizacji
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BETWEEN_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                        grantResults[1] == PackageManager.PERMISSION_GRANTED)
            ) {
                // Jeśli użytkownik udzielił zgody, pobieramy lokalizację
                getLocation()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.removeUpdates(locationListener)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val MIN_TIME_BETWEEN_UPDATES: Long = 5000 // 5 sekund
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 metrów
    }
}