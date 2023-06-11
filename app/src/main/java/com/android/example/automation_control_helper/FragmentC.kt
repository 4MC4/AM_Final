package com.android.example.automation_control_helper

import android.Manifest
import android.content.pm.PackageManager
import android.location.LocationManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.IOException
import java.util.*


class FragmentC : Fragment() {

    private lateinit var btnGetLocation: Button
    private lateinit var tvLocation: TextView
    private lateinit var geocoder: Geocoder
    private lateinit var locationListener: LocationListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_c, container, false)

        btnGetLocation = view.findViewById(R.id.btn_get_location)
        tvLocation = view.findViewById(R.id.tv_location)
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude
                val locationText = "Latitude: $latitude, Longitude: $longitude"
                tvLocation.text = locationText

                try {
                    val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                    if (addresses != null && addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val addressText = "Address: ${address.getAddressLine(0)}"
                        tvLocation.append("\n$addressText")
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }

                getTemperatureFromApi(latitude, longitude)

                tvLocation.visibility = View.VISIBLE
            }

            override fun onProviderEnabled(provider: String) {}

            override fun onProviderDisabled(provider: String) {}

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        btnGetLocation.setOnClickListener {
            checkLocationPermission()
        }

        return view
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getLocation()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getLocation() {
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestSingleUpdate(
                LocationManager.GPS_PROVIDER,
                locationListener,
                null
            )
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun getTemperatureFromApi(latitude: Double, longitude: Double) {
        val apiUrl = "https://api.open-meteo.com/v1/forecast?latitude=$latitude&longitude=$longitude&current_weather=true"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, apiUrl, null,
            Response.Listener { response ->
                val temperature = response.getJSONObject("current_weather").getDouble("temperature")
                val temperatureText = "Temperature: $temperature °C"
                tvLocation.append("\n$temperatureText")
            },
            Response.ErrorListener { error ->
                error.printStackTrace()
            })

        Volley.newRequestQueue(requireContext()).add(jsonObjectRequest)
    }

    private fun displayLocation(latitude: Double, longitude: Double) {
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address = addresses[0]
                val addressText = "Address: ${address.getAddressLine(0)}"
                tvLocation.append("\n$addressText")
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }
}